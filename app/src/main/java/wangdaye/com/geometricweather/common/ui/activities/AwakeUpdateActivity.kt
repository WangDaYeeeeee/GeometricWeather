package wangdaye.com.geometricweather.common.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.polling.PollingManager

class AwakeUpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, R.string.refresh, Toast.LENGTH_SHORT).show()
        PollingManager.resetAllBackgroundTask(this, true)
        finish()
    }
}
