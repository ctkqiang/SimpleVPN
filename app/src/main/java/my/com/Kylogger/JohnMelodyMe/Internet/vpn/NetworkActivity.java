package my.com.Kylogger.JohnMelodyMe.Internet.vpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import my.com.Kylogger.JohnMelodyMe.Internet.vpn.Service.NetworkService;

/**
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 * @Class: NetworkActivity.class
 */

public class NetworkActivity extends AppCompatActivity {
    private static final String TAG = "VPN";
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static Button Network;
    private static boolean isVpnStarted;

    public void DeclarationInit() {
        Network = findViewById(R.id.network);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting " + NetworkActivity.class.getName());
        DeclarationInit();

        registerReceiver(vpnStateReceiver, new IntentFilter(NetworkService.BROADCAST_VPN_STATE));
        Network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVpnStarted) {
                    startVPN();
                    Network.setText(getResources().getString(R.string.stop));
                    Log.d(TAG, "VPN-STARTED");
                } else {
                    sendBroadcast(new Intent(NetworkService.BROADCAST_STOP_VPN));
                    Network.setText(getResources().getString(R.string.start));
                    Log.d(TAG, "VPN-STOPPED");
                }
            }
        });

        Network.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sendBroadcast(new Intent(NetworkService.BROADCAST_STOP_VPN));
                return false;
            }
        });
    }

    public void startVPN() {
        Intent vpnIntent = VpnService.prepare(NetworkActivity.this);
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        } else {
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
        }
    }

    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkService.BROADCAST_VPN_STATE.equals(intent.getAction())) {
                if (intent.getBooleanExtra("Running", false)) {
                    isVpnStarted = true;
                    Network.setText(getResources().getString(R.string.stop));
                } else {
                    isVpnStarted = false;
                    Network.setText(getResources().getString(R.string.start));
                    handler.postDelayed(runnable, 200);
                }
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopService(new Intent(NetworkActivity.this, NetworkService.class));
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        unregisterReceiver(vpnStateReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startService(new Intent(this, NetworkService.class));
        }
    }

}
