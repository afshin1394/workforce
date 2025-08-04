package presentation.screens.download.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import irancell.nwg.wfm.MR
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import presentation.screens.download.viewmodel.DownloadViewModel
import presentation.screens.main.compose.MainScreen
import presentation.screens.main.compose.BaseScreen
import presentation.components.SimpleTopAppBar
import presentation.theme.backgroundBackground3
import presentation.theme.surfaceDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.textPrimary
import presentation.theme.textSecondary
import presentation.theme.textInverse
import presentation.theme.brand_blue_7

class DownloadScreen : Screen {
    
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: DownloadViewModel = koinInject()
        val downloadState by viewModel.downloadState.collectAsState()
        val scaffoldState = rememberBottomSheetScaffoldState()
        
        // Navigate to main screen when download is completed
        LaunchedEffect(downloadState.isCompleted) {
            if (downloadState.isCompleted && downloadState.error == null) {
                // Wait a moment to show completion message
                kotlinx.coroutines.delay(1500)
                navigator.replace(MainScreen(forceReload = true))
            }
        }
        
        BaseScreen(
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            title = stringResource(MR.strings.download),
            hasDrawer = false,
            topBar = {
                SimpleTopAppBar(
                    title = stringResource(MR.strings.download),
                    onBackClick = { navigator.pop() }
                )
            },
            content = { _ ->
                ManualDownloadScreenContent(
                    state = downloadState,
                    onStartDownload = { viewModel.startDownload() },
                    onRetry = { viewModel.retryDownload() },
                    onDismissError = { viewModel.clearError() }
                )
            },
            onBackPressed = { navigator.pop() }
        )
    }
}

