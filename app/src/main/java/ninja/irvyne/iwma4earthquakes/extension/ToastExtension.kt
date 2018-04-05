package ninja.irvyne.iwma4earthquakes.extension

import android.content.Context
import android.widget.Toast

inline fun Context.blabla(message: String): Toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
    show()
}