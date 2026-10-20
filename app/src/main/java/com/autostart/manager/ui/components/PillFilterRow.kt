package com.autostart.manager.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.autostart.manager.model.AutostartFilter

@Composable
fun PillFilterRow(
    selectedFilter: AutostartFilter,
    totalCount: Int,
    allowedCount: Int,
    notAllowedCount: Int,
    onFilterSelected: (AutostartFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PillItem(
            title = "All",
            count = totalCount,
            isSelected = selectedFilter == AutostartFilter.ALL,
            onClick = { onFilterSelected(AutostartFilter.ALL) },
            modifier = Modifier.weight(1f)
        )
        PillItem(
            title = "Allowed",
            count = allowedCount,
            isSelected = selectedFilter == AutostartFilter.ALLOWED,
            onClick = { onFilterSelected(AutostartFilter.ALLOWED) },
            modifier = Modifier.weight(1f)
        )
        PillItem(
            title = "Not Allowed",
            count = notAllowedCount,
            isSelected = selectedFilter == AutostartFilter.NOT_ALLOWED,
            onClick = { onFilterSelected(AutostartFilter.NOT_ALLOWED) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PillItem(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "pill_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "pill_fg"
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title ($count)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1
        )
    }
}