@Composable
fun ManualDownloadScreenContent(
    state: presentation.screens.download.viewmodel.DownloadScreenState,
    onStartDownload: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBackground3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Title
            Text(
                text = stringResource(MR.strings.download_data),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = stringResource(MR.strings.refresh_all_data_description),
                style = MaterialTheme.typography.bodyLarge,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Show progress if downloading
            if (state.isLoading) {
                DownloadProgressCard(state = state)
            } else {
                // Show download button when not downloading
                DownloadActionCard(
                    onStartDownload = onStartDownload,
                    hasError = state.error != null
                )
            }
            
            // Error handling
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(MR.strings.download_error),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                onDismissError()
                                onRetry()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = stringResource(MR.strings.retry),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
            
            // Success message
            if (state.isCompleted && state.error == null) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(MR.strings.download_completed),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(MR.strings.redirecting_to_main),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadActionCard(
    onStartDownload: () -> Unit,
    hasError: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = surfaceDefault
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Icon
            Text(
                text = "⬇️",
                style = MaterialTheme.typography.displayMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = if (hasError) {
                    stringResource(MR.strings.download_failed_try_again)
                } else {
                    stringResource(MR.strings.download_fresh_data_description)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Download Button
            Button(
                onClick = onStartDownload,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = surfaceBrandDefault
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Download",
                        tint = textInverse,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasError) {
                            stringResource(MR.strings.retry_download)
                        } else {
                            stringResource(MR.strings.start_download)
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = textInverse
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadProgressCard(
    state: presentation.screens.download.viewmodel.DownloadScreenState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = surfaceDefault
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Progress Bar
            LinearProgressIndicator(
                progress = state.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = surfaceBrandDefault
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Minimal Progress Details
            if (state.totalChunks > 0 || state.totalTasks > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - Current operation
                    Text(
                        text = when {
                            state.totalChunks > 0 && state.currentChunk < state.totalChunks -> 
                                "Chunk ${state.currentChunk + 1}/${state.totalChunks}"
                            state.totalTasks > 0 -> 
                                "Processing ${state.currentTasksProcessed}/${state.totalTasks}"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                    
                    // Right side - Speed indicator or status
                    Text(
                        text = when {
                            state.message.contains("Waiting") -> "⏳"
                            state.message.contains("Processing") || state.message.contains("Saving") -> "💾"
                            state.message.contains("Downloading") -> "⬇️"
                            state.message.contains("Clearing") -> "🗑️"
                            state.isCompleted -> "✅"
                            else -> "📡"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Progress Text
            Text(
                text = "${(state.progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Current Status Message
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Detailed Progress Info
            if (state.totalChunks > 0 || state.totalTasks > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Download Details",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = textPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        if (state.totalChunks > 0) {
                            EnhancedProgressInfoRow(
                                label = "Chunks Downloaded",
                                current = state.currentChunk,
                                total = state.totalChunks,
                                icon = "📦"
                            )
                            
                            if (state.totalTasks > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        if (state.totalTasks > 0) {
                            EnhancedProgressInfoRow(
                                label = "Tasks Processed",
                                current = state.currentTasksProcessed,
                                total = state.totalTasks,
                                icon = "📋"
                            )
                        }
                        
                        // Show estimated completion or speed
                        if (!state.isCompleted && (state.totalChunks > 0 || state.totalTasks > 0)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary
                                )
                                
                                Text(
                                    text = when {
                                        state.message.contains("Waiting") -> "⏳ Waiting for server"
                                        state.message.contains("Processing") -> "⚙️ Processing data"
                                        state.message.contains("Saving") || state.message.contains("Saved") -> "💾 Saving to database"
                                        state.message.contains("Downloading") -> "⬇️ Downloading"
                                        state.message.contains("Clearing") -> "🗑️ Clearing old data"
                                        else -> "📡 Working..."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadScreenContent(
    state: presentation.screens.download.viewmodel.DownloadScreenState,
    onRetry: () -> Unit,
    onDismissError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBackground3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Title
            Text(
                text = stringResource(MR.strings.downloading_data),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = stringResource(MR.strings.please_wait_downloading),
                style = MaterialTheme.typography.bodyLarge,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceDefault
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // Progress Bar
                    LinearProgressIndicator(
                        progress = state.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = surfaceBrandDefault
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Minimal Progress Details
                    if (state.totalChunks > 0 || state.totalTasks > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left side - Current operation
                            Text(
                                text = when {
                                    state.totalChunks > 0 && state.currentChunk < state.totalChunks -> 
                                        "Chunk ${state.currentChunk + 1}/${state.totalChunks}"
                                    state.totalTasks > 0 -> 
                                        "Processing ${state.currentTasksProcessed}/${state.totalTasks}"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary
                            )
                            
                            // Right side - Speed indicator or status
                            Text(
                                text = when {
                                    state.message.contains("Waiting") -> "⏳"
                                    state.message.contains("Processing") || state.message.contains("Saving") -> "💾"
                                    state.message.contains("Downloading") -> "⬇️"
                                    state.message.contains("Clearing") -> "🗑️"
                                    state.isCompleted -> "✅"
                                    else -> "📡"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Progress Text
                    Text(
                        text = "${(state.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Current Status Message
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Detailed Progress Info
                    if (state.totalChunks > 0 || state.totalTasks > 0) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Download Details",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = textPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                if (state.totalChunks > 0) {
                                    EnhancedProgressInfoRow(
                                        label = stringResource(MR.strings.chunks_downloaded),
                                        current = state.currentChunk,
                                        total = state.totalChunks,
                                        icon = "📦"
                                    )
                                    
                                    if (state.totalTasks > 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                                
                                if (state.totalTasks > 0) {
                                    EnhancedProgressInfoRow(
                                        label = stringResource(MR.strings.tasks_processed),
                                        current = state.currentTasksProcessed,
                                        total = state.totalTasks,
                                        icon = "📋"
                                    )
                                }
                                
                                // Show estimated completion or speed
                                if (!state.isCompleted && (state.totalChunks > 0 || state.totalTasks > 0)) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Status",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textSecondary
                                        )
                                        
                                        Text(
                                            text = when {
                                                state.message.contains("Waiting") -> "⏳ Waiting for server"
                                                state.message.contains("Processing") -> "⚙️ Processing data"
                                                state.message.contains("Saving") || state.message.contains("Saved") -> "💾 Saving to database"
                                                state.message.contains("Downloading") -> "⬇️ Downloading"
                                                state.message.contains("Clearing") -> "🗑️ Clearing old data"
                                                else -> "📡 Working..."
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Error handling
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(MR.strings.download_error),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                onDismissError()
                                onRetry()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = stringResource(MR.strings.retry),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
            
            // Success message
            if (state.isCompleted && state.error == null) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(MR.strings.download_completed),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(MR.strings.redirecting_to_main),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressInfoRow(
    label: String,
    current: Int,
    total: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textSecondary
        )
        
        Text(
            text = "$current / $total",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = textPrimary
        )
    }
}

@Composable
fun EnhancedProgressInfoRow(
    label: String,
    current: Int,
    total: Int,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Progress indicator for this specific item
            val progress = if (total > 0) current.toFloat() / total.toFloat() else 0f
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = surfaceBrandDefault,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Text(
                text = "$current / $total",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = textPrimary
            )
        }
    }
}