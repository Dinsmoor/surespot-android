package com.twofours.surespot.billing;

import android.content.Context;

import com.twofours.surespot.common.SurespotConstants;
import com.twofours.surespot.common.SurespotLog;
import com.twofours.surespot.network.IAsyncCallback;

public class BillingController {
	protected static final String TAG = "BillingController";


	private boolean mQueried;
	private boolean mQuerying;


	public static final int BILLING_QUERYING_INVENTORY = 100;
	private Context mContext;

	public BillingController(Context context) {
		mContext = context;
		setup(context, true, null);

	}

	public synchronized void setup(Context context, final boolean query, final IAsyncCallback<Integer> callback) {
		SurespotLog.v(TAG, "In-app Billing disabled");
	}


	private boolean isConsumable(String sku) {
		if (sku.equals(SurespotConstants.Products.VOICE_MESSAGING)) {
			return false;
		} else {
			if (sku.startsWith(SurespotConstants.Products.PWYL_PREFIX)) {
				return true;
			} else {

				return false;
			}
		}
	}

	public synchronized void dispose() {
		SurespotLog.v(TAG, "dispose");

		mQueried = false;
		mQuerying = false;
	}
}

