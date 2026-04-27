package com.app.huisu.ui.quicknote

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.data.entity.QuickNote
import com.app.huisu.data.entity.QuickNoteSpace
import com.app.huisu.data.entity.QuickNoteType
import com.app.huisu.data.entity.generateQuickNoteTitle
import com.app.huisu.data.entity.tagList
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.DividerColor
import com.app.huisu.ui.theme.ErrorRed
import com.app.huisu.ui.theme.GlassWhite
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary
import com.app.huisu.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuickNoteScreen(
    viewModel: QuickNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var viewingNote by remember { mutableStateOf<QuickNote?>(null) }
    var editingNote by remember { mutableStateOf<QuickNote?>(null) }
    var noteToDelete by remember { mutableStateOf<QuickNote?>(null) }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            delay(2400)
            viewModel.clearError()
        }
    }

    ZenBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    top = 18.dp,
                    end = 18.dp,
                    bottom = 104.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickNoteSearchField(
                        query = uiState.query,
                        onQueryChange = viewModel::updateQuery
                    )
                }

                item {
                    QuickNoteMetaFilterRow(
                        selected = uiState.selectedSpace,
                        onSpaceSelected = viewModel::selectSpace,
                        onClear = viewModel::clearFilters
                    )
                }

                if (uiState.popularTags.isNotEmpty()) {
                    item {
                        PopularTagsRow(
                            tags = uiState.popularTags,
                            onTagSelected = viewModel::applyTagQuery
                        )
                    }
                }

                uiState.error?.let {
                    item {
                        InlineMessage(
                            message = it,
                            accent = ErrorRed
                        )
                    }
                }

                if (uiState.notes.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            EmptyQuickNoteState(
                                hasQuery = uiState.query.isNotBlank()
                            )
                        }
                    }
                } else {
                    items(uiState.notes, key = { it.id }) { note ->
                        QuickNoteCard(
                            note = note,
                            onOpen = { viewingNote = note },
                            onEdit = { editingNote = note },
                            onToggleFavorite = { viewModel.toggleFavorite(note.id) },
                            onTogglePinned = { viewModel.togglePinned(note.id) },
                            onDelete = { noteToDelete = note }
                        )
                    }
                }
            }

            QuickNoteAddButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
            )
        }
    }

    if (showCreateDialog) {
        QuickNoteEditorDialog(
            title = "新建记录",
            initialTitle = "",
            initialContent = "",
            initialType = QuickNoteType.NOTE,
            initialSpace = QuickNoteSpace.PERSONAL,
            initialTags = "",
            initialFavorite = false,
            initialPinned = false,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, content, type, space, tags, favorite, pinned ->
                viewModel.addNote(title, content, type, space, tags, favorite, pinned)
                showCreateDialog = false
            }
        )
    }

    viewingNote?.let { note ->
        QuickNoteDetailDialog(
            note = note,
            onDismiss = { viewingNote = null }
        )
    }

    editingNote?.let { note ->
        QuickNoteEditorDialog(
            title = "编辑记录",
            initialTitle = note.title,
            initialContent = note.content,
            initialType = note.type,
            initialSpace = note.space,
            initialTags = note.tags,
            initialFavorite = note.isFavorite,
            initialPinned = note.isPinned,
            onDismiss = { editingNote = null },
            onConfirm = { title, content, type, space, tags, favorite, pinned ->
                viewModel.updateNote(note, title, content, type, space, tags, favorite, pinned)
                editingNote = null
            }
        )
    }

    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("删除记录") },
            text = { Text("删除后，这条记录不会再出现在速记列表中。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(note.id)
                        noteToDelete = null
                    }
                ) {
                    Text("删除", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun QuickNoteAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(58.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Purple667),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickNoteSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "搜索",
                tint = TextSecondary
            )
        },
        placeholder = {
            Text(
                text = "搜索标题、正文或自定义标签",
                color = TextTertiary
            )
        },
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = GlassWhite,
            focusedContainerColor = GlassWhite,
            unfocusedBorderColor = DividerColor,
            focusedBorderColor = Purple667,
            cursorColor = Purple667
        )
    )
}

@Composable
private fun QuickNoteMetaFilterRow(
    selected: QuickNoteSpaceFilter,
    onSpaceSelected: (QuickNoteSpaceFilter) -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = "全部",
            selected = selected == QuickNoteSpaceFilter.ALL,
            onClick = onClear
        )
        QuickNoteSpaceFilter.values()
            .filter { it != QuickNoteSpaceFilter.ALL }
            .forEach { filter ->
                FilterChip(
                    label = "#${filter.label}",
                    selected = selected == filter,
                    onClick = {
                        onSpaceSelected(
                            if (selected == filter) QuickNoteSpaceFilter.ALL else filter
                        )
                    }
                )
            }
    }
}

@Composable
private fun PopularTagsRow(
    tags: List<QuickNoteTagCount>,
    onTagSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "高频标签",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                InfoPill(
                    label = "#${tag.label}",
                    modifier = Modifier.clickable { onTagSelected(tag.label) },
                    backgroundColor = Color.White.copy(alpha = 0.75f),
                    contentColor = Purple667
                )
            }
        }
    }
}

