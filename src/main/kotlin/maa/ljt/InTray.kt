package maa.ljt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.ZonedDateTime

@Composable
fun ApplicationScope.TopTray(top: TopState) {

  val icon = rememberVectorPainter(Icons.Default.Info)


  Tray(icon, tooltip = "Hint Test", onAction = {
    //== DOUBLECLICK for some reason
    println("test tray on action")
  }) {

    this.Menu("test menu") {
      this.Item("sub item") {
        println("sub item clicked")
      }
    }
    this.Item("test item") {
      println("test item clicked")
    }

    this.Item("Show") {
      top.showGather()
    }
  }

  LaunchedEffect(top) {
    tickerShowGather(top)
  }
}


suspend fun tickerShowGather(top: TopState) {
  //this is not ideal, but good enough for now

  //start of some day - used to ALIGN period
  val msBaseline = ZonedDateTime.now().with(LocalTime.MIN).toInstant().toEpochMilli()

  while (true) {
//    val ima = LocalDateTime.now()
//    val start = ima.with(LocalTime.MIN)
//    val msOfDay = ChronoUnit.MILLIS.between(start, ima)

    //ms from start of day - determines period offset (starting position)
    val msOfDay = System.currentTimeMillis() - msBaseline

    // 10 min
    val msPeriod = 10L * 60 * 1000
    // TMP 2 for testing
    //val msPeriod = 2L * 60 * 1000

    val msAlready = msOfDay.mod(msPeriod) //how far into the period now is
    val msLeft = msPeriod - msAlready

    println("ticker10min: $msLeft ms")
    delay(msLeft) //ms left until next period

    top.showGather()
  }
}