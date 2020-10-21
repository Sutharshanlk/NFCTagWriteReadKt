package com.example.administrator.nfctagwritereadkt

import android.content.Context
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.tech.Ndef
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.skyfishjy.library.RippleBackground
import java.io.IOException
import java.nio.charset.Charset

class NFCWriteFragment: DialogFragment() {

    val TAG = NFCWriteFragment::class.java.simpleName

    fun newInstance(): NFCWriteFragment? {
        return NFCWriteFragment()
    }

    var mTvMessage: TextView? = null
    var mProgress: ProgressBar? = null
    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write, container, false)
        initViews(view)
        // animation
        val rippleBackground =
            view.findViewById<View>(R.id.wanimation) as RippleBackground
        rippleBackground.visibility = View.VISIBLE
        rippleBackground.startRippleAnimation()
        return view
    }

    private fun initViews(view: View) {
        mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        mProgress = view.findViewById<View>(R.id.progress) as ProgressBar
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Write_data
        mListener!!.onDialogDisplayed()
    }

    override fun onDetach() {
        super.onDetach()
        mListener!!.onDialogDismissed()
    }

    fun onNfcDetected(ndef: Ndef?, messageToWrite: String) {
        mProgress!!.visibility = View.VISIBLE
        writeToNfc(ndef, messageToWrite)
    }

    private fun writeToNfc(ndef: Ndef?, message: String) {
        mTvMessage!!.text = getString(R.string.message_write_progress)
        if (ndef != null) {
            try {
                ndef.connect()
                val mimeRecord = NdefRecord.createMime(
                    "text/plain",
                    message.toByteArray(Charset.forName("US-ASCII"))
                )
                // NdefRecord mimeRecord = NdefRecord.createMime(message[0].toString(), message[1].getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(NdefMessage(mimeRecord))
                ndef.close()
                //Write Successful
                mTvMessage!!.text = getString(R.string.message_write_success)
            } catch (e: IOException) {
                e.printStackTrace()
                mTvMessage!!.text = getString(R.string.message_write_error)
            } catch (e: FormatException) {
                e.printStackTrace()
                mTvMessage!!.text = getString(R.string.message_write_error)
            } finally {
                mProgress!!.visibility = View.GONE
            }
        }
    }
}