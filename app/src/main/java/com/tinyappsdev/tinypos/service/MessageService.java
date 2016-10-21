package com.tinyappsdev.tinypos.service;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.ui.SyncAllActivity;

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

    private final IBinder mBinder = new Binder();

    private boolean mIsDone = false;
    private Thread mWorker;
    private ConfigCache mConfigCache;
    private Account mSyncAccount;
    private boolean mIsStarted = false;

    public MessageService() {
        Log.i("PKT", ">>>>>>>>>>>>>>>MessageService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("PKT", ">>>>>>>>>>>>>>>onBind");
        onStartCommand(null, 0, 0);

        return mBinder;
    }

    @Override
    public void onCreate() {
        mConfigCache = ConfigCache.getInstance(getApplicationContext());
        mSyncAccount = SyncService.GetSyncAccount(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mIsStarted) return START_STICKY;
        mIsStarted = true;

        Log.i("PKT", ">>>>>>>>>>>>>>>onStartCommand");
        mWorker = new Thread() {
            @Override
            public void run() {
                doWork();
            }
        };
        mWorker.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("PKT", ">>>>>>>>>>>>>>>onDestroy");
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
        //Log.i("PKT", String.format(">> %d", mConfigCache.getInt("syncAll")));
        if(mConfigCache.getInt("syncAll") == 0 && mConfigCache.getLong("lastDocEventId") >= lastDocEventId) return;
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
        Log.i("PKT", ">>>>requestSync->Done");
    }

    protected void doWork() {
        DatagramSocket socket = null;
        long lastTs = 0;
        long lastDocEventId = 0;
        DatagramPacket packetSent = new DatagramPacket("REQU".getBytes(), 4);
        DatagramPacket packetRecv = new DatagramPacket(new byte[8], 8);
        long relaxMs = 0;

        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("192.168.1.109"), 8889);
            socket.setSoTimeout(1000);

            while(!mIsDone) {
                try {
                    Thread.sleep(relaxMs); //relax
                    relaxMs = 0;

                    requestSync(lastDocEventId);

                    if (System.currentTimeMillis() - lastTs > 5000) {
                        socket.send(packetSent);
                        if (lastTs == 0) packetSent.setData("WTCH".getBytes());
                        lastTs = System.currentTimeMillis();
                    }

                    Arrays.fill(packetRecv.getData(), (byte)0);
                    socket.receive(packetRecv);
                    lastDocEventId = ByteBuffer.wrap(packetRecv.getData()).order(ByteOrder.LITTLE_ENDIAN).getLong();

                } catch(SocketTimeoutException e) {

                } catch (IOException e) {
                    relaxMs = 500;
                    e.printStackTrace();

                } catch (InterruptedException e) {

                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if(socket != null)
                socket.close();

            Log.i("PKT", ">>>>MessageService->Done");
        }

    }
}
