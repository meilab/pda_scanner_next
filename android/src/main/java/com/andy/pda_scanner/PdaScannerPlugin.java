package com.andy.pda_scanner;

import android.content.IntentFilter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry;

public class PdaScannerPlugin implements EventChannel.StreamHandler {
  public static final String CHANNEL = "com.andy.pda_scanner/plugin";
  public static final String SCAN_ACTION = "techain.intent.action.DISPLAY_SCAN_RESULT";
  public static final String ACTION_SCAN_UP = "techain.intent.action.KEY_SCAN_UP";
  public static final String DECODE_DATA = "decode_data";

  private static EventChannel.EventSink eventSink;

  private Activity activity;
  private static final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (SCAN_ACTION.equals(intent.getAction())) {
        String code = intent.getStringExtra(DECODE_DATA);
        eventSink.success(code);
      } else {
        Log.i("PdaScannerPlugin", "NoSuchAction");
      }
    }
  };

  private PdaScannerPlugin(Activity activity) {
    this.activity = activity;
    Intent intent = new Intent(ACTION_SCAN_UP);
    activity.sendBroadcast(intent);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(SCAN_ACTION);
    intentFilter.setPriority(Integer.MAX_VALUE);
    activity.registerReceiver(scanReceiver, intentFilter);
  }

  public static void registerWith(PluginRegistry.Registrar registrar) {
    EventChannel channel = new EventChannel(registrar.messenger(), CHANNEL);
    PdaScannerPlugin plugin = new PdaScannerPlugin(registrar.activity());
    channel.setStreamHandler(plugin);
  }

  @Override
  public void onListen(Object o, final EventChannel.EventSink eventSink) {
    PdaScannerPlugin.eventSink = eventSink;
  }

  @Override
  public void onCancel(Object o) {
    Log.i("PdaScannerPlugin", "PdaScannerPlugin:onCancel");
  }
}
