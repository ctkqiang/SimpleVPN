package my.com.Kylogger.JohnMelodyMe.Internet.vpn;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class NetworkActivity extends AppCompatActivity {
    private static final String TAG = "VPN";
    private static final int VPN_REQUEST_CODE = 0x0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting " + NetworkActivity.class.getName());
    }
}
