package com.toufiq.randomquranayahs.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toufiq.randomquranayahs.ui.theme.Poppins
import com.toufiq.randomquranayahs.ui.viewmodel.NotificationStyle
import com.toufiq.randomquranayahs.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification Frequency Card
            SettingsCard(title = "Notification Frequency") {
                Text(
                    text = "How often would you like to receive Quran ayahs?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Poppins
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.frequency.toFloat(),
                        onValueChange = { viewModel.updateFrequency(it.toInt()) },
                        valueRange = 5f..60f,
                        steps = 11,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${state.frequency} min",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Note: Android may adjust the exact timing to optimize battery life",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Poppins,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Notification Style Card
            SettingsCard(title = "Notification Style") {
                Text(
                    text = "Choose how your notifications appear",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Poppins
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NotificationStyle.values().forEach { style ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = style.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = Poppins
                            )
                            RadioButton(
                                selected = state.notificationStyle == style,
                                onClick = { viewModel.updateNotificationStyle(style) }
                            )
                        }
                    }
                }
            }

            // Appearance Settings Card
            SettingsCard(title = "Appearance Settings") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Content Visibility
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Content Visibility",
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium
                        )
                        
                        SettingsSwitch(
                            text = "Show Arabic Text",
                            checked = state.appearance.showArabicText,
                            onCheckedChange = { viewModel.toggleShowArabicText() }
                        )
                        
                        SettingsSwitch(
                            text = "Show Translation",
                            checked = state.appearance.showTranslation,
                            onCheckedChange = { viewModel.toggleShowTranslation() }
                        )
                        
                        SettingsSwitch(
                            text = "Show Surah Info",
                            checked = state.appearance.showSurahInfo,
                            onCheckedChange = { viewModel.toggleShowSurahInfo() }
                        )
                        
                        SettingsSwitch(
                            text = "Show Translator Info",
                            checked = state.appearance.showTranslatorInfo,
                            onCheckedChange = { viewModel.toggleShowTranslatorInfo() }
                        )
                    }

                    // Font Settings
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Font Settings",
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Font Size",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = Poppins
                            )
                            Text(
                                text = "${state.appearance.fontSize}sp",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = Poppins
                            )
                        }
                        
                        Slider(
                            value = state.appearance.fontSize.toFloat(),
                            onValueChange = { viewModel.updateFontSize(it.toInt()) },
                            valueRange = 12f..24f,
                            steps = 6,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        SettingsSwitch(
                            text = "Use Custom Font",
                            checked = state.appearance.useCustomFont,
                            onCheckedChange = { viewModel.toggleCustomFont() }
                        )
                        
                        if (state.appearance.useCustomFont) {
                            OutlinedTextField(
                                value = state.appearance.customFontName,
                                onValueChange = { viewModel.updateCustomFont(it) },
                                label = { Text("Custom Font Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Notification Preview Card
            SettingsCard(title = "Notification Preview") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Mode Preview",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = Poppins
                    )
                    Switch(
                        checked = state.isDarkModePreview,
                        onCheckedChange = { viewModel.toggleDarkModePreview() }
                    )
                }

                // Animated Preview Container
                val backgroundColor by animateColorAsState(
                    targetValue = if (state.isDarkModePreview) {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    animationSpec = tween(300),
                    label = "backgroundColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (state.isDarkModePreview) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = tween(300),
                    label = "textColor"
                )

                val secondaryTextColor by animateColorAsState(
                    targetValue = if (state.isDarkModePreview) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    },
                    animationSpec = tween(300),
                    label = "secondaryTextColor"
                )

                val tertiaryTextColor by animateColorAsState(
                    targetValue = if (state.isDarkModePreview) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    },
                    animationSpec = tween(300),
                    label = "tertiaryTextColor"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // App Icon and Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = "Random Quran Ayahs",
                                style = MaterialTheme.typography.titleSmall,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                        }

                        // Notification Content
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Surah Al-Fatiha - Ayah 1",
                                style = MaterialTheme.typography.titleSmall,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                            )
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = Poppins,
                                color = textColor
                            )
                            Text(
                                text = "In the name of Allah, the Entirely Merciful, the Especially Merciful",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = Poppins,
                                color = secondaryTextColor
                            )
                            Text(
                                text = "Translation by Saheeh International",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = Poppins,
                                color = tertiaryTextColor
                            )
                        }
                    }
                }

                Text(
                    text = "This is how your notifications will appear",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Poppins,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Poppins
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
} 