package com.philips.lighting.quickstart.DataClass.ThreadClass;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.philips.lighting.quickstart.Activity.MyApplicationActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;



public class NFCWriteTask extends AsyncTask<Tag, Void, String> {

    private MyApplicationActivity activity;
    private String text;

    public  final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";

    public NFCWriteTask(MyApplicationActivity activity, String text){
        this.activity = activity;
        this.text = text;
    }

    @Override
    protected String doInBackground(Tag... params) {

        Tag tag = params[0];

        NdefRecord[] records = new NdefRecord[0];
        try {

            records = new NdefRecord[]{createRecord(text)};

            NdefMessage message = new NdefMessage(records);
            // Get an instance of Ndef for the tag.
            Ndef ndef = Ndef.get(tag);
            // Enable I/O
            ndef.connect();
            // Write the message
            ndef.writeNdefMessage(message);
            // Close the connection
            ndef.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            Log.i("THREAD WRITE: ", WRITE_ERROR);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("THREAD WRITE: ", WRITE_ERROR);
            e.printStackTrace();
        }

        return null;
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
           // activity.Toast("Success on writing to NFC TAG");
        }
    }
}
