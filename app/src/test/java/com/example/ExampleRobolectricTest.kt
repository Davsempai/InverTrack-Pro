package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.InvestmentProject
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectWithWithdrawals
import com.example.data.model.RiskLevel
import com.example.data.model.WithdrawalTransaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("InverTrack", appName)
  }

  @Test
  fun `verify profit and loss calculation on fallen project`() {
    val project = InvestmentProject(
      id = 1,
      name = "Scam Yield Pool",
      category = "Crypto",
      initialCapital = 1000.0,
      status = ProjectStatus.CAIDO,
      riskLevel = RiskLevel.ESPECULATIVO
    )
    val withdrawals = listOf(
      WithdrawalTransaction(id = 1, projectId = 1, amount = 400.0)
    )
    val item = ProjectWithWithdrawals(project, withdrawals)

    assertEquals(400.0, item.totalWithdrawn, 0.001)
    assertEquals(-600.0, item.netProfitLoss, 0.001)
    assertEquals(600.0, item.capitalRemainingToRecover, 0.001)
    assertTrue(item.isFallen)
  }
}