@Composable
private fun InlineMessage(
    message: String,
    accent: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun EmptyQuickNoteState(
    hasQuery: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (hasQuery) "没有找到匹配的记录" else "还没有任何速记",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Text(
            text = if (hasQuery) {
                "可以换个关键词，或者清空筛选后再看。"
            } else {
                "先记下一条短内容，后面再补充标题和自定义标签。"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun QuickNoteCard(
    note: QuickNote,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTogglePinned: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        border = BorderStroke(1.dp, DividerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoPill(
                        label = "#${note.space.displayName}",
                        backgroundColor = Purple667.copy(alpha = 0.12f),
                        contentColor = Purple667
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreHoriz,
                            contentDescription = "更多操作",
                            tint = TextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("编辑") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "编辑",
                                    tint = Purple667
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (note.isFavorite) "取消收藏" else "加入收藏") },
                            onClick = {
                                showMenu = false
                                onToggleFavorite()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (note.isPinned) "取消置顶" else "置顶") },
                            onClick = {
                                showMenu = false
                                onTogglePinned()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("删除", color = ErrorRed) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "删除",
                                    tint = ErrorRed
                                )
                            }
                        )
                    }
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (note.tagList().isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    note.tagList().forEach { tag ->
                        InfoPill(
                            label = "#$tag",
                            backgroundColor = Color.White.copy(alpha = 0.72f),
                            contentColor = TextSecondary
                        )
                    }
                }
            }

            Text(
                text = "更新于 ${formatQuickNoteTime(note.updatedAt)}",
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun QuickNoteDetailDialog(
    note: QuickNote,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                InfoPill(
                    label = "#${note.space.displayName}",
                    backgroundColor = Purple667.copy(alpha = 0.12f),
                    contentColor = Purple667
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
                if (note.tagList().isNotEmpty()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        note.tagList().forEach { tag ->
                            InfoPill(
                                label = "#$tag",
                                backgroundColor = Color.White.copy(alpha = 0.72f),
                                contentColor = TextSecondary
                            )
                        }
                    }
                }
                Text(
                    text = "更新于 ${formatQuickNoteTime(note.updatedAt)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭", color = Purple667)
            }
        }
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Purple667.copy(alpha = 0.14f) else GlassWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Purple667.copy(alpha = 0.30f) else DividerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Purple667 else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun QuickNoteTagEditor(
    tags: String,
    pendingTag: String,
    onPendingTagChange: (String) -> Unit,
    onTagsChange: (String) -> Unit
) {
    val currentTags = remember(tags) { parseQuickNoteTags(tags) }

    fun commitTags(raw: String) {
        val nextTags = (currentTags + parseQuickNoteTags(raw)).distinct()
        onTagsChange(nextTags.joinToString(", "))
        onPendingTagChange("")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "自定义标签",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )

        if (currentTags.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentTags.forEach { tag ->
                    InfoPill(
                        label = "#$tag",
                        modifier = Modifier.clickable {
                            onTagsChange(currentTags.filterNot { it == tag }.joinToString(", "))
                        },
                        backgroundColor = Color.White.copy(alpha = 0.72f),
                        contentColor = TextSecondary
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = pendingTag,
                onValueChange = { value ->
                    if (value.any { it == ',' || it == '，' || it == '、' || it == '\n' }) {
                        commitTags(value)
                    } else {
                        onPendingTagChange(value)
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("新增标签") },
                placeholder = { Text("#标签名") },
                shape = RoundedCornerShape(20.dp)
            )
            TextButton(
                enabled = parseQuickNoteTags(pendingTag).isNotEmpty(),
                onClick = { commitTags(pendingTag) }
            ) {
                Text("添加", color = Purple667)
            }
        }
    }
}

@Composable
private fun QuickNoteEditorDialog(
    title: String,
    initialTitle: String,
    initialContent: String,
    initialType: QuickNoteType,
    initialSpace: QuickNoteSpace,
    initialTags: String,
    initialFavorite: Boolean,
    initialPinned: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        content: String,
        type: QuickNoteType,
        space: QuickNoteSpace,
        tags: String,
        isFavorite: Boolean,
        isPinned: Boolean
    ) -> Unit
) {
    var noteTitle by remember(title, initialTitle) { mutableStateOf(initialTitle) }
    var noteContent by remember(title, initialContent) { mutableStateOf(initialContent) }
    var noteSpace by remember(title, initialSpace) { mutableStateOf(initialSpace) }
    var noteTags by remember(title, initialTags) { mutableStateOf(initialTags) }
    var pendingTag by remember(title) { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("标题（可选）") },
                        placeholder = { Text(generateQuickNoteTitle()) },
                        shape = RoundedCornerShape(20.dp)
                    )

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("正文") },
                        placeholder = { Text("把想到的内容先记下来") },
                        minLines = 5,
                        maxLines = 8,
                        shape = RoundedCornerShape(20.dp)
                    )

                    Text(
                        text = "类型",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickNoteSpace.values().forEach { space ->
                            FilterChip(
                                label = "#${space.displayName}",
                                selected = noteSpace == space,
                                onClick = { noteSpace = space }
                            )
                        }
                    }

                    QuickNoteTagEditor(
                        tags = noteTags,
                        pendingTag = pendingTag,
                        onPendingTagChange = { pendingTag = it },
                        onTagsChange = { noteTags = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = "取消",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "保存",
                        onClick = {
                            onConfirm(
                                noteTitle,
                                noteContent,
                                initialType,
                                noteSpace,
                                noteTags,
                                initialFavorite,
                                initialPinned
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun formatQuickNoteTime(timestamp: Long): String {
    return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date(timestamp))
}

private fun parseQuickNoteTags(raw: String): List<String> {
    return raw
        .split(Regex("[,，、\\n]"))
        .map { it.trim().trimStart('#').trim() }
        .filter { it.isNotEmpty() }
        .distinct()
}
