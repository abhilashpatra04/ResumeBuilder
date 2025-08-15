package com.example.resumebuilder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.resumebuilder.ui.screens.*
import com.example.resumebuilder.ui.screens.forms.PersonalInfoScreen
import com.example.resumebuilder.ui.screens.forms.ExperienceScreen

import com.example.resumebuilder.ui.screens.forms.SkillsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        addCoreGraph(navController)
    }
}

private fun NavGraphBuilder.addCoreGraph(navController: NavHostController) {
    composable(NavDestinations.Welcome) {
        WelcomeScreen(
            onCreateNew = { navController.navigate(NavDestinations.ResumeBuilder) },
            onMyResumes = { navController.navigate(NavDestinations.ResumeList) }
        )
    }

    composable(NavDestinations.ResumeList) {
        ResumeListScreen(
            onBack = { navController.popBackStack() },
            onCreateNew = { navController.navigate(NavDestinations.ResumeBuilder) },
            onOpenResume = { /* TODO open existing draft -> builder */ navController.navigate(NavDestinations.ResumeBuilder) }
        )
    }

    // Builder hub: shows step list & progress. From here go to individual form steps.
    composable(NavDestinations.ResumeBuilder) {
        ResumeBuilderScreen(
            onBack = { navController.popBackStack() },
            onPersonalInfo = { navController.navigate(NavDestinations.PersonalInfo) },
            onExperience = { navController.navigate(NavDestinations.Experience) },
            onSkills = { navController.navigate(NavDestinations.Skills) },
            onNextToTemplates = { navController.navigate(NavDestinations.TemplatePicker) }
        )
    }

    // Form steps
    composable(NavDestinations.PersonalInfo) {
        PersonalInfoScreen(
            onBack = { navController.popBackStack() },
            onNext = { navController.navigate(NavDestinations.Experience) }
        )
    }
    composable(NavDestinations.Experience) {
        ExperienceScreen(
            onBack = { navController.popBackStack() },
            onNext = { navController.navigate(NavDestinations.Skills) }
        )
    }
    composable(NavDestinations.Skills) {
        SkillsScreen(
            onBack = { navController.popBackStack() },
            onNext = { navController.navigate(NavDestinations.TemplatePicker) }
        )
    }

    // Template → Customize → Preview
    composable(NavDestinations.TemplatePicker) {
        TemplatePickerScreen(
            onBack = { navController.popBackStack() },
            onSelectTemplate = { navController.navigate(NavDestinations.Customize) }
        )
    }
    composable(NavDestinations.Customize) {
        CustomizeScreen(
            onBack = { navController.popBackStack() },
            onPreview = { navController.navigate(NavDestinations.Preview) }
        )
    }
    composable(NavDestinations.Preview) {
        PreviewScreen(
            onBack = { navController.popBackStack() },
            onExport = { /* Export then show success */ navController.navigate(NavDestinations.ExportSuccess) }
        )
    }

    // Export success
    composable(NavDestinations.ExportSuccess) {
        ExportSuccessScreen(
            onShare = { /* share intent handled inside */ },
            onCreateAnother = {
                navController.navigate(NavDestinations.ResumeBuilder) {
                    popUpTo(NavDestinations.Welcome) { inclusive = false }
                }
            },
            onHome = {
                navController.navigate(NavDestinations.Welcome) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}
