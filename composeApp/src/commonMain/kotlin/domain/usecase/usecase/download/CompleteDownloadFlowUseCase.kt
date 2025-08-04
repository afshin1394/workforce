package domain.usecase.usecase.download

import data.network.response.download.ChunkStepData
import data.network.response.download.TaskChunkData
import data.network.response.download.TaskPropertyData
import data.network.response.task.task.InstanceTicketsBasicInformationValues
import data.network.response.task.task.InstanceTicketsProperties
import database.entity.ActivityListEntity
import database.entity.InitialFormEntity
import database.entity.StepPointerEntity
import database.entity.StepsEntity
import database.entity.TaskEntity
import domain.mappers.toFormStructDomain
import domain.models.steps.StepDetailDomain
import domain.repository.IDownloadRepository
import domain.repository.IInitialFormRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import utils.LoggingConfig

data class DownloadProgressUpdate(
    val currentChunk: Int,
    val totalChunks: Int,
    val currentTasksProcessed: Int,
    val totalTasks: Int,
    val message: String,
    val isCompleted: Boolean = false,
    val error: String? = null
)

class CompleteDownloadFlowUseCase(
    private val downloadRepository: IDownloadRepository,
    private val taskRepository: ITaskRepository,
    private val initialFormRepository: IInitialFormRepository,
    private val stepsRepository: IStepsRepository,
    private val stepPointerRepository: IStepPointerRepository
) : BaseUseCase<Flow<DownloadProgressUpdate>, Unit>() {
    
    override suspend fun run(params: Unit): Flow<DownloadProgressUpdate> = flow {
        
        try {
            // Step 1: Start download process
            LoggingConfig.logUseCase("CompleteDownloadFlow", "Starting complete download flow")
            emit(DownloadProgressUpdate(
                currentChunk = 0,
                totalChunks = 0,
                currentTasksProcessed = 0,
                totalTasks = 0,
                message = "Starting download process..."
            ))
            
            val startResponse = downloadRepository.startDownload()
            val jobId = startResponse.jobId
            
            // Step 2: Get first chunk to determine total parts
            var chunkIndex = 0
            var chunkResponse = waitForChunkCompletion(
                jobId = jobId,
                chunkIndex = chunkIndex,
                totalParts = 0,
                message = "Waiting for server to prepare data..."
            ) { progressUpdate ->
                emit(progressUpdate)
            }
            
            // Check for errors in the response
            if (chunkResponse.hasError()) {
                throw Exception("API Error: ${chunkResponse.error}")
            }
            
            // Get total parts from response (should be available after waiting for completion)
            val totalParts = chunkResponse.totalParts ?: throw Exception("totalParts is null in chunk response - response may still be pending")
            var totalTasksProcessed = 0
            
            LoggingConfig.logUseCase("CompleteDownloadFlow", "Total parts to download: $totalParts")
            
            // Get list of modified tasks to preserve them
            emit(DownloadProgressUpdate(
                currentChunk = 0,
                totalChunks = totalParts,
                currentTasksProcessed = 0,
                totalTasks = 0,
                message = "Checking for modified tasks..."
            ))
            
            val modifiedTicketNumbers = stepPointerRepository.getModifiedTicketNumbers()
            LoggingConfig.logUseCase("CompleteDownloadFlow", "Found ${modifiedTicketNumbers.size} modified tasks to preserve")
            
            // Track all ticket numbers from chunks for cleanup
            val downloadedTicketNumbers = mutableSetOf<String>()
            
            // Clear existing data except for modified tasks
            emit(DownloadProgressUpdate(
                currentChunk = 0,
                totalChunks = totalParts,
                currentTasksProcessed = 0,
                totalTasks = 0,
                message = "Clearing unmodified data..."
            ))
            
            if (modifiedTicketNumbers.isNotEmpty()) {
                // Delete only unmodified tasks and related data
                taskRepository.deleteAllExcept(modifiedTicketNumbers)
                initialFormRepository.deleteAllExcept(modifiedTicketNumbers)
                stepsRepository.deleteAllExcept(modifiedTicketNumbers)
                stepPointerRepository.deleteAllExcept(modifiedTicketNumbers)
            } else {
                // No modified tasks, safe to delete all
                taskRepository.deleteAll()
                initialFormRepository.deleteAll()
                stepsRepository.deleteAll()
                stepPointerRepository.deleteAll(emptyList())
            }
            
            // Step 3: Process chunks incrementally to avoid OOM
            for (currentChunk in 0 until totalParts) {
                emit(DownloadProgressUpdate(
                    currentChunk = currentChunk,
                    totalChunks = totalParts,
                    currentTasksProcessed = totalTasksProcessed,
                    totalTasks = 0,
                    message = "Downloading chunk ${currentChunk + 1} of $totalParts..."
                ))
                
                val currentChunkResponse = if (currentChunk == 0) {
                    chunkResponse // Use the first chunk we already got
                } else {
                    waitForChunkCompletion(
                        jobId = jobId,
                        chunkIndex = currentChunk,
                        totalParts = totalParts,
                        message = "Downloading chunk ${currentChunk + 1} of $totalParts..."
                    ) { progressUpdate ->
                        emit(progressUpdate)
                    }
                }
                
                // Process current chunk immediately to save memory
                val chunkTasks = currentChunkResponse.data
                if (chunkTasks.isNotEmpty()) {
                    emit(DownloadProgressUpdate(
                        currentChunk = currentChunk + 1,
                        totalChunks = totalParts,
                        currentTasksProcessed = totalTasksProcessed,
                        totalTasks = 0,
                        message = "Processing ${chunkTasks.size} tasks from chunk ${currentChunk + 1}..."
                    ))
                    
                    // Track ticket numbers from this chunk
                    chunkTasks.forEach { taskData ->
                        downloadedTicketNumbers.add(taskData.instanceTicketsNumber)
                    }
                    
                    // Process this chunk's data immediately, skipping modified tasks
                    val processedCount = processChunkData(chunkTasks, currentChunk + 1, totalParts, totalTasksProcessed, modifiedTicketNumbers)
                    totalTasksProcessed += processedCount
                    
                    // Clear chunk data from memory
                    emit(DownloadProgressUpdate(
                        currentChunk = currentChunk + 1,
                        totalChunks = totalParts,
                        currentTasksProcessed = totalTasksProcessed,
                        totalTasks = 0,
                        message = "Saved $processedCount tasks from chunk ${currentChunk + 1}. Total: $totalTasksProcessed"
                    ))
                }
                
                // Clear variables to help with garbage collection
                // Note: Explicit GC calls are not available in Kotlin multiplatform
            }
            
            LoggingConfig.logUseCase("CompleteDownloadFlow", "Downloaded $totalTasksProcessed total tasks")
            
            // Step 4: Clean up tasks that are not in downloaded chunks but exist in database
            emit(DownloadProgressUpdate(
                currentChunk = totalParts,
                totalChunks = totalParts,
                currentTasksProcessed = totalTasksProcessed,
                totalTasks = totalTasksProcessed,
                message = "Cleaning up obsolete tasks..."
            ))
            
            val tasksToKeep = downloadedTicketNumbers + modifiedTicketNumbers
            cleanupObsoleteTasks(tasksToKeep.toList())
            
            // Step 5: Complete
            emit(DownloadProgressUpdate(
                currentChunk = totalParts,
                totalChunks = totalParts,
                currentTasksProcessed = totalTasksProcessed,
                totalTasks = totalTasksProcessed,
                message = "Download completed successfully!",
                isCompleted = true
            ))
            
            LoggingConfig.logUseCase("CompleteDownloadFlow", 
                "Download flow completed successfully - Total Tasks: $totalTasksProcessed")
            
        } catch (e: Exception) {
            Napier.e("Download flow failed", e)
            emit(DownloadProgressUpdate(
                currentChunk = 0,
                totalChunks = 0,
                currentTasksProcessed = 0,
                totalTasks = 0,
                message = "Download failed",
                error = e.message
            ))
        }
    }
    
    /**
     * Process a single chunk's data immediately to save memory
     * Returns the number of tasks actually processed (skipping modified ones)
     */
    private suspend fun processChunkData(
        chunkTasks: List<TaskChunkData>,
        currentChunk: Int,
        totalParts: Int,
        currentTasksProcessed: Int,
        modifiedTicketNumbers: List<String>
    ): Int {
        if (chunkTasks.isEmpty()) return 0
        
        // Filter out tasks that have been modified
        val tasksToProcess = chunkTasks.filter { taskData ->
            !modifiedTicketNumbers.contains(taskData.instanceTicketsNumber)
        }
        
        if (tasksToProcess.isEmpty()) {
            LoggingConfig.logUseCase("CompleteDownloadFlow", 
                "Chunk $currentChunk: All ${chunkTasks.size} tasks are modified, skipping processing")
            return 0
        }
        
        if (tasksToProcess.size != chunkTasks.size) {
            LoggingConfig.logUseCase("CompleteDownloadFlow", 
                "Chunk $currentChunk: Skipping ${chunkTasks.size - tasksToProcess.size} modified tasks, processing ${tasksToProcess.size}")
        }
        
        // Convert tasks to entities (only unmodified tasks)
        val taskEntities = tasksToProcess.map { taskData ->
            // Convert TaskPropertyData to InstanceTicketsProperties
            val properties = taskData.instanceTicketsProperties?.map { prop ->
                InstanceTicketsProperties(
                    key = prop.key,
                    value = prop.values
                )
            } ?: emptyList()
            
            TaskEntity(
                ticket_id = 0, // Let Room auto-generate the ID
                ticket_type_id = taskData.instanceTicketsTicketId.toInt(), // Store the original ticket ID here
                instancePrefix = taskData.instanceTicketsNumber, // Use ticket number as instance prefix
                ticket_number = taskData.instanceTicketsNumber,
                ticket_state = taskData.instanceTicketsState,
                activity_id = taskData.activityId,
                activity__title = taskData.activityTitle,
                properties = properties
            )
        }
        
        // Insert tasks incrementally
        taskRepository.insertAll(taskEntities)
        
        // Process initial forms from this chunk (only unmodified tasks)
        val initialFormEntities = tasksToProcess.mapNotNull { taskData ->
            taskData.initStructure?.let { initialFormJson ->
                // Convert basic information values to InstanceTicketsBasicInformationValues
                val basicInfoValues = taskData.instanceTicketsBasicInformationValues?.map { prop ->
                    InstanceTicketsBasicInformationValues(
                        key = prop.key,
                        value = prop.values
                    )
                } ?: emptyList()
                
                InitialFormEntity(
                    ticket_number = taskData.instanceTicketsNumber,
                    initForms = basicInfoValues,
                    initFormJson = initialFormJson.toString()
                )
            }
        }
        
        // Insert initial forms incrementally
        if (initialFormEntities.isNotEmpty()) {
            initialFormRepository.insertAll(initialFormEntities)
        }
        
        // Process steps from this chunk (only unmodified tasks)
        val stepsEntities = tasksToProcess.flatMap { taskData ->
            taskData.steps?.map { stepData ->
                // Convert ChunkStepData to StepDetailDomain
                val stepDetailDomain = stepData.toStepDetailDomain()
                
                // Convert to StepsEntity
                StepsEntity(
                    ticketNumber = taskData.instanceTicketsNumber,
                    activityId = stepDetailDomain.activity_id,
                    formStructure = stepDetailDomain.form_structure.toString(),
                    // Fields from StepDetailDomain
                    activityTitle = stepDetailDomain.activity_title,
                    activityProcessId = stepDetailDomain.activity_process_id,
                    activityTaskGroup = stepDetailDomain.activity_task_group,
                    activityKind = stepDetailDomain.activity_kind,
                    activityForm = stepDetailDomain.activity_form,
                    workflowActivityTagsId = stepDetailDomain.workflow_activity_tags_id,
                    formName = stepDetailDomain.form_name
                )
            } ?: emptyList()
        }
        
        // Insert steps incrementally
        if (stepsEntities.isNotEmpty()) {
            stepsRepository.insertAll(stepsEntities)
        }
        
        // Process activity list from this chunk's steps (only unmodified tasks)
        val activityEntities = tasksToProcess.flatMap { taskData ->
            taskData.steps?.map { stepData ->
                ActivityListEntity(
                    id = stepData.activityId,
                    title = stepData.activityTitle,
                    instancePrefix = taskData.instanceTicketsNumber
                )
            } ?: emptyList()
        }.distinctBy { it.id } // Remove duplicates by activity ID
        
        // Insert activity list entities incrementally
        if (activityEntities.isNotEmpty()) {
            taskRepository.insertAllActivityList(activityEntities)
        }
        
        // Create StepPointer entities for each task (initialize with first activity, only unmodified tasks)
        val stepPointerEntities = tasksToProcess.map { taskData ->
            // Find the first activity ID for this task (initial step)
            val firstActivityId = taskData.steps?.firstOrNull()?.activityId ?: taskData.activityId
            
            StepPointerEntity(
                pk = 0, // Let Room auto-generate
                ticketNumber = taskData.instanceTicketsNumber,
                activeActivity = firstActivityId,
                edited = false // Initially not edited
            )
        }
        
        // Insert step pointers incrementally
        if (stepPointerEntities.isNotEmpty()) {
            stepPointerRepository.insertAll(stepPointerEntities)
        }
        
        LoggingConfig.logUseCase("CompleteDownloadFlow", 
            "Chunk $currentChunk processed: ${taskEntities.size} tasks, ${initialFormEntities.size} forms, ${stepsEntities.size} steps, ${activityEntities.size} activities, ${stepPointerEntities.size} step pointers")
        
        return tasksToProcess.size
    }
    
    /**
     * Clean up tasks that are not in the downloaded chunks or modified
     */
    private suspend fun cleanupObsoleteTasks(tasksToKeep: List<String>) {
        try {
            // Get all existing task ticket numbers from database
            val allExistingTasks = taskRepository.getAll()
            val existingTicketNumbers = allExistingTasks.map { it.ticket_number }
            
            // Find tasks that should be deleted (exist in DB but not in chunks or modified)
            val tasksToDelete = existingTicketNumbers.filter { ticketNumber ->
                !tasksToKeep.contains(ticketNumber)
            }
            
            if (tasksToDelete.isNotEmpty()) {
                LoggingConfig.logUseCase("CompleteDownloadFlow", 
                    "Cleaning up ${tasksToDelete.size} obsolete tasks that are no longer available")
                
                // Delete obsolete tasks and related data
                taskRepository.deleteSpecific(tasksToDelete)
                initialFormRepository.deleteSpecific(tasksToDelete)
                stepsRepository.deleteSpecific(tasksToDelete)
                stepPointerRepository.deleteSpecific(tasksToDelete)
                
                LoggingConfig.logUseCase("CompleteDownloadFlow", 
                    "Successfully cleaned up obsolete tasks: ${tasksToDelete.joinToString(", ")}")
            } else {
                LoggingConfig.logUseCase("CompleteDownloadFlow", "No obsolete tasks found for cleanup")
            }
        } catch (e: Exception) {
            LoggingConfig.logUseCase("CompleteDownloadFlow", "Error during cleanup: ${e.message}")
            // Don't fail the entire download process if cleanup fails
        }
    }
    
    /**
     * Wait for chunk completion with 5-second intervals for PENDING status
     * Retries indefinitely until data is received or an error occurs
     */
    private suspend fun waitForChunkCompletion(
        jobId: String,
        chunkIndex: Int,
        totalParts: Int,
        message: String,
        onProgress: suspend (DownloadProgressUpdate) -> Unit
    ): data.network.response.download.ChunkResponse {
        var chunkResponse = downloadRepository.getChunk(jobId, chunkIndex)
        var waitCount = 0
        
        // Wait for chunk to be ready (handle pending status) - retry indefinitely
        while (chunkResponse.isPending()) {
            // Check for errors before waiting
            if (chunkResponse.hasError()) {
                throw Exception("API Error in chunk $chunkIndex: ${chunkResponse.error}")
            }
            
            waitCount++
            val waitMessage = if (chunkIndex == 0) {
                "Waiting for server to prepare data... (${waitCount * 5}s)"
            } else {
                "Waiting for chunk ${chunkIndex + 1} to complete... (${waitCount * 5}s)"
            }
            
            onProgress(DownloadProgressUpdate(
                currentChunk = chunkIndex,
                totalChunks = totalParts,
                currentTasksProcessed = 0,
                totalTasks = 0,
                message = waitMessage
            ))
            
            LoggingConfig.logUseCase("CompleteDownloadFlow", 
                "Chunk $chunkIndex is PENDING, waiting 5 seconds... (elapsed: ${waitCount * 5}s)")
            
            delay(5000) // Wait 5 seconds before checking again
            chunkResponse = downloadRepository.getChunk(jobId, chunkIndex)
        }
        
        // Final check for errors after pending loop
        if (chunkResponse.hasError()) {
            throw Exception("API Error in chunk $chunkIndex: ${chunkResponse.error}")
        }
        
        // For ready chunks, verify required fields are present
        if (chunkResponse.isReady() && (chunkResponse.jobId == null || chunkResponse.partId == null || chunkResponse.totalParts == null)) {
            throw Exception("Chunk $chunkIndex is ready but missing required fields: jobId=${chunkResponse.jobId}, partId=${chunkResponse.partId}, totalParts=${chunkResponse.totalParts}")
        }
        
        // Log completion info
        if (chunkResponse.data.isEmpty()) {
            LoggingConfig.logUseCase("CompleteDownloadFlow", 
                "Chunk $chunkIndex has no data - treating as empty chunk")
        } else {
            LoggingConfig.logUseCase("CompleteDownloadFlow", 
                "Chunk $chunkIndex ready with ${chunkResponse.data.size} tasks after ${waitCount * 5}s")
        }
        
        return chunkResponse
    }
}

// Extension function to convert ChunkStepData to StepDetailDomain
private fun ChunkStepData.toStepDetailDomain(): StepDetailDomain {
    return StepDetailDomain(
        activity_id = this.activityId,
        activity_title = this.activityTitle,
        activity_process_id = this.activityProcessId,
        activity_task_group = this.activityTaskGroup,
        activity_kind = this.activityKind,
        activity_form = this.activityForm,
        workflow_activity_tags_id = this.workflowActivityTagsId,
        form_name = this.formName,
        form_structure = this.formStructure?.toFormStructDomain() ?: domain.models.form_struct.FormStructDomain()
    )
}