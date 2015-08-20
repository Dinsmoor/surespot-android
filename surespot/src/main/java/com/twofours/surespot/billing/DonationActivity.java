package com.twofours.surespot.billing;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.twofours.surespot.R;
import com.twofours.surespot.common.SurespotLog;
import com.twofours.surespot.common.Utils;

public class DonationActivity extends Activity {

    protected static final String TAG = "BillingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
    }

    private Uri getPayPalUri() {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
        uriBuilder.appendQueryParameter("cmd", "_donations");

        uriBuilder.appendQueryParameter("business", Utils.getResourceString(this, "donations__paypal_user"));
        uriBuilder.appendQueryParameter("lc", "US");
        uriBuilder.appendQueryParameter("item_name", Utils.getResourceString(this, "donations__paypal_item_name"));
        uriBuilder.appendQueryParameter("no_note", "1");
        // uriBuilder.appendQueryParameter("no_note", "0");
        // uriBuilder.appendQueryParameter("cn", "Note to the developer");
        uriBuilder.appendQueryParameter("no_shipping", "1");
        uriBuilder.appendQueryParameter("currency_code", Utils.getResourceString(this, "donations__paypal_currency_code"));
        // uriBuilder.appendQueryParameter("bn", "PP-DonationsBF:btn_donate_LG.gif:NonHosted");
        return uriBuilder.build();

    }

    public void onPaypalBrowser(View arg0) {

        Uri payPalUri = getPayPalUri();
        SurespotLog.d(TAG, "Opening browser with url: %s", payPalUri);

        // Start your favorite browser
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
        startActivity(viewIntent);
    }


    public void onBitcoinClipboard(View arg0) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        String bitcoinAddy = Utils.getResourceString(this, "donations__bitcoin");

        ClipData clip = ClipData.newPlainText("Bitcoin Address", bitcoinAddy);
        clipboard.setPrimaryClip(clip);

        Utils.makeToast(this, getString(R.string.billing_bitcoin_copied_to_clipboard, bitcoinAddy));

    }

    public void onBitcoinWallet(View arg0) {
        String bitcoinAddy = Utils.getResourceString(this, "donations__bitcoin");

        Uri uri = Uri.parse("bitcoin:" + bitcoinAddy);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException anfe) {
            Utils.makeToast(this, getString(R.string.could_not_open_bitcoin_wallet));
        }

    }

    // @Override
    // protected void onSaveInstanceState(Bundle outState) {
    //
    // super.onSaveInstanceState(outState);
    //
    // outState.putBoolean("queried", mQueried);
    // }
    //
    // @Override
    // protected void onDestroy() {
    // super.onDestroy();
    // if (mIabHelper != null && mQueried) {
    // try {
    // mIabHelper.dispose();
    // } catch (Exception e) {
    // }
    // }
    //
    // mIabHelper = null;
    // }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
