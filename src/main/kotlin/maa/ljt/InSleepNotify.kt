package maa.ljt

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.io.path.pathString
import kotlin.math.tanh

//instantiated to a specific DAY
// - all fields are a function of that day, just precomputed
data class SleepNotifyBounds(
  //the reference time, the bounds were created FOR
  val ref: ZonedDateTime,
  val start: ZonedDateTime,
  val strong: ZonedDateTime = start.with(SleepConf.NOTIFY_STRONG),
  val end: ZonedDateTime = start.plus(SleepConf.NOTIFY_DURATION),
) {

  fun hasWithinBounds(point: ZonedDateTime) = point.isAfter(start) && point.isBefore(end)
  val refWithinBounds get() = hasWithinBounds(ref)

  fun refFracOfStrong(): Float {
    if (ref < start) return 0f
    if (ref > strong) return 1f

    val len = ChronoUnit.MILLIS.between(start, strong)
    val pos = ChronoUnit.MILLIS.between(start, ref)
    return pos.toFloat() / len
  }
}

object SleepConf {
  val NOTIFY_START: LocalTime = LocalTime.of(20, 30)
  val NOTIFY_STRONG: LocalTime = LocalTime.of(23, 0)
  val NOTIFY_DURATION: Duration = Duration.ofHours(6)

  val DERU_JIKAN = LocalTime.of(10, 0)

  fun boundsAround(now: ZonedDateTime): SleepNotifyBounds {

    val timeEnd = NOTIFY_START.plus(NOTIFY_DURATION) // only hours: wraps and overflows
    val someStart = now.with(NOTIFY_START)
    //now I get the time today - instead of adding duration here, which would push it to tomorrow
    val someEnd = now.with(timeEnd)

    return SleepNotifyBounds(
      now,
      if (now < someEnd) {
        //still is active from "yesterday"
        (someStart.minusDays(1))
      } else {
        //either active "today" or WILL BE - return that
        (someStart)
      }
    )
  }

  fun currentBounds() = boundsAround(ZonedDateTime.now())
}

//private val xxxTimeFmt = DateTimeFormatter.ISO_LOCAL_TIME
private val xxxTimeFmt = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun TopSleepNotifyWindow(top: TopState) {
  val ws = rememberWindowState(size = DpSize(1000.dp, 370.dp), position = WindowPosition((-1830).dp, 888.dp))

  //for DBG
  remember { top.sleepNotifyInProgress.value = true }

  val showSleepNotify by top.sleepNotifyInProgress.collectAsState()
  if (showSleepNotify) {
    Window(
      onCloseRequest = {},
      state = ws,
      title = "Sleep Notify",
      alwaysOnTop = true,
      undecorated = true,
      focusable = false,
      transparent = true,
      //visible = gwVisible,
    ) {
      remember {
        //remember{}, because needs to run AS SOON as possible - before window shown
        window.isAutoRequestFocus = false
        window.focusableWindowState = false //aha! this works! - it allows hides it in window Mgr, but fine!
      }

      TopSleepNotify(top, ws)
    }
  }
}

