package com.tinyappsdev.tinypos.service;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.helper.ConfigCache;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class MessageService extends Service {
    private final static String TAG = MessageService.class.getSimpleName();

    private final IBinder mBinder = new Binder();

    private boolean mIsDone = false;
    private Thread mWorker;
    private SharedPreferences mSharedPreferences;
    private Account mSyncAccount;
    private boolean mIsStarted = false;
    private ServerAddress mServerAddress;
    private Handler mHandler;

    class ServerAddress {
        String address;
        int port;
    }

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        onStartCommand(null, 0, 0);
        return mBinder;
    }

    @Override
    public void onCreate() {
        mSharedPreferences = AppGlobal.getInstance().getSharedPreferences();
        mSyncAccount = SyncService.GetSyncAccount(getApplicationContext());
        setServerAddress(mSharedPreferences.getString("serverAddress", ""));

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(mHandler == null) return;
                if(msg.what == R.id.onServerInfoChanged) {
                    setServerAddress(mSharedPreferences.getString("serverAddress", ""));
                    if(mWorker != null) mWorker.interrupt();
                }
            }
        };
        AppGlobal.getInstance().registerMsgHandler(mHandler);
    }

    public void setServerAddress(String serverAddress) {
        if(serverAddress != null && !serverAddress.isEmpty()) {
            String[] parts = TextUtils.split(serverAddress, ":");
            String address = parts[0];
            int port = 0;
            try {
                if(parts.length >= 2)
                    port = Integer.parseInt(parts[1]) + 1;
                else
                    port = 8999;
            } catch(NumberFormatException e) {
                port = 0;
            }

            if(!address.isEmpty() && port > 0) {
                ServerAddress sa = new ServerAddress();
                sa.address = address;
                sa.port = port;
                mServerAddress = sa;
                return;
            }
        }
        mServerAddress = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mIsStarted) return START_STICKY;

        if(mServerAddress != null) {
            mIsStarted = true;
            mWorker = new Thread() {
                @Override
                public void run() {
                    doWork();
                }
            };
            mWorker.start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppGlobal.getInstance().unregisterMsgHandler(mHandler);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        stop();
    }

    private void stop() {
        mIsDone = true;
        if(mWorker != null) {
            mWorker.interrupt();
            mWorker = null;
        }
        mIsStarted = false;
    }

    private void requestSync(long lastDocEventId) {
        if(!mSharedPreferences.getBoolean("resyncDatabase", false)
                && mSharedPreferences.getLong("lastDocEventId", 0) >= lastDocEventId) return;
        if(ContentResolver.isSyncPending(mSyncAccount, getString(R.string.sync_authority))) return;
        if(ContentResolver.isSyncActive(mSyncAccount, getString(R.string.sync_authority))) return;

        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putLong("lastDocEventId", lastDocEventId);

        ContentResolver.requestSync(
                mSyncAccount,
                getString(R.string.sync_authority),
                extras
        );
    }

    protected void doWork() {
        DatagramSocket socket = null;
        long lastTs = 0;
        long lastDocEventId = 0;
        DatagramPacket packetSent = new DatagramPacket("REQU".getBytes(), 4);
        DatagramPacket packetRecv = new DatagramPacket(new byte[8], 8);
        long relaxMs = 0;
        ServerAddress sa = null;

        try {
            while(!mIsDone) {
                try {
                    Thread.sleep(relaxMs); //relax
                    relaxMs = 0;

                    requestSync(lastDocEventId);

                    if(socket == null || sa != mServerAddress) {
                        if(sa != mServerAddress) sa = mServerAddress;
                        if(sa == null) return;
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }

                        try {
                            Log.i(TAG, String.format("***Create UDP Socket %s:%s", sa.address, sa.port));
                            socket = new DatagramSocket();
                            socket.connect(InetAddress.getByName(sa.address), sa.port);
                            socket.setSoTimeout(3000);
                        } catch(SocketException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    if (System.currentTimeMillis() - lastTs > 5000) {
                        socket.send(packetSent);
                        if (lastTs == 0) packetSent.setData("WTCH".getBytes());
                        lastTs = System.currentTimeMillis();
                    }

                    Arrays.fill(packetRecv.getData(), (byte)0);
                    socket.receive(packetRecv);
                    lastDocEventId = ByteBuffer.wrap(packetRecv.getData())
                            .order(ByteOrder.LITTLE_ENDIAN).getLong();

                } catch(SocketTimeoutException e) {

                } catch (InterruptedException e) {

                } catch (IOException e) {
                    //e.printStackTrace();
                    relaxMs = 5000;
                }
            }

        } finally {
            if(socket != null)
                socket.close();
        }

    }
}
