package maa.ljt

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.pathString

//instantiated to a specific DAY
// - all fields are a function of that day, just precomputed
data class SleepNotifyBounds(
  val start: ZonedDateTime,
  val strong: ZonedDateTime = start.with(SleepConf.NOTIFY_STRONG),
  val end: ZonedDateTime = start.plus(SleepConf.NOTIFY_DURATION),
) {

  fun hasWithinBounds(point: ZonedDateTime) = point.isAfter(start) && point.isBefore(end)
}

object SleepConf {
  val NOTIFY_START: LocalTime = LocalTime.of(21, 0)
  val NOTIFY_STRONG: LocalTime = LocalTime.of(23, 0)
  val NOTIFY_DURATION: Duration = Duration.ofHours(6)

  fun boundsAround(now: ZonedDateTime): SleepNotifyBounds {

    val timeEnd = NOTIFY_START.plus(NOTIFY_DURATION) // only hours: wraps and overflows
    val someStart = now.with(NOTIFY_START)
    //now I get the time today - instead of adding duration here, which would push it to tomorrow
    val someEnd = now.with(timeEnd)

    return SleepNotifyBounds(
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
fun TopSleepNotify(top: TopState, ws: WindowState) {

  val cScope = rememberCoroutineScope { Dispatchers.Default }

  //TODO: produce state
  val curTime = ZonedDateTime.now()

  val mod = Modifier


  val clrFS = remember { mutableStateOf(0.5f) }
  var clrF by clrFS

  val alfFS = remember { mutableStateOf(0.5f) }
  var alfF by alfFS

  val cyan = Color.Cyan.copy(alpha = 0.2f * (1 - clrF))
  val red = Color.Red.copy(alpha = 0.5f * (clrF))

  //val surLab = MaterialTheme.colors.surface.convert(ColorSpaces.CieLab)
  val lerpC = Color.Cyan.copy(alpha = 0.2f).compositeOver(MaterialTheme.colors.surface)
  val lerpR = Color.Red.copy(alpha = 0.5f).compositeOver(MaterialTheme.colors.surface)

  //interestingly, I prefer the weird interpolation I made...
  val bg2 = lerp(lerpC, lerpR, clrF)

  val bg = red.compositeOver(cyan.compositeOver(MaterialTheme.colors.surface)).copy(alpha = alfF)

  Surface(
    modifier = mod.fillMaxSize(),
    color = bg,
    contentColor = contentColorFor(MaterialTheme.colors.surface),
    //cannot make the whole window rounded - transparent bg does not work
    //shape = RoundedCornerShape(8.dp),
  ) {

    Column {
      val w = ws
//      Text(
//        "${w.size.height},${w.size.width} @ ${w.position.x},${w.position.y}", fontSize = 18.sp,
//        modifier = Modifier.fillMaxWidth()
//      )

      Slider(clrF, onValueChange = { clrF = it }, modifier = Modifier.fillMaxWidth())
      Slider(alfF, onValueChange = { alfF = it }, modifier = Modifier.fillMaxWidth())



      Row(modifier = mod.weight(1f).fillMaxWidth()) {

        //TODO: clock of remaining time

        Column(mod.padding(10.dp).weight(1f)) {
//          Text("LEFT", mod, fontSize = 70.sp)
//          Spacer(mod.size(10.dp))
          val tmstr = curTime.format(xxxTimeFmt)
          Text(tmstr, mod, fontSize = 80.sp)
        }

        Column(mod) {
          //TODO:FUTURE: also allow writing more stuff
          // - i.e. "what are you grateful for?" etc.
          // - then save all

          Button(onClick = {
            cScope.launch { doSleepPC(top) }

          }, mod.padding(10.dp)) {
            Text("Spát")
          }
        }
      }

      Row(mod) {
        //IFS are needed, because weight 0 throws
        val nonFull = ((clrF - 0.5f) * 0.9999f) + 0.5f //nice: not visible, and does not throw
        //if (clrF > 0.0)
        Surface(mod.height(40.dp).weight(nonFull), color = red) { }
        //if (clrF < 1.0f)
        Surface(mod.height(40.dp).weight(1 - nonFull)) { }
      }

      //TODO: progress bar to end
    }

  }


}


suspend fun doSleepPC(top: TopState) {
  //TODO: write before - will sleep
  // - after: did sleep

  println("A")

  withContext(Dispatchers.IO) {
//    System.getenv().forEach { (t, u) ->
//      println("ENV: $t   -->   $u")
//    }
    //val cmd = Path.of(System.getenv("USERPROFILE"), "dev\\tools\\delay1000.bat")
    val cmd = Path.of(System.getenv("USERPROFILE"), "dev\\tools\\sleep.bat")
    Runtime.getRuntime().exec(cmd.pathString).waitFor()
  }
  println("B")

  //TODO: notify here to hide
}