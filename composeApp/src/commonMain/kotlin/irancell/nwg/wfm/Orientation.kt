package irancell.nwg.wfm

expect class Orientation {
    companion object {

        fun orientationState(context: Any, onChange: (state:String) -> Unit)
    }
}