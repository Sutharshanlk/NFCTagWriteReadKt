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

class Write_data : AppCompatActivity(),Listener {

    val TAG = MainActivity::class.java.simpleName

    private var mBtWrite: Button? = null
    private var mEtItemCode: EditText? = null
    private var mEtItemdescription: EditText? = null

    private var mNfcWriteFragment: NFCWriteFragment?=null

    private var isDialogDisplayed = false
    private var isWrite = false

    private var mNfcAdapter: NfcAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)

        initViews()
        initNFC()
    }

    private fun initViews() {
        mEtItemCode = findViewById<View>(R.id.etItemNumber) as EditText
        mBtWrite = findViewById<View>(R.id.btnWriteItemCode) as Button
        mEtItemdescription = findViewById<View>(R.id.etItemDescription) as EditText
        mBtWrite!!.setOnClickListener(View.OnClickListener {
            if (mEtItemCode!!.getText().toString().trim { it <= ' ' }.length != 0 && mEtItemdescription!!.getText().toString().trim { it <= ' ' }.length != 0
            ) {
                showWriteFragment()
            } else {
                Toast.makeText(this,
                    "Invalid Item code or Item Description",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    private fun showWriteFragment() {
        isWrite = true
        mNfcWriteFragment = supportFragmentManager.findFragmentByTag(NFCWriteFragment().TAG) as NFCWriteFragment?
        if (mNfcWriteFragment == null) {
            mNfcWriteFragment = NFCWriteFragment().newInstance()
        }
        mNfcWriteFragment!!.show(supportFragmentManager, NFCWriteFragment().TAG)
    }


    override fun onDialogDisplayed() {
        isDialogDisplayed = true
    }

    override fun onDialogDismissed() {
        isDialogDisplayed = false
        isWrite = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val tag = intent!!.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

        Log.d(com.example.administrator.nfctagwritereadkt.Write_data().TAG,
            "onNewIntent: " + intent!!.action
        )

        if (tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT)
                .show()
            val ndef = Ndef.get(tag)
            if (isDialogDisplayed) {
                if (isWrite) {
                    val messageToWrite = mEtItemCode!!.text.toString()
                        .trim { it <= ' ' } + "%" + mEtItemdescription!!.text.toString()
                        .trim { it <= ' ' }
                    //String messageToWrite[] = {mEtMessage.getText().toString(),"JW RED LABEL"};
                    mNfcWriteFragment = supportFragmentManager.findFragmentByTag(NFCWriteFragment().TAG) as NFCWriteFragment
                    mNfcWriteFragment!!.onNfcDetected(ndef, messageToWrite)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (mNfcAdapter != null) mNfcAdapter!!.disableForegroundDispatch(this)
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
        if (mNfcAdapter != null) mNfcAdapter!!.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null )
    }
}