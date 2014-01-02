package com.nfcreader;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.widget.*;

public class MainActivity extends Activity {
	
	private TextView MessageView;
	private NfcManager manager;
	private NfcAdapter adapter;
	private IntentFilter nfc_tech;
	private IntentFilter[] nfcFilter;
	private PendingIntent nfcPendingIntent;
	private Intent nfc_intent;
	private String[][] tech_list;
    private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MessageView = (TextView)findViewById(R.id.MessageTextView);
		Decration();
	}
	
	private void Decration()
	{
		manager = (NfcManager)getSystemService(NFC_SERVICE);
		adapter = manager.getDefaultAdapter();
		nfc_intent = new Intent(this,getClass());
		nfcPendingIntent = PendingIntent.getActivity(this, 0, nfc_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		nfc_tech = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			nfc_tech.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) { }
		nfcFilter = new IntentFilter[]{nfc_tech};
		tech_list = new String[][]{new String[]{NfcA.class.getName()}};
	}  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
		{
			NdefMessage[] msg = getNdefMessages(intent);
			String str1 = new String(msg[0].getRecords()[0].getPayload());
			MessageView.setText(str1);
		}
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		adapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 處理由Android系統送出應用程式處理的intent filter內容
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
	        // 取得NdefMessage
	        NdefMessage[] messages = getNdefMessages(getIntent());
	        // 取得實際的內容
	        byte[] payload = messages[0].getRecords()[0].getPayload();
	        // 往下送出該intent給其他的處理對象
	        setIntent(new Intent()); 
	    }
		adapter.enableForegroundDispatch(this, nfcPendingIntent, nfcFilter, tech_list);
	}
	
	NdefMessage[] getNdefMessages(Intent intent) {
	    // Parse the intent
	    NdefMessage[] msgs = null;
	    String action = intent.getAction();
	    // 識別目前的action為何
	    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
	            || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	        // 取得parcelabelarrry的資料
	        Parcelable[] rawMsgs = 
	            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        // 取出的內容如果不為null，將parcelable轉成ndefmessage
	        if (rawMsgs != null) {
	            msgs = new NdefMessage[rawMsgs.length];
	            for (int i = 0; i < rawMsgs.length; i++) {
	                msgs[i] = (NdefMessage) rawMsgs[i];
	            }
	        } else {
	            // Unknown tag type
	            byte[] empty = new byte[] {};
	            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
	            NdefMessage msg = new NdefMessage(new NdefRecord[] {
	                record
	            });
	            msgs = new NdefMessage[] {
	                msg
	            };
	        }
	    } else {
	        Log.d(TAG, "Unknown intent.");
	        finish();
	    }
	    return msgs;
	}
}
