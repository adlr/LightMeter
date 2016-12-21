package com.example.adlr.lightmeter;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by adlr on 12/21/16.
 */

public class TeensyConnection {
    private static final String TAG = "TeensyConnection";
    static final int TEENSY_VID = 0x16c0;
    private static final String USB_PERMISSION_RESPONSE_INTENT = "usb-permission-response";

    private Context mContext;

    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbConnection;
    private UsbEndpoint mEndpointIn;
    private UsbEndpoint mEndpointOut;

    TeensyConnection(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        context.registerReceiver(respondToUsbPermission, new IntentFilter(USB_PERMISSION_RESPONSE_INTENT));
        mContext = context;
    }

    BroadcastReceiver respondToUsbPermission = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mUsbDevice == null) {
                Log.v(TAG, "usb device was not properly opened");
                return;
            }
            if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                Log.v(TAG, "didn't get permission to open device");
                return;
            }
            // Hard-coded indices to the serial port of the device
            int ifIdx = 1;
            int epInIdx = 1;
            int epOutIdx = 0;
            UsbInterface iface = mUsbDevice.getInterface(ifIdx);
            if (!mUsbConnection.claimInterface(iface, true)) {
                Log.v(TAG, "couldn't claim interface");
                return;
            }
            mEndpointIn = iface.getEndpoint(epInIdx);
            mEndpointOut = iface.getEndpoint(epOutIdx);
            Log.v(TAG, "have endpoints!");
        }
    };

    public void connect() {
        Log.v(TAG, "connect called");
        UsbDevice dev = findUsbDevice();
        if (dev == null) {
            Log.v(TAG, "device not found");
            return;
        }
        mUsbDevice = dev;
        // Request permission
        // This displays a dialog asking user for permission to use the device.
        // No dialog is displayed if the permission was already given before or the app started as a
        // result of intent filter when the device was plugged in.

        PendingIntent permissionIntent = PendingIntent.getBroadcast(mContext, 0,
                new Intent(USB_PERMISSION_RESPONSE_INTENT), 0);
        Log.v(TAG, "Requesting permission for USB device.");
        mUsbManager.requestPermission(mUsbDevice, permissionIntent);
    }

    private UsbDevice findUsbDevice() {
        Log.v(TAG, "starting device search");
        HashMap<String, UsbDevice> deviceHash = mUsbManager.getDeviceList();
        if (deviceHash.isEmpty()) {
            Log.v(TAG, "No connected USB devices found");
            return null;
        }

        Log.v(TAG, "Found " + deviceHash.size() + " connected USB devices:");

        for (String key : deviceHash.keySet()) {

            UsbDevice dev = deviceHash.get(key);

            String msg = String.format(
                    "USB Device: %s, VID:PID - %x:%x, %d interfaces",
                    key, dev.getVendorId(), dev.getProductId(), dev.getInterfaceCount());


            if (dev.getVendorId() == TEENSY_VID) {
                msg += " <- using this one.";
                Log.v(TAG, msg);
                return dev;
            }

            Log.v(TAG, msg);

        }
        return null;
    }
}
