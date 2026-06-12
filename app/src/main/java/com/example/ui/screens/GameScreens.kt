package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.spring
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Case
import com.example.model.CaseDifficulty
import com.example.ui.theme.*
import com.example.viewmodel.GamePhase
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.GridMark

@Composable
fun GameScreens(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val phase by viewModel.gamePhase.collectAsState()
    val activeCase by viewModel.activeCase.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                fadeIn(spring()) + slideInVertically { it / 5 } togetherWith
                        fadeOut(spring())
            },
            label = "PhaseTransition"
        ) { currentPhase ->
            when (currentPhase) {
                GamePhase.WELCOME -> WelcomeScreen(viewModel)
                GamePhase.CASE_SELECT -> CaseSelectionScreen(viewModel)
                GamePhase.DOSSIER_INTRO -> DossierIntroScreen(viewModel)
                GamePhase.PLAYING -> GamePlayScreen(viewModel)
                GamePhase.ACCUSATION -> AccusationScreen(viewModel)
                GamePhase.VICTORY -> VictoryScreen(viewModel)
                GamePhase.FAILURE -> FailureScreen(viewModel)
            }
        }
    }
}

// ----------------------------------------------------
// 1. WELCOME SCREEN
// ----------------------------------------------------
@Composable
fun WelcomeScreen(viewModel: GameViewModel) {
    val solvedIds by viewModel.solvedCaseIds.collectAsState()
    val totalCases = viewModel.cases.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // Decorative Retro-Noir Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NOIR DEDUCTION",
                fontSize = 14.sp,
                letterSpacing = 6.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "DETECTIVE GRID",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Logic Riddle Syndicate",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Custom drawn detective console badge/radar
        Box(
            modifier = Modifier
                .size(200.dp)
                .drawBehind {
                    val center = Offset(size.width / 2, size.height / 2)
                    // Draw target reticle
                    drawCircle(
                        color = Color(0x33E53935),
                        radius = size.width / 2.5f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.51f)
                    )
                    drawCircle(
                        color = Color(0x1F29B6F6),
                        radius = size.width / 3.8f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.22f)
                    )
                    drawLine(
                        color = Color(0x2CE53935),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color(0x2CE53935),
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 1f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🔍",
                    fontSize = 52.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CASE FILE VAULT",
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Statistics Cards
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "COMMISSION STATS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${solvedIds.size} / $totalCases",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "SOLVED",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val rank = getDetectiveRank(solvedIds.size)
                        Text(
                            text = rank,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "OFFICIAL RANK",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Play Button
        Button(
            onClick = { viewModel.changePhase(GamePhase.CASE_SELECT) },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Enter Headquarters",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACCESS DOSSIERS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun getDetectiveRank(solvedCount: Int): String {
    return when (solvedCount) {
        0 -> "Street Watch"
        1 -> "Patrolled Analyst"
        2 -> "Junior Gumshoe"
        3 -> "Grid Inspector"
        4 -> "Master Mind"
        else -> "Legend Inspector"
    }
}

// ----------------------------------------------------
// 2. CASE SELECTION SCREEN
// ----------------------------------------------------
@Composable
fun CaseSelectionScreen(viewModel: GameViewModel) {
    val completedMap by viewModel.caseCompletions.collectAsState()
    val cases = viewModel.cases

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.changePhase(GamePhase.WELCOME) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Welcome",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "ACTIVE DOSSIERS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "Select a cold case assignment to begin logical cross-examination.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cases) { case ->
                val progress = completedMap[case.id]
                val isSolved = progress?.isCompleted == true && progress.isSuccess

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectCase(case) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (isSolved) 2.dp else 1.dp,
                        color = if (isSolved) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Header line with difficulty & solved status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DifficultyBadge(case.difficulty)
                            if (isSolved) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1F4CAF50))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Solved",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CASE RESOLVED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            } else if (progress != null && !progress.isCompleted) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1F29B6F6))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "IN PROGRESS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Title
                        Text(
                            text = case.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Description
                        Text(
                            text = case.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Parameters count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "👤 ${case.suspects.size} Suspects",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🔪 ${case.weapons.size} Weapons",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "📍 ${case.locations.size} Locations",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyBadge(difficulty: CaseDifficulty) {
    val (label, bg, fg) = when (difficulty) {
        CaseDifficulty.BEGINNER -> Triple("BEGINNER", Color(0x1F4CAF50), Color(0xFF81C784))
        CaseDifficulty.INTERMEDIATE -> Triple("INTERMEDIATE", Color(0x1F29B6F6), Color(0xFF64B5F6))
        CaseDifficulty.ADVANCED -> Triple("ADVANCED", Color(0x1FE53935), Color(0xFFEF5350))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            letterSpacing = 1.sp
        )
    }
}

// ----------------------------------------------------
// 3. DOSSIER INTRO SCREEN
// ----------------------------------------------------
@Composable
fun DossierIntroScreen(viewModel: GameViewModel) {
    val case by viewModel.activeCase.collectAsState()
    val scrollState = rememberScrollState()

    if (case == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Row for Title & Back
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.changePhase(GamePhase.CASE_SELECT) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to selection",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "CASE FILE REVIEW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = case!!.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dossier details scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp)
        ) {
            // Narrative Story Desk
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(1.dp, Color(0x3390A4AE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INCIDENT BRIEFING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = case!!.backstory,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Target Parameters folders
            Text(
                text = "CASE PARAMETERS & ROSTERS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Suspects list
            ParameterSectionCard("👤 SUSPECT LIST", case!!.suspects, MaterialTheme.colorScheme.primary)
            ParameterSectionCard("🔪 RETRIEVED WEAPONS", case!!.weapons, MaterialTheme.colorScheme.secondary)
            ParameterSectionCard("📍 CRIME SCENE LOCATIONS", case!!.locations, MaterialTheme.colorScheme.tertiary)

            if (case!!.solutionLiar != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x1FE53935)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "CRIME SCENE TWIST ACTIVE!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935),
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Exactly ONE suspect in this case is outright LYING. Cross-examine their witness alibis to deduce the truth teller and find the fraud!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Action launch button
        Button(
            onClick = { viewModel.changePhase(GamePhase.PLAYING) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "COMMENCE CROSS-EXAMINATION",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ParameterSectionCard(title: String, items: List<String>, highlightColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = highlightColor,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. GAME PLAY SCREEN (CHRONICLES & BLUEPRINTS VIEW)
// ----------------------------------------------------
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePlayScreen(viewModel: GameViewModel) {
    val case by viewModel.activeCase.collectAsState()
    val elapsedSeconds by viewModel.elapsedTime.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp >= 600

    if (case == null) return

    val formattedTime = remember(elapsedSeconds) {
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Chronicles (Logs), 1: Grid (Blueprints)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = case!!.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "INVESTIGATING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        
                        // Timer Dashboard
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⏳",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = formattedTime,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.changePhase(GamePhase.CASE_SELECT) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to list"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // High-contrast primary crime resolution button in bottom of screen if portrait
            if (!isLandscape) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabSelectionRow(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            modifier = Modifier.weight(1f).padding(end = 12.dp)
                        )

                        Button(
                            onClick = { viewModel.changePhase(GamePhase.ACCUSATION) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = "ACCUSE ⚖️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LogGridConsole(
            case = case!!,
            viewModel = viewModel,
            isLandscape = isLandscape,
            selectedTab = selectedTab,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TabSelectionRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (selectedTab == 0) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .clickable { onTabSelected(0) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📰 CHRONICLES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (selectedTab == 1) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .clickable { onTabSelected(1) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📊 BLUEPRINT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LogGridConsole(
    case: Case,
    viewModel: GameViewModel,
    isLandscape: Boolean,
    selectedTab: Int,
    modifier: Modifier = Modifier
) {
    if (isLandscape) {
        // Dual horizontal split
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane: Chronicles
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChroniclesPane(
                    case = case,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { viewModel.changePhase(GamePhase.ACCUSATION) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "FILE FINAL DEDUCTION REPORT ⚖️",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Right Pane: Active Deduction Grid
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                DeductionBlueprintPane(
                    case = case,
                    viewModel = viewModel,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    } else {
        // Single pane toggled by swipe/tab
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (selectedTab == 0) {
                ChroniclesPane(
                    case = case,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    DeductionBlueprintPane(
                        case = case,
                        viewModel = viewModel,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4a. CHRONICLES PANE (STORY, CLUES, TESTIMONIES)
// ----------------------------------------------------
@Composable
fun ChroniclesPane(
    case: Case,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val struckSet by viewModel.struckClues.collectAsState()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Case background summary
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CASE FILE DOCKET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = case.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Testimonies (Quotes / Witness records)
        if (case.testimonies.isNotEmpty()) {
            item {
                Text(
                    text = "WITNESS TESTIMONIES (1 SULK LIAR ACTIVE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }

            items(case.testimonies) { testimony ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🕵️",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = testimony.suspect,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${testimony.quote}\"",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            )
                        }
                    }
                }
            }
        }

        // Clue log
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISCOVERED EVIDENCE LOGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Tap clue to cross off",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(case.clues.size) { index ->
            val clueText = case.clues[index]
            val isStruck = struckSet.contains(index)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleClueStrike(index) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isStruck) Color(0x0F29B6F6) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isStruck) Color(0x3329B6F6) else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isStruck) Color(0xFF29B6F6) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isStruck) Color(0xFF29B6F6) else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isStruck) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Checked",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = clueText,
                        fontSize = 13.sp,
                        color = if (isStruck) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isStruck) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4b. DEDUCTION BLUEPRINT PANE (THE INTERACTIVE GRID & LEGEND)
// ----------------------------------------------------
@Composable
fun DeductionBlueprintPane(
    case: Case,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gridState by viewModel.gridState.collectAsState()
    
    // Choose which sub-grid matrix to show.
    // 0: Suspects vs Weapons, 1: Suspects vs Locations, 2: Locations vs Weapons
    var activeSubgridTab by remember { mutableStateOf(0) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Grid Selector Rows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BLUEPRINT DETECT-O-GRID",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
            
            TextButton(
                onClick = { viewModel.clearActiveGrid() },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Board",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "RESET MATRIX", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Subgrid selector chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubgridChip(
                label = "👤 ↔ 🔪 SUS/WEP",
                selected = activeSubgridTab == 0,
                onClick = { activeSubgridTab = 0 }
            )
            SubgridChip(
                label = "👤 ↔ 📍 SUS/LOC",
                selected = activeSubgridTab == 1,
                onClick = { activeSubgridTab = 1 }
            )
            SubgridChip(
                label = "📍 ↔ 🔪 LOC/WEP",
                selected = activeSubgridTab == 2,
                onClick = { activeSubgridTab = 2 }
            )
        }

        // Select the active labels
        val (rows, cols, rowIcon, colIcon, rowPrefix, colPrefix) = when (activeSubgridTab) {
            0 -> Sextuple(case.suspects, case.weapons, "👤", "🔪", "S", "W")
            1 -> Sextuple(case.suspects, case.locations, "👤", "📍", "S", "L")
            else -> Sextuple(case.locations, case.weapons, "📍", "🔪", "L", "W")
        }

        // The Table Grid Board
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Table Columns Header Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Top-Left Header Cell (Intersection descriptors)
                    Box(
                        modifier = Modifier
                            .size(width = 85.dp, height = 44.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$rowIcon ↔ $colIcon",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Column Item Cells
                    cols.forEachIndexed { colIdx, _ ->
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 44.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(0.5.dp, MaterialTheme.colorScheme.outline),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$colPrefix${colIdx + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Table Rows
                rows.forEachIndexed { rowIdx, rowItemName ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Left Row Title Cell
                        Box(
                            modifier = Modifier
                                .size(width = 85.dp, height = 44.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(0.5.dp, MaterialTheme.colorScheme.outline)
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "$rowPrefix${rowIdx + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // Grid cells
                        cols.forEach { colItemName ->
                            val cellMark = gridState["$rowItemName|$colItemName"] ?: GridMark.NONE

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        when (cellMark) {
                                            GridMark.NONE -> MaterialTheme.colorScheme.surface
                                            GridMark.X -> Color(0x1FE53935)
                                            GridMark.O -> Color(0x1F4CAF50)
                                        }
                                    )
                                    .border(0.5.dp, MaterialTheme.colorScheme.outline)
                                    .clickable { viewModel.toggleGridMark(rowItemName, colItemName) },
                                contentAlignment = Alignment.Center
                            ) {
                                when (cellMark) {
                                    GridMark.NONE -> {}
                                    GridMark.X -> {
                                        Text(
                                            text = "X",
                                            color = Color(0xFFE53935),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    GridMark.O -> {
                                        Text(
                                            text = "✔",
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Space out
        Spacer(modifier = Modifier.height(12.dp))

        // Legend / Key directory
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "DEDUCTION DECODER KEY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column
                    Column(modifier = Modifier.weight(1f)) {
                        rows.forEachIndexed { idx, name ->
                            Text(
                                text = "$rowIcon $rowPrefix${idx + 1}: $name",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Divider segment
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(MaterialTheme.colorScheme.outline)
                            .padding(horizontal = 8.dp)
                    )

                    // Right Column
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        cols.forEachIndexed { idx, name ->
                            Text(
                                text = "$colIcon $colPrefix${idx + 1}: $name",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Sextuple<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
)

@Composable
fun SubgridChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ----------------------------------------------------
// 5. ACCUSATION SUBMISSION SCREEN
// ----------------------------------------------------
@Composable
fun AccusationScreen(viewModel: GameViewModel) {
    val case by viewModel.activeCase.collectAsState()
    val chosenSuspect by viewModel.accusedSuspect.collectAsState()
    val chosenWeapon by viewModel.accusedWeapon.collectAsState()
    val chosenLocation by viewModel.accusedLocation.collectAsState()
    val chosenLiar by viewModel.accusedLiar.collectAsState()
    val scrollState = rememberScrollState()

    if (case == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Toolbar header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.changePhase(GamePhase.PLAYING) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return to playboard",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "DEDUCTION STATEMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "ACCUSE CONSPIRATORS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instruction Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = "Select who committed the crime, which weapon was utilized, and the location. If a double-cross lie was active, identify the liar!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Selection 1: Suspect
            AccusationDropdownSection(
                title = "👤 THE PRIMARY SUSPECT",
                options = case!!.suspects,
                currentSelection = chosenSuspect,
                onSelected = { viewModel.setAccusedSuspect(it) },
                iconColor = MaterialTheme.colorScheme.primary
            )

            // Selection 2: Weapon
            AccusationDropdownSection(
                title = "🔪 THE MURDER WEAPON",
                options = case!!.weapons,
                currentSelection = chosenWeapon,
                onSelected = { viewModel.setAccusedWeapon(it) },
                iconColor = MaterialTheme.colorScheme.secondary
            )

            // Selection 3: Location
            AccusationDropdownSection(
                title = "📍 THE CRIME SCENE LOCATION",
                options = case!!.locations,
                currentSelection = chosenLocation,
                onSelected = { viewModel.setAccusedLocation(it) },
                iconColor = MaterialTheme.colorScheme.tertiary
            )

            // Selection 4: Liar (if twist active)
            if (case!!.solutionLiar != null) {
                AccusationDropdownSection(
                    title = "⚠️ THE LYING WITNESS",
                    options = case!!.suspects,
                    currentSelection = chosenLiar,
                    onSelected = { viewModel.setAccusedLiar(it) },
                    iconColor = CrimsonAccent
                )
            }
        }

        val isReady = chosenSuspect != null && chosenWeapon != null && chosenLocation != null &&
                (case!!.solutionLiar == null || chosenLiar != null)

        // Submit Button
        Button(
            onClick = { viewModel.submitAccusation() },
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "SUBMIT OFFICIAL DEDUCTION REPORT ⚖️",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccusationDropdownSection(
    title: String,
    options: List<String>,
    currentSelection: String?,
    onSelected: (String) -> Unit,
    iconColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = iconColor,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = currentSelection ?: "Choose connection...",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = iconColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (currentSelection == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. VICTORY AND FAILURE SCREENS
// ----------------------------------------------------
@Composable
fun VictoryScreen(viewModel: GameViewModel) {
    val currentCase by viewModel.activeCase.collectAsState()
    val finalSeconds by viewModel.elapsedTime.collectAsState()

    if (currentCase == null) return

    val totalMin = finalSeconds / 60
    val totalSec = finalSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🏆",
                fontSize = 62.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "CASE RESOLVED",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Deductions match physical evidence!",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Case File details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, Color(0xFF4CAF50))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "DEDUCTION DISPATCH SUMMARY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                AccusationSummaryRow("Primary Suspect", currentCase!!.solutionSuspect, "👤")
                AccusationSummaryRow("Assault Weapon", currentCase!!.solutionWeapon, "🔪")
                AccusationSummaryRow("Crime Scene", currentCase!!.solutionLocation, "📍")
                if (currentCase!!.solutionLiar != null) {
                    AccusationSummaryRow("Lying Witness", currentCase!!.solutionLiar!!, "⚠️")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Solving Time:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format("%02d min %02d sec", totalMin, totalSec),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Return buttons
        Button(
            onClick = { viewModel.changePhase(GamePhase.CASE_SELECT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "RETURN TO HEADQUARTERS",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun FailureScreen(viewModel: GameViewModel) {
    val currentCase by viewModel.activeCase.collectAsState()

    if (currentCase == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "❌",
                fontSize = 62.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "JUDGEMENT ERROR",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color(0xFFEF5350),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Your accusation contradicts some logs!",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Encouragement card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, Color(0xFFEF5350))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "INVESTIGATION LOG NOTES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "A key testimony or logic clue has been misaligned. Check your crosses and checkmarks inside the blueprints to double-check the relationships.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Action controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.changePhase(GamePhase.PLAYING); viewModel.startCaseTimer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "REVISE BLUEPRINTS (RESUME)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }

            OutlinedButton(
                onClick = { viewModel.restartCase() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "WIPE BLUEPRINT & RE-BEGIN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun AccusationSummaryRow(label: String, value: String, icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$icon $label:",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
