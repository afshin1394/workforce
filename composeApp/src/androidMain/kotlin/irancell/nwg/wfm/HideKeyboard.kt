package irancell.nwg.wfm
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context



actual fun HideKeyboard() {

        val context = provideAppContext() as? Context ?: return

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val activity = context as? Activity
        val view: View = activity?.window?.decorView?.findViewById(android.R.id.content) ?: return

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

}