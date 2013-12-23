package com.nfcreader;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
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
		nfc_tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
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
		// TODO �۰ʲ��ͪ���k Stub
		super.onNewIntent(intent);
		String action = intent.getAction();
		if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
		{
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MessageView.setText(readTag(tagFromIntent));			
		}
	}

	public String readTag(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(2);
            
            return new String(payload,Charset.forName("US-ASCII"));
            //return payload.toString();
        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareUltralightmessage...", e);
            return null;
        }
    }
	

	@Override
	protected void onPause() {
		// TODO �۰ʲ��ͪ���k Stub
		super.onPause();
		adapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onResume() {
		// TODO �۰ʲ��ͪ���k Stub
		super.onResume();
		adapter.enableForegroundDispatch(this, nfcPendingIntent, nfcFilter, tech_list);
	}	
}
