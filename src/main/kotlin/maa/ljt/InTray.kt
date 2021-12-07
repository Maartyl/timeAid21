package maa.ljt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZonedDateTime

@Composable
fun ApplicationScope.TopTray(top: TopState) {

  val icon = rememberVectorPainter(Icons.Default.Info)

  val cScope = rememberCoroutineScope()

  //onAction = {
  //    //== DOUBLECLICK for some reason
  //    println("test tray on action")
  //  }
  Tray(icon, tooltip = "Hint Test") {

    this.Menu("test") {}

    this.Item("Show") {
      top.showGather()
    }

    this.Item("Sleep") {
      cScope.launch { top.showSleepNotify() }
    }

    this.Item("Quit") {
      exitApplication()
    }
  }

  LaunchedEffect(top) {
    withContext(Dispatchers.Default) {
      tickerShowGather(top)
    }
  }
  LaunchedEffect(top) {
    withContext(Dispatchers.Default) {
      tickerSleepTime(top)
    }
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


suspend fun tickerSleepTime(top: TopState) {
  while (true) {
    //always needs to check again after long suspend - computer might have slept meanwhile etc.
    val tNow = ZonedDateTime.now()
    val boundsNow = SleepConf.boundsAround(tNow)

    if (boundsNow.hasWithinBounds(tNow)) {
      top.showSleepNotify()
    } else {
      //ChronoUnit.MILLIS.between(boundsNow.start, tNow) // wrong: I want negative millies, if already happened ... ?
      val waitFor = boundsNow.start.toInstant().toEpochMilli() - tNow.toInstant().toEpochMilli()
      delay(waitFor)
    }
  }
}