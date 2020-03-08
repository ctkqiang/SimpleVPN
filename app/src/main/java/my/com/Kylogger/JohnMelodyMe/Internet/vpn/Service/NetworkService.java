package my.com.Kylogger.JohnMelodyMe.Internet.vpn.Service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.com.Kylogger.JohnMelodyMe.Internet.vpn.Model.ByteBufferPool;
import my.com.Kylogger.JohnMelodyMe.Internet.vpn.NetworkActivity;
import my.com.Kylogger.JohnMelodyMe.Internet.vpn.R;

/**
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 * @Service: NETWORKSERVICE
 */

public class NetworkService extends VpnService {

    public static final String VPN_ADDRESS = "5.181.235.14";//5.181.235.14 (UK) // 219.100.37.215(JPN)// 168.168.168.168
    public static final String BROADCAST_VPN_STATE = "com.vpn.status";
    public static final String BROADCAST_STOP_VPN = "com.vpn.stop";
    private static final String TAG = "VPN";
    private static final String VPN_ROUTE = "0.0.0.0";
    private static final String VPN_DNS = "192.168.1.1";
    private ParcelFileDescriptor vpnInterface = null;
    private ExecutorService executorService;
    private VPNRunnable vpnRunnable;
    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (BROADCAST_STOP_VPN.equals(intent.getAction())) {
                onRevoke();
                stopVpn();

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(stopReceiver, new IntentFilter(BROADCAST_STOP_VPN));
        if (setupVPN()) {

            sendBroadcast(new Intent(BROADCAST_VPN_STATE).putExtra("running", true));
            vpnRunnable = new VPNRunnable(vpnInterface);
            executorService = Executors.newFixedThreadPool(1);
            executorService.submit(vpnRunnable);
        }
    }

    private boolean setupVPN() {
        try {
            if (vpnInterface == null) {
                Builder builder = new Builder();
                builder.addAddress(VPN_ADDRESS, 24);
                builder.addRoute(VPN_ROUTE, 0);

                Intent configure = new Intent(this, NetworkActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, configure, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setConfigureIntent(pi);

                vpnInterface = builder.setSession(getString(R.string.app_name)).establish();

            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void stopVpn() {

        if (vpnRunnable != null) {
            vpnRunnable.stop();
        }
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        vpnInterface = null;
        vpnRunnable = null;
        executorService = null;

        sendBroadcast(new Intent(BROADCAST_VPN_STATE).putExtra("running", false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVpn();
        unregisterReceiver(stopReceiver);
    }

    private static class VPNRunnable implements Runnable {
        private static final String TAG = VPNRunnable.class.getSimpleName();
        ParcelFileDescriptor vpnInterface;
        private boolean isStop;

        public VPNRunnable(ParcelFileDescriptor vpnInterface) {
            isStop = false;
            this.vpnInterface = vpnInterface;
        }

        public void stop() {
            isStop = true;
        }

        @Override
        public void run() {
            FileChannel vpnInput = new FileInputStream(vpnInterface.getFileDescriptor()).getChannel();
            FileChannel vpnOutput = new FileOutputStream(vpnInterface.getFileDescriptor()).getChannel();

            ByteBuffer bufferToNetwork = null;
            while (true) {
                if (isStop) {
                    vpnInterface = null;
                    break;
                }

                if (bufferToNetwork != null) {
                    bufferToNetwork.clear();
                } else {
                    bufferToNetwork = ByteBufferPool.acquire();
                }

                int readBytes = 0;
                try {
                    readBytes = vpnInput.read(bufferToNetwork);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (readBytes > 0) {
                    bufferToNetwork.flip();
                    Packet packet = null;
                    try {
                        packet = new Packet(bufferToNetwork, false);
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    String sIp = null;
                    if (packet.ip4Header.destinationAddress != null) {
                        sIp = packet.ip4Header.destinationAddress.getHostAddress();
                    }

                    if (packet.isUDP()) {
                        Log.i(TAG, "udp address:" + packet.ip4Header.sourceAddress.getHostAddress() + " udp port:"
                                + packet.udpHeader.sourcePort + " des:" + sIp + " des port:" + packet.udpHeader.destinationPort);

                    } else if (packet.isTCP()) {

                        Log.i(TAG, "tcp address:" + packet.ip4Header.sourceAddress.getHostAddress() + "tcp port:"
                                + packet.tcpHeader.sourcePort + " des:" + sIp + " des port:" + packet.tcpHeader.destinationPort);

                    } else if (packet.isPing()) {
                        Log.w(TAG, packet.ip4Header.toString());
                    } else {
                        Log.w(TAG, "Unknown packet type");
                        Log.w(TAG, packet.ip4Header.toString());
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}