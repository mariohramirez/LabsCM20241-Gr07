/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.jetsnack.R
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.addHomeGraph
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.navigation.rememberJetsnackNavController
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackApp() {
    val snackViewModel: SnackViewModel =
        viewModel(factory = SnackViewModel.Factory)
    JetsnackTheme {
        val jetsnackNavController = rememberJetsnackNavController()
        when (snackViewModel.snackUiState){
            //Si se encuentra en proceso de carga, se llama al metodo de JetsnackLoading
            is SnackUiState.Loading -> JetsnackLoading()
            //Si tiene exito se carga el NavHost
            is SnackUiState.Success -> {
                NavHost(
                    navController = jetsnackNavController.navController,
                    startDestination = MainDestinations.HOME_ROUTE
                ) {
                    jetsnackNavGraph(
                        viewModel = snackViewModel,
                        onSnackSelected = jetsnackNavController::navigateToSnackDetail,
                        upPress = jetsnackNavController::upPress,
                        onNavigateToRoute = jetsnackNavController::navigateToBottomBarRoute
                    )
                }
            }
            //Si hay un error en la carha de datos se llama al metodo JetsnackError
            is SnackUiState.Error -> JetsnackError(retryAction = snackViewModel::getData)
        }

    }
}

private fun NavGraphBuilder.jetsnackNavGraph(
    viewModel: SnackViewModel,
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.FEED.route
    ) {
        addHomeGraph(viewModel, onSnackSelected, onNavigateToRoute)
    }
    composable(
        "${MainDestinations.SNACK_DETAIL_ROUTE}/{${MainDestinations.SNACK_ID_KEY}}",
        arguments = listOf(navArgument(MainDestinations.SNACK_ID_KEY) { type = NavType.LongType })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val snackId = arguments.getLong(MainDestinations.SNACK_ID_KEY)
        SnackDetail(viewModel, snackId, upPress)
    }
}

/**
 * Pantalla que se muestra cuando esta cargando los datos
 */
@Composable
fun JetsnackLoading(modifier: Modifier = Modifier) {
    Scaffold {
        Surface(
            color = Color.White,
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Image(
                modifier = modifier.size(200.dp),
                painter = painterResource(R.drawable.loading_img),
                contentDescription = "Loading"
            )
        }
    }

}

/**
 * Pantalla que se muestra cuando hay un error en la carga de los datos
 */
@Composable
fun JetsnackError(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold {
        Surface(
            color = Color.White,
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_connection_error),
                    contentDescription = ""
                )
                Text(text = "Loading failed", modifier = Modifier.padding(16.dp))
                Button(onClick = retryAction) {
                    Text(text="Retry", color = Color.White)
                }
            }
        }
    }
}