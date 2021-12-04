// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import maa.ljt.*


fun main() = application {
  AppTheme {
    //gathering
    val initSize = DpSize(370.dp, 370.dp)
    val initPos = WindowPosition((-450).dp, 888.dp)
    val gws = rememberWindowState(size = initSize, position = initPos)

    //sleep notify
    val snws = rememberWindowState(size = DpSize(1000.dp, 370.dp), position = WindowPosition((-1830).dp, 888.dp))

    val tops = remember { TopState(gatheringWindow = gws) }

    TopTray(tops)

    tops.gatheringInProgress.value?.let { gs ->
      //var gwVisible by remember { mutableStateOf(false) }
      Window(
        onCloseRequest = {},
        state = gws,
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

    //for DBG
    remember { tops.sleepNotifyInProgress.value = true }

    val showSleepNotify by tops.sleepNotifyInProgress.collectAsState()
    if (showSleepNotify) {
      Window(
        onCloseRequest = {},
        state = snws,
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

        TopSleepNotify(tops, snws)
      }
    }

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