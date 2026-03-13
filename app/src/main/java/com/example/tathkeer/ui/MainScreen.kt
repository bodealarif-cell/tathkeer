package com.example.tathkeer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tathkeer.TathkeerTheme
import com.example.tathkeer.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val section by viewModel.section.collectAsState()
    val currentZikr by viewModel.currentZikr.collectAsState()
    val index by viewModel.index.collectAsState()
    val listSize by viewModel.currentList.collectAsState { it.size }

    TathkeerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                // رأس التطبيق
                Header()

                Spacer(modifier = Modifier.height(16.dp))

                // علامات التبويب (صباح / مساء)
                SectionTabs(
                    selected = section,
                    onSelect = { viewModel.setSection(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // بطاقة الذكر
                if (currentZikr != null) {
                    ZikrCard(
                        zikr = currentZikr!!,
                        onTap = { viewModel.increment() }
                    )
                } else {
                    Text("لا توجد أذكار")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // عداد التقدم
                ProgressIndicator(
                    current = index + 1,
                    total = listSize,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // أزرار التنقل
                NavigationButtons(
                    onPrevious = { if (index > 0) viewModel.setIndex(index - 1) },
                    onNext = { if (index < listSize - 1) viewModel.setIndex(index + 1) },
                    onList = { /* نفتح قائمة الأذكار - يمكن إضافتها */ }
                )

                Spacer(modifier = Modifier.weight(1f))

                // زر إعادة تعيين
                ResetButton(onReset = { viewModel.resetAll() })
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "تَذْكِير",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        // يمكن إضافة أيقونة الثيم هنا
    }
}

@Composable
fun SectionTabs(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        FilterChip(
            selected = selected == "morning",
            onClick = { onSelect("morning") },
            label = { Text("أذكار الصباح") },
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        FilterChip(
            selected = selected == "evening",
            onClick = { onSelect("evening") },
            label = { Text("أذكار المساء") },
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun ProgressIndicator(current: Int, total: Int, modifier: Modifier = Modifier) {
    Text(
        text = "$current / $total",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = modifier
    )
}

@Composable
fun NavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onList: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onPrevious) {
            Icon(Icons.Default.ArrowBack, contentDescription = "السابق")
            Spacer(modifier = Modifier.width(4.dp))
            Text("السابق")
        }
        Button(onClick = onList) {
            Icon(Icons.Default.List, contentDescription = "القائمة")
        }
        Button(onClick = onNext) {
            Text("التالي")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "التالي")
        }
    }
}

@Composable
fun ResetButton(onReset: () -> Unit) {
    Button(
        onClick = onReset,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("إعادة تعيين", color = MaterialTheme.colorScheme.onErrorContainer)
    }
}
