package com.example.administrator.nfctagwritereadkt

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(),Listener {

    val TAG = MainActivity::class.java.simpleName

    private val mEtMessage: EditText? = null
    private var mBtWrite: Button? = null
    private var mBtRead: Button? = null

    private var mNfcWriteFragment: NFCWriteFragment? = null
    private var mNfcReadFragment: NFCReadFragment? = null

    private var isDialogDisplayed = false
    private var isWrite = false

    private var mNfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initNFC()
    }
    private fun initViews() {

        // mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtWrite = findViewById<View>(R.id.btn_write) as Button
        mBtRead = findViewById<View>(R.id.btn_read) as Button
        mBtRead!!.setOnClickListener(View.OnClickListener { showReadFragment() })
        mBtWrite!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, Write_data::class.java)
            startActivity(intent)
            //showWriteFragment();
        })
    }


    private fun initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    private fun showReadFragment() {
        mNfcReadFragment = supportFragmentManager.findFragmentByTag(NFCReadFragment().TAG) as NFCReadFragment?

        if (mNfcReadFragment == null) {
            mNfcReadFragment = NFCReadFragment().newInstance()
        }
        mNfcReadFragment!!.show(supportFragmentManager, NFCReadFragment().TAG)
    }

    override fun onDialogDisplayed() {
        isDialogDisplayed = true
    }

    override fun onDialogDismissed() {
        isDialogDisplayed = false
        isWrite = false
    }

    override fun onResume() {
        super.onResume()

        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val nfcIntentFilter =
            arrayOf(techDetected, tagDetected, ndefDetected)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        if (mNfcAdapter != null) mNfcAdapter!!.enableForegroundDispatch(
            this,
            pendingIntent,
            nfcIntentFilter,
            null
        )
    }

    override fun onPause() {
        super.onPause()

        if (mNfcAdapter != null) mNfcAdapter!!.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val tag = intent!!.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

        Log.d(TAG, "onNewIntent: " + intent.action)

        if (tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT)
                .show()
            val ndef = Ndef.get(tag)
            if (isDialogDisplayed) {
                if (isWrite) {
                    val messageToWrite = mEtMessage!!.text.toString()
                    //String[] messageToWrite= {mEtMessage.getText().toString(),"JW RED LABEL"};
                    mNfcWriteFragment = supportFragmentManager.findFragmentByTag(NFCWriteFragment().TAG) as NFCWriteFragment
                    mNfcWriteFragment!!.onNfcDetected(ndef, messageToWrite)
                } else {
                    mNfcReadFragment =supportFragmentManager.findFragmentByTag(NFCReadFragment().TAG) as NFCReadFragment
                    mNfcReadFragment!!.onNfcDetected(ndef)
                }
            }
        }
    }
}