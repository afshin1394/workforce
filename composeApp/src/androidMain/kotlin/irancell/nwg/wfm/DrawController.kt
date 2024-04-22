package irancell.nwg.wfm


import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*


actual class DrawController  {



    actual companion object {

        private val _redoPathList = mutableStateListOf<PathWrapper>()
        private val _undoPathList = mutableStateListOf<PathWrapper>()
        internal val pathList: SnapshotStateList<PathWrapper> = _undoPathList


        private val _historyTracker = MutableSharedFlow<String>(extraBufferCapacity = 1)
        private val historyTracker = _historyTracker.asSharedFlow()

        private val _bitmapGenerators = MutableSharedFlow<Bitmap.Config>(extraBufferCapacity = 1)
        private val bitmapGenerators = _bitmapGenerators.asSharedFlow()


        var opacity by mutableStateOf(1f)
            private set

        var strokeWidth by mutableStateOf(10f)
            private set

        var color by mutableStateOf(Color.Red)
            private set

        var bgColor by mutableStateOf(Color.Black)
            private set
       actual fun trackHistory(
            scope: CoroutineScope,
            trackHistory: (undoCount: Int, redoCount: Int) -> Unit
        ) {
            historyTracker
                .onEach { trackHistory(_undoPathList.size, _redoPathList.size) }
                .launchIn(scope)
        }


      actual  fun saveBitmap( ) = _bitmapGenerators.tryEmit(Bitmap.Config.ARGB_8888)



        actual  fun changeOpacity(value: Float) {
            opacity = value
        }

        actual fun changeColor(value: Color) {
            color = value
        }

        actual fun changeBgColor(value: Color) {
            bgColor = value
        }

        actual fun changeStrokeWidth(value: Float) {
            strokeWidth = value
        }




        actual  fun unDo() {
            if (_undoPathList.isNotEmpty()) {
                val last = _undoPathList.last()
                _redoPathList.add(last)
                _undoPathList.remove(last)

                _historyTracker.tryEmit("Undo - ${_undoPathList.size}")
            }
        }

        actual fun reDo() {
            if (_redoPathList.isNotEmpty()) {
                val last = _redoPathList.last()
                _undoPathList.add(last)
                _redoPathList.remove(last)
                _historyTracker.tryEmit("Redo - ${_redoPathList.size}")
            }
        }


        actual fun reset() {
            _redoPathList.clear()
            _undoPathList.clear()
            _historyTracker.tryEmit("-")
        }

        actual fun updateLatestPath(newPoint: Offset) {
            val index = _undoPathList.lastIndex
            _undoPathList[index].points.add(newPoint)
        }

        actual fun insertNewPath(newPoint: Offset) {
            val pathWrapper = PathWrapper(
                points = mutableStateListOf(newPoint),
                strokeColor = color,
                alpha = opacity,
                strokeWidth = strokeWidth,
            )
            _undoPathList.add(pathWrapper)
            _redoPathList.clear()
            _historyTracker.tryEmit("${_undoPathList.size}")
        }

        actual fun trackBitmaps(
            it: Any,
            coroutineScope: CoroutineScope,
            onCaptured: (ImageBitmap?, Throwable?) -> Unit) {
            bitmapGenerators
                .mapNotNull { config -> (it as View).drawBitmapFromView(it.context, config) }
                .onEach { bitmap -> onCaptured(bitmap.asImageBitmap(), null) }
                .catch { error -> onCaptured(null, error) }
                .launchIn(coroutineScope)

        }
    }





}