@Composable
fun TopSleepNotify(top: TopState, ws: WindowState) {

  val mod = Modifier
  val cScope = rememberCoroutineScope { Dispatchers.Default }

  //prevent actions right after appearing, in case
  val actionsEnabled = produceState(false) {
    delay(Duration.ofSeconds(10).toMillis())
    value = true
  }

  //tmp - offset
  //val clrFS = remember { mutableStateOf(0.5f) }
  val clrTmp = remember { mutableStateOf(0.3f) }

  val curBoundsState = produceState(remember { SleepConf.currentBounds() }, clrTmp.value) {
    while (true) {

      //value = SleepConf.currentBounds()
      value =
        SleepConf.boundsAround(
          ZonedDateTime.now().plusSeconds((clrTmp.value * ChronoUnit.DAYS.duration.toSeconds()).toLong())
        ).also { top.sleepNotifyInProgress.value = it.refWithinBounds }

      delay(10 * 1000)
    }
  }
  val curBounds = curBoundsState.value
  val curTime = curBounds.ref

  //var clrF by clrFS
  val clrF = curBounds.refFracOfStrong()

//  val alfFS = remember { mutableStateOf(0.5f) }
//  var alfF2 by alfFS

  //I want full opacity at 1/3 clrF (tanh gives 1 after PI)
  val alfF = tanh(clrF * Math.PI * 3).toFloat()

  val cyan = Color.Cyan.copy(alpha = 0.2f * (1 - clrF))
  val red = Color.Red.copy(alpha = 0.5f * (clrF))

//  //val surLab = MaterialTheme.colors.surface.convert(ColorSpaces.CieLab)
//  val lerpC = Color.Cyan.copy(alpha = 0.2f).compositeOver(MaterialTheme.colors.surface)
//  val lerpR = Color.Red.copy(alpha = 0.5f).compositeOver(MaterialTheme.colors.surface)
//  val bg2 = lerp(lerpC, lerpR, clrF)

  //interestingly, I visually prefer the weird interpolation I made...
  val bg = red.compositeOver(cyan.compositeOver(MaterialTheme.colors.surface)).copy(alpha = alfF)

  Surface(
    modifier = mod.fillMaxSize(), //.alpha(alfF)
    color = bg,
    contentColor = contentColorFor(MaterialTheme.colors.surface),
    //cannot make the whole window rounded - transparent bg does not work
    //shape = RoundedCornerShape(8.dp),
  ) {

    Column {
//      val w = ws
//      Text(
//        "${w.size.height},${w.size.width} @ ${w.position.x},${w.position.y}", fontSize = 18.sp,
//        modifier = Modifier.fillMaxWidth()
//      )

      //Slider(clrF, onValueChange = { clrF = it }, modifier = Modifier.fillMaxWidth())
      Slider(clrTmp.value, onValueChange = { clrTmp.value = it }, modifier = Modifier.fillMaxWidth())
      //Slider(alfF, onValueChange = { alfF = it }, modifier = Modifier.fillMaxWidth())


      Row(modifier = mod.weight(1f).fillMaxWidth()) {

        //TODO: clock of remaining time
        //time of waking up if went to sleep NOW (safe-ish)
        val wakeTime = curTime.plusHours(8).plusMinutes(40)

        Column(mod.padding(10.dp)) {

          Text("今 " + curTime.format(xxxTimeFmt), mod, fontSize = 80.sp)
          Spacer(mod.size(10.dp))
          //o.kiru
          Text("起 " + wakeTime.format(xxxTimeFmt), mod, fontSize = 80.sp)
        }

        Column(mod.padding(10.dp)) {

          val rmng = Duration.between(wakeTime, wakeTime.with(SleepConf.DERU_JIKAN))
          Text("残 ${rmng.toHours()}+${"%02d".format(rmng.toMinutesPart())}", mod, fontSize = 80.sp)
          //残 noko.ri - remaining
        }

        Spacer(mod.weight(1f))

        Column(mod) {
          //TODO:FUTURE: also allow writing more stuff
          // - i.e. "what are you grateful for?" etc.
          // - then save all

          Button(onClick = {
            cScope.launch { doSleepPC(top) }
          }, mod.padding(10.dp), enabled = actionsEnabled.value) {
            Text("Spát")
          }
        }
      }

      SplitBar(clrF, red)
    }

  }


}

@Composable
fun SplitBar(
  //fraction to fill; RANGE:  0.0 - 1.0
  f: Float,
  colorLeft: Color = MaterialTheme.colors.surface,
  colorRight: Color = MaterialTheme.colors.surface,
  mod: Modifier = Modifier
) {
  Row(mod) {
    //IFS are needed, because weight 0 throws
    val nonFull = ((f - 0.5f) * 0.9999f) + 0.5f //nice: not visible, and does not throw
    //if (clrF > 0.0)
    Surface(mod.height(40.dp).weight(nonFull), color = colorLeft) { }
    //if (clrF < 1.0f)
    Surface(mod.height(40.dp).weight(1 - nonFull), color = colorRight) { }
  }
}


suspend fun doSleepPC(top: TopState) {
  //TODO: write before - will sleep
  // - after: did sleep

  withContext(Dispatchers.IO) {
//    System.getenv().forEach { (t, u) ->
//      println("ENV: $t   -->   $u")
//    }

    //to TEST
    //val cmd = Path.of(System.getenv("USERPROFILE"), "dev\\tools\\delay1000.bat")

    //this invokes bat -> PowerShell -> [System.Windows.Forms.Application]::SetSuspendState
    val cmd = Path.of(System.getenv("USERPROFILE"), "dev\\tools\\sleep.bat")
    val p = Runtime.getRuntime().exec(cmd.pathString)
//    launch {
//      println("ERR: ${InputStreamReader(p.errorStream).readText()}")
//    }
//    launch {
//      println("OUT: ${InputStreamReader(p.inputStream).readText()}")
//    }
    p.waitFor()
  }

  top.sleepNotifyInProgress.value = false
}