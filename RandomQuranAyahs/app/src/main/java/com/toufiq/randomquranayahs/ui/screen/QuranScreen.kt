package com.toufiq.randomquranayahs.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toufiq.randomquranayahs.ui.state.QuranUiState
import com.toufiq.randomquranayahs.ui.theme.Amiri
import com.toufiq.randomquranayahs.ui.theme.Poppins
import com.toufiq.randomquranayahs.ui.viewmodel.QuranViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuranScreen(
    onSettingsClick: () -> Unit,
    viewModel: QuranViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    var isAnimating by remember { mutableStateOf(false) }
    var isFlipping by remember { mutableStateOf(false) }
    
    // Scale animation (plays once)
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = tween(500),
        label = "scale"
    )

    // Rotation animation for card flip
    val rotation by animateFloatAsState(
        targetValue = if (isFlipping) 180f else 0f,
        animationSpec = tween(500),
        label = "rotation"
    )

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Random Quran Ayahs",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Random Quran Ayah",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .scale(if (visible) 1f else 0.5f),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Content
                AnimatedContent(
                    targetState = state.uiState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) with fadeOut(animationSpec = tween(500))
                    },
                    label = "content"
                ) { uiState ->
                    when (uiState) {
                        is QuranUiState.Initial -> {
                            InitialContent()
                        }
                        is QuranUiState.Loading -> {
                            LoadingContent()
                        }
                        is QuranUiState.Success -> {
                            SuccessContent(
                                data = uiState.data,
                                isAnimating = isAnimating,
                                scale = scale,
                                rotation = rotation
                            )
                        }
                        is QuranUiState.Error -> {
                            ErrorContent(
                                message = uiState.message,
                                onRetry = { viewModel.loadRandomAyah() }
                            )
                        }
                    }
                }

                // Button
                Button(
                    onClick = {
                        isAnimating = true
                        isFlipping = true
                        viewModel.loadRandomAyah()
                        // Reset animations after delay using coroutine scope
                        scope.launch {
                            delay(500)
                            isAnimating = false
                            isFlipping = false
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .scale(if (visible) 1f else 0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (state.isLoading) "Loading..." else "Get Random Ayah",
                        modifier = Modifier.padding(8.dp),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialContent() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Text(
        text = "Click the button below to get a random Quran ayah",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(32.dp)
            .alpha(if (visible) 1f else 0f)
            .scale(if (visible) 1f else 0.8f),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun LoadingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    CircularProgressIndicator(
        modifier = Modifier
            .size(48.dp)
            .rotate(rotation),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SuccessContent(
    data: com.toufiq.randomquranayahs.data.model.QuranData,
    isAnimating: Boolean,
    scale: Float,
    rotation: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .graphicsLayer {
                if (isAnimating) {
                    scaleX = scale
                    scaleY = scale
                }
                rotationY = rotation
                cameraDistance = 8 * density
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                visible = true
            }
            
            Text(
                text = "Surah ${data.surah.englishName}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(if (visible) 1f else 0f)
                    .scale(if (visible) 1f else 0.8f),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = data.surah.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(if (visible) 1f else 0f)
                    .scale(if (visible) 1f else 0.8f),
                fontFamily = Amiri,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Ayah ${data.numberInSurah}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alpha(if (visible) 1f else 0f)
                    .scale(if (visible) 1f else 0.8f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = data.text,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(if (visible) 1f else 0f)
                    .scale(if (visible) 1f else 0.8f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Translation by ${data.edition.englishName}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(if (visible) 1f else 0f)
                    .scale(if (visible) 1f else 0.8f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(if (visible) 1f else 0f)
            .scale(if (visible) 1f else 0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Retry",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
} 