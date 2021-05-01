import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

public class MyUtil {
    companion object {

        //Check NetWork Connect is ok or NOT
        public fun isOnline(activity: Activity): Boolean {
            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }

}