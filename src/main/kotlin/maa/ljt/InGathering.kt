package maa.ljt

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.mouseClickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TopGathering(gs: TopGatheringState) {
  var mouseIn by remember { mutableStateOf(false) }

  val mod = Modifier.pointerMoveFilter(onEnter = {
    mouseIn = true
    false
  }, onExit = {
    mouseIn = false
    false
  })
  val bg = if (mouseIn) {
    MaterialTheme.colors.primary.copy(alpha = 0.1f).compositeOver(MaterialTheme.colors.surface)
  } else {
    MaterialTheme.colors.surface
  }

  Modifier.mouseClickable { }

  Surface(
    modifier = mod.fillMaxSize(),
    color = bg,
    contentColor = contentColorFor(MaterialTheme.colors.surface),
    //cannot make the whole window rounded - transparent bg does not work
    //shape = RoundedCornerShape(8.dp),
  ) {

    Column {
      GBody(gs)

      //ModalBottomSheetLayout()

//      //TODO bottom bar
//      BottomDrawer(drawerContent = {
//        Column {
//          Text("bottom drawer content")
//          Image(Icons.Default.Add, "meh", Modifier.fillMaxSize())
//          Text("bottom drawer content")
//        }
//
//      }) {
//        Column {
//          Text("bottom content")
//
//        }
//
//      }
    }


  }

}

@Composable
fun GBody(gs: TopGatheringState) {
  Column {

    val w = gs.top.window
    Text("${w.size.height},${w.size.width} @ ${w.position.x},${w.position.y}", fontSize = 18.sp,
      modifier = Modifier.clickable {

      })

    val mod = Modifier.padding(8.dp).fillMaxWidth()

    /*
    TODO:
      thinking (useful / fantasizing / lost)
      tired / passive / effort / productive
      fun / "pointless" learning / working on own thing / "chores" / work

    TODO: once all options selected: make TIMER that takes few seconds to run out then CLOSES window
      - no need for OK button, ONLY: SUPPRESS button; can be later unsuppressed and timer starts then
      - one might sometimes make more complex input
      EVEN BETTER: keep it WHILE cursor is ON the window - once moved away - timer ticks

    * */

    Button(onClick = { gs.tmpHide() }, mod) {
      Text("Details")
    }

    Button(onClick = { gs.tmpHide() }, mod) {
      Text("was Away")
    }

    Button(onClick = { gs.tmpHide() }, mod) {
      Text("Fun")
    }

    Button(onClick = { gs.tmpHide() }, mod) {
      Text("Work")
    }

    Slider(0.5f, {})


  }
}