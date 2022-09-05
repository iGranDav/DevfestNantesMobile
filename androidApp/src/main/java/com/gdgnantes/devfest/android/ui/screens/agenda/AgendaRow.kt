package com.gdgnantes.devfest.android.ui.screens.agenda

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.ui.BookmarksViewModel
import com.gdgnantes.devfest.android.ui.components.SessionCategory
import com.gdgnantes.devfest.android.ui.theme.DevFest_NantesTheme
import com.gdgnantes.devfest.android.ui.theme.bookmarked
import com.gdgnantes.devfest.model.Session
import com.gdgnantes.devfest.model.stubs.buildSessionStub
import com.gdgnantes.devfest.utils.getDurationAndLanguageString

@Composable
fun AgendaRow(
    modifier: Modifier = Modifier,
    bookmarksViewModel: BookmarksViewModel = hiltViewModel(),
    session: Session,
    onSessionClick: ((Session) -> Unit)
) {
    AgendaRow(
        modifier = modifier,
        isBookmarked = bookmarksViewModel.subscribe(session.id).collectAsState(initial = false),
        session = session,
        onSessionClick = onSessionClick,
        onSessionBookmarkClick = { isBookmarked ->
            bookmarksViewModel.setBookmarked(session.id, isBookmarked)
        }
    )
}

@Composable
fun AgendaRow(
    modifier: Modifier = Modifier,
    isBookmarked: State<Boolean>,
    session: Session,
    onSessionClick: ((Session) -> Unit),
    onSessionBookmarkClick: ((Boolean) -> Unit)
) {
    AgendaRow(
        modifier = modifier,
        isBookmarked = isBookmarked.value,
        session = session,
        onSessionClick = onSessionClick,
        onSessionBookmarkClick = onSessionBookmarkClick
    )
}

@Composable
fun AgendaRow(
    modifier: Modifier = Modifier,
    isBookmarked: Boolean,
    session: Session,
    onSessionClick: ((Session) -> Unit),
    onSessionBookmarkClick: ((Boolean) -> Unit)
) {
    Column(modifier = modifier.clickable { onSessionClick(session) }) {
        Column(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 4.dp
            )
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.h5
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                session.category?.let { category ->
                    SessionCategory(category = category)
                }
                Text(
                    text = session.getDurationAndLanguageString(),
                    style = MaterialTheme.typography.subtitle1
                )
            }



            Row {
                Column(Modifier.weight(1F)) {
                    Text(
                        text = session.room.name,
                        style = MaterialTheme.typography.subtitle1
                    )

                    val speakers =
                        session.speakers.joinToString(", ") { it.getFullName() }
                    if (speakers.isNotBlank()) {
                        Text(
                            text = speakers,
                            style = MaterialTheme.typography.subtitle1,
                            //modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Box {
                    val icon =
                        if (isBookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark

                    val tint by animateColorAsState(
                        if (isBookmarked) bookmarked
                        else Color.LightGray
                    )

                    IconToggleButton(
                        checked = isBookmarked,
                        onCheckedChange = {
                            onSessionBookmarkClick(it)
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "favorite",
                            tint = tint
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .height(0.5.dp)
                .fillMaxWidth(),
            color = colors.onSurface
        ) {}
    }
}

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun AgendaRowPreview() {
    DevFest_NantesTheme {
        Scaffold {
            AgendaRow(
                isBookmarked = true,
                session = buildSessionStub(),
                onSessionClick = {},
                onSessionBookmarkClick = {}
            )
        }
    }
}