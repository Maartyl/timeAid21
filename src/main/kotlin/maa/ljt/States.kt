package maa.ljt

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

//scope that lives for FULL duration of Process
class TopState(
  //state reused each time window is open
  // window for Gathering
  val gatheringWindow: WindowState,

  val gatheringInProgress: MutableState<TopGatheringState?> = mutableStateOf(null),
  val sleepNotifyInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false),
) {
  fun showGather() {
    val g = gatheringInProgress.value
    if (g == null) {
      gatheringInProgress.value = TopGatheringState(this)
    } else {
      g.countsFor.value++
    }
  }

  // waits until window CLOSED - then lets ticker continue
  // - usually: PC should have SLEPT by then
  suspend fun showSleepNotify() {
    sleepNotifyInProgress.value = true
    sleepNotifyInProgress.first { !it } //wait untill off
  }
}

//scope that lives only AS LONG AS gathering window shown
class TopGatheringState(
  val top: TopState,

  //how many gathers waited while this shown
  val countsFor: MutableState<Int> = mutableStateOf(1),
) {

  fun tmpHide() {
    top.gatheringInProgress.value = null
  }
}