package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.PortfolioSummary
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.InvestmentUiState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        DashboardScreen(
          state = InvestmentUiState(
            portfolioSummary = PortfolioSummary(
              totalInvested = 10500.0,
              totalWithdrawn = 7820.0,
              netProfitLoss = -2680.0,
              activeCapitalAtRisk = 3000.0,
              totalCapitalLostInFallen = 1250.0,
              activeProjectsCount = 3,
              fallenProjectsCount = 1,
              profitableProjectsCount = 1
            )
          ),
          onAddProjectClick = {},
          onAddWithdrawalClick = {},
          onProjectClick = {},
          onViewAllProjectsClick = {},
          onViewReportsClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
