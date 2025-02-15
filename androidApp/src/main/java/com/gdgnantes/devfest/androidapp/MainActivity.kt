package com.gdgnantes.devfest.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gdgnantes.devfest.analytics.AnalyticsPage
import com.gdgnantes.devfest.analytics.AnalyticsService
import com.gdgnantes.devfest.androidapp.services.ExternalContentService
import com.gdgnantes.devfest.androidapp.ui.screens.Home
import com.gdgnantes.devfest.androidapp.ui.screens.Screen
import com.gdgnantes.devfest.androidapp.ui.screens.datasharing.DataSharingAgreementDialog
import com.gdgnantes.devfest.androidapp.ui.screens.datasharing.DataSharingSettingsScreen
import com.gdgnantes.devfest.androidapp.ui.screens.legal.LegalScreen
import com.gdgnantes.devfest.androidapp.ui.screens.session.SessionLayout
import com.gdgnantes.devfest.androidapp.ui.screens.session.SessionViewModel
import com.gdgnantes.devfest.androidapp.ui.screens.settings.Settings
import com.gdgnantes.devfest.androidapp.ui.theme.DevFest_NantesTheme
import com.gdgnantes.devfest.androidapp.utils.assistedViewModel
import com.gdgnantes.devfest.model.SessionType
import com.gdgnantes.devfest.model.WebLinks
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), NavController.OnDestinationChangedListener {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun sessionViewModelFactory(): SessionViewModel.SessionViewModelFactory
    }

    @Inject
    lateinit var analyticsService: AnalyticsService

    @Inject
    lateinit var externalContentService: ExternalContentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            DevFest_NantesTheme {
                val mainNavController = rememberNavController()
                mainNavController.addOnDestinationChangedListener(this)

                NavHost(
                    navController = mainNavController,
                    startDestination = Screen.Home.route
                ) {
                    composable(route = Screen.Home.route) {
                        Home(
                            analyticsService = analyticsService,
                            externalContentService = externalContentService,
                            onSessionClick = { session ->
                                when (session.type) {
                                    SessionType.QUICKIE,
                                    SessionType.CONFERENCE,
                                    SessionType.CODELAB -> {
                                        mainNavController.navigate("${Screen.Session.route}/${session.id}")
                                    }
                                    else -> {}
                                }
                                analyticsService.eventSessionOpened(session.id)
                            },
                            onSettingsClick = { mainNavController.navigate(Screen.Settings.route) }
                        )
                    }

                    composable(
                        route = "${Screen.Session.route}/{sessionId}"
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments!!.getString("sessionId")!!
                        SessionLayout(
                            viewModel = assistedViewModel {
                                SessionViewModel.provideFactory(
                                    sessionViewModelFactory(),
                                    sessionId
                                )
                            },
                            onBackClick = { mainNavController.popBackStack() },
                            onSocialLinkClick = { socialItem, speaker ->
                                socialItem.link?.let { link ->
                                    externalContentService.openUrl(link)
                                    analyticsService.eventSpeakerSocialLinkOpened(
                                        speaker.id,
                                        socialItem.type
                                    )
                                }
                            }
                        )
                    }

                    composable(
                        route = Screen.Settings.route
                    ) {
                        Settings(
                            onBackClick = { mainNavController.popBackStack() },
                            onLegalClick = { mainNavController.navigate(Screen.Legal.route) },
                            onOpenDataSharing = { mainNavController.navigate(Screen.Legal.route) },
                            onSupportClick = {
                                externalContentService.openUrl(WebLinks.SUPPORT.url)
                                analyticsService.eventLinkSupportOpened()
                            }
                        )
                    }

                    composable(
                        route = Screen.DataSharing.route
                    ) {
                        DataSharingSettingsScreen(
                            onBackClick = { mainNavController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.Legal.route
                    ) {
                        LegalScreen(
                            onBackClick = { mainNavController.popBackStack() }
                        )
                    }
                }

                DataSharingAgreementDialog {
                    mainNavController.navigate(Screen.DataSharing.route)
                }
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        destination.route?.let { route ->
            when (route) {
                Screen.DataSharing.route -> analyticsService.pageEvent(
                    AnalyticsPage.DATASHARING,
                    route
                )
                Screen.Legal.route -> analyticsService.pageEvent(AnalyticsPage.LEGAL, route)
                Screen.Session.route -> analyticsService.pageEvent(
                    AnalyticsPage.SESSION_DETAILS,
                    route
                )
                Screen.Settings.route -> analyticsService.pageEvent(AnalyticsPage.SETTINGS, route)
            }
        }
    }
}