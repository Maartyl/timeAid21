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
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import maa.ljt.*


fun main() = application {
  AppTheme {
    //gathering
    val gws = rememberWindowState(
      size = DpSize(370.dp, 370.dp),
      position = WindowPosition((-450).dp, 888.dp)
    )

    //possibly: rememberWindowState of sleep notify here too

    val top = remember { TopState(gatheringWindow = gws) }

    TopTray(top)

    TopGatheringWindow(top)

    TopSleepNotifyWindow(top)

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