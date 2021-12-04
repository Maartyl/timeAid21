// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import maa.ljt.AppTheme
import maa.ljt.TopGathering
import maa.ljt.TopState
import maa.ljt.TopTray


fun main() = application {
  AppTheme {
    val initSize = DpSize(370.dp, 370.dp)
    val initPos = WindowPosition((-450).dp, 888.dp)
    val ws = rememberWindowState(size = initSize, position = initPos)

    val tops = remember { TopState(window = ws) }

    TopTray(tops)

    this.exitApplication()

    tops.gatheringInProgress.value?.let { gs ->
      //var gwVisible by remember { mutableStateOf(false) }
      Window(
        onCloseRequest = ::exitApplication,
        state = ws,
        title = "Gathering",
        alwaysOnTop = true,
        undecorated = true,
        focusable = false,
        //visible = gwVisible,
      ) {
        remember {
          //remember{}, because needs to run AS SOON as possible - before window shown
          window.isAutoRequestFocus = false
          window.focusableWindowState = false //aha! this works! - it allows hides it in window Mgr, but fine!

          //this seems to break interactivity ... ?
          //window.background = java.awt.Color(0,0,0,0)

          //window.isUndecorated = true
          //window.isVisible = false
          //gwVisible = true

          //window.toBack() //maybe this was forcing it the the last viirtual desktop?
          // YES!!! It was just this! - AND it is not needed to not be focused - NICE
          //FFS now it keeps opening in the wrong virtual desktop ... WHY ...
          // why does nothing just work sensibly????

          //OMG! It fixed itself! and now it is on ALL desktops!
          // - amazing!
        }

        TopGathering(gs)
      }
    }

//    Window(onCloseRequest = {}) {
//      Test()
//    }


  }
}


@Composable
@Preview
fun TestPreview() {
  AppTheme {
    Test()
  }
}

@Composable
fun Test() {
  Scaffold(
    topBar = {
      TopAppBar {
        Spacer(modifier = Modifier.size(8.dp))
        Text("test title")
      }
    }
  ) {
    Text("test content")
  }
}