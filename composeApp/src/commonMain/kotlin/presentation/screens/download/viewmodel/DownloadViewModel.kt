package presentation.screens.download.viewmodel

import domain.usecase.usecase.download.CompleteDownloadFlowUseCase
import domain.usecase.usecase.download.DownloadProgressUpdate
import domain.usecase.usecase.database.CheckDatabaseDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import utils.BaseViewModel

data class DownloadScreenState(
    val isLoading: Boolean = false,
    val progress: Float = 0f,
    val currentChunk: Int = 0,
    val totalChunks: Int = 0,
    val currentTasksProcessed: Int = 0,
    val totalTasks: Int = 0,
    val message: String = "",
    val isCompleted: Boolean = false,
    val error: String? = null,
    val skipDownload: Boolean = false
)

class DownloadViewModel(
    private val completeDownloadFlowUseCase: CompleteDownloadFlowUseCase,
    private val checkDatabaseDataUseCase: CheckDatabaseDataUseCase
) : BaseViewModel() {

    private val _downloadState = MutableStateFlow(DownloadScreenState())
    val downloadState: StateFlow<DownloadScreenState> = _downloadState.asStateFlow()

    fun startDownload() {
        viewModelScope.launch {
            // Reset state before starting new download
            _downloadState.value = DownloadScreenState(
                isLoading = true,
                error = null,
                message = "Checking database...",
                isCompleted = false
            )
            
            // Always perform download (force refresh all data)
            performDownload()
        }
    }
    
    private suspend fun performDownload() {
        _downloadState.value = _downloadState.value.copy(
            message = "Initializing download...",
            skipDownload = false
        )
        
        try {
            completeDownloadFlowUseCase(Unit)
                .flatMapLatest { result ->
                    when (result.status) {
                        utils.AsyncStatus.SUCCESS -> {
                            result.data ?: emptyFlow()
                        }
                        utils.AsyncStatus.ERROR -> {
                            // Emit a single error update then complete
                            flowOf(
                                DownloadProgressUpdate(
                                    currentChunk = 0,
                                    totalChunks = 0,
                                    currentTasksProcessed = 0,
                                    totalTasks = 0,
                                    message = "Download failed",
                                    error = result.message ?: "Unknown error occurred"
                                )
                            )
                        }
                        utils.AsyncStatus.EMPTY -> {
                            // Emit a single empty update then complete
                            flowOf(
                                DownloadProgressUpdate(
                                    currentChunk = 0,
                                    totalChunks = 0,
                                    currentTasksProcessed = 0,
                                    totalTasks = 0,
                                    message = "No data to download"
                                )
                            )
                        }
                        utils.AsyncStatus.LOADING -> {
                            // Don't emit anything for loading, let the progress updates handle it
                            emptyFlow()
                        }
                    }
                }
                .collect { update ->
                    _downloadState.value = _downloadState.value.copy(
                        currentChunk = update.currentChunk,
                        totalChunks = update.totalChunks,
                        currentTasksProcessed = update.currentTasksProcessed,
                        totalTasks = update.totalTasks,
                        message = update.message,
                        progress = calculateProgress(update),
                        isCompleted = update.isCompleted,
                        error = update.error
                    )
                    
                    // If there's an error, stop loading
                    if (update.error != null) {
                        _downloadState.value = _downloadState.value.copy(isLoading = false)
                    }
                    
                    // If completed, stop loading
                    if (update.isCompleted) {
                        _downloadState.value = _downloadState.value.copy(isLoading = false)
                    }
                }
        } catch (e: Exception) {
            _downloadState.value = _downloadState.value.copy(
                isLoading = false,
                error = e.message ?: "Unknown error occurred",
                message = "Download failed"
            )
        }
    }
    
    private fun calculateProgress(update: DownloadProgressUpdate): Float {
        return when {
            update.totalChunks == 0 && update.totalTasks == 0 -> 0f
            update.totalTasks > 0 -> {
                // When processing tasks, use task progress (60-100%)
                val taskProgress = update.currentTasksProcessed.toFloat() / update.totalTasks.toFloat()
                0.6f + (taskProgress * 0.4f)
            }
            update.totalChunks > 0 -> {
                // When downloading chunks, use chunk progress (0-60%)
                val chunkProgress = update.currentChunk.toFloat() / update.totalChunks.toFloat()
                chunkProgress * 0.6f
            }
            else -> 0f
        }
    }
    
    fun retryDownload() {
        startDownload()
    }
    
    fun clearError() {
        _downloadState.value = _downloadState.value.copy(error = null)
    }
}