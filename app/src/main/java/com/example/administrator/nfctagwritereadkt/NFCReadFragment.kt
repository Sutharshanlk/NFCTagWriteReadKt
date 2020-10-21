package com.example.administrator.nfctagwritereadkt


import android.content.Context
import android.nfc.FormatException
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment;

import com.skyfishjy.library.RippleBackground
import java.io.IOException

class NFCReadFragment: DialogFragment() {
    val TAG = NFCReadFragment::class.java.simpleName

    fun newInstance(): NFCReadFragment? {
        return NFCReadFragment()
    }

     var mTvMessage: TextView? = null
     private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_read, container, false)
        initViews(view)

        // animation
        val rippleBackground =
            view.findViewById<View>(R.id.animation) as RippleBackground
        rippleBackground.visibility = View.VISIBLE
        rippleBackground.startRippleAnimation()
        return view
    }

    private fun initViews(view: View) {
        mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as MainActivity
        mListener!!.onDialogDisplayed()
    }

    override fun onDetach() {
        super.onDetach()
        mListener!!.onDialogDismissed()
    }

    fun onNfcDetected(ndef: Ndef?) {
        if (ndef != null) {
            readFromNFC(ndef)
        }
    }

    private fun readFromNFC(ndef: Ndef) {
        try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            if (ndefMessage != null) {
                val message = String(ndefMessage.records[0].payload)
                if (message.trim { it <= ' ' }.length != 0) {
                    if (message.contains("%")) {
                        val itemData =
                            message.split("\\%".toRegex()).toTypedArray()
                        Log.d(TAG, "readFromNFC: $message")
                        mTvMessage!!.text = "SKU " + itemData[0] + " " + itemData[1]
                    } else {
                        Log.d(TAG, "readFromNFC: $message")
                        mTvMessage!!.text = message
                    }
                }
            } else {
                mTvMessage!!.text = "No Data in the Tag.."
            }
            ndef.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
    }
}