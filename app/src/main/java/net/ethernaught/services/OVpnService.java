package net.ethernaught.services;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OVpnService extends VpnService {

    private ParcelFileDescriptor ointerface;
    private volatile boolean running = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Builder builder = new Builder();
        ointerface = builder.setSession("Ethernaut")
                .setMtu(20000)
                .addAddress("10.0.0.2", 32)
                .addDnsServer("8.8.8.8")
                .addRoute("0.0.0.0", 0)
                //.addRoute("172.25.0.1", 16)
                .establish();

        if(ointerface == null){
            stopSelf();
            return START_NOT_STICKY;
        }

        new Thread(new Runnable(){
            @Override
            public void run(){
                InputStream in = new FileInputStream(ointerface.getFileDescriptor());
                OutputStream out = new FileOutputStream(ointerface.getFileDescriptor());

                byte[] packet = new byte[2000];
                while(running){
                    try{
                        int length = in.read(packet);

                        if(length > 0){
                            printPacket(packet, length);
                            out.write(packet, 0, length);
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return START_STICKY;//super.onStartCommand(intent, flags, startId);//START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        running = false;

        try{
            if(ointerface != null){
                ointerface.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void printPacket(byte[] packet, int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X ", packet[i]));
            if ((i + 1) % 16 == 0) sb.append("\n"); // Format output nicely
        }
        System.out.println("Packet Data:\n" + sb.toString());
    }
}
