package com.twofours.surespot.backup;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.twofours.surespot.R;
import com.twofours.surespot.common.FileUtils;
import com.twofours.surespot.common.Utils;
import com.twofours.surespot.identity.IdentityController;
import com.twofours.surespot.network.IAsyncCallback;
import com.twofours.surespot.ui.SingleProgressDialog;
import com.twofours.surespot.ui.UIUtils;

import java.io.File;
import java.util.List;

public class ExportIdentityActivity extends SherlockActivity {
	private static final String TAG = "ExportIdentityActivity";
	private List<String> mIdentityNames;
	private Spinner mSpinner;

	private TextView mAccountNameDisplay;
	private SingleProgressDialog mSpd;
	private SingleProgressDialog mSpdBackupDir;
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export_identity);

		Utils.configureActionBar(this, getString(R.string.identity), getString(R.string.backup), true);
		final String identityDir = FileUtils.getIdentityExportDir().toString();

		TextView tvBackupWarning = (TextView) findViewById(R.id.backupIdentitiesWarning);
		Spannable s1 = new SpannableString(getString(R.string.help_backupIdentities1));
		s1.setSpan(new ForegroundColorSpan(Color.RED), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvBackupWarning.setText(s1);

		final TextView tvPath = (TextView) findViewById(R.id.backupLocalLocation);
		mSpinner = (Spinner) findViewById(R.id.identitySpinner);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item);
		adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		mIdentityNames = IdentityController.getIdentityNames(this);

		for (String name : mIdentityNames) {
			adapter.add(name);
		}

		mSpinner.setAdapter(adapter);

		String backupUsername = getIntent().getStringExtra("backupUsername");
		getIntent().removeExtra("backupUsername");

		mSpinner.setSelection(adapter.getPosition(backupUsername == null ? IdentityController.getLoggedInUser() : backupUsername));
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				String identityFile = identityDir + File.separator + IdentityController.caseInsensitivize(adapter.getItem(position))
						+ IdentityController.IDENTITY_EXTENSION;
				tvPath.setText(identityFile);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		Button exportToSdCardButton = (Button) findViewById(R.id.bExportSd);

		exportToSdCardButton.setEnabled(FileUtils.isExternalStorageMounted());

		exportToSdCardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO progress
				final String user = (String) mSpinner.getSelectedItem();
				mDialog = UIUtils.passwordDialog(ExportIdentityActivity.this, getString(R.string.backup_identity, user),
						getString(R.string.enter_password_for, user), new IAsyncCallback<String>() {
							@Override
							public void handleResponse(String result) {
								if (!TextUtils.isEmpty(result)) {
									exportIdentity(user, result);
								}
								else {
									Utils.makeToast(ExportIdentityActivity.this, getString(R.string.no_identity_exported));
								}
							}
						});

			}
		});
	}

	// //////// Local
	private void exportIdentity(String user, String password) {
		IdentityController.exportIdentity(ExportIdentityActivity.this, user, password, new IAsyncCallback<String>() {
			@Override
			public void handleResponse(String response) {
				if (response == null) {
					Utils.makeToast(ExportIdentityActivity.this, getString(R.string.no_identity_exported));
				}
				else {
					Utils.makeLongToast(ExportIdentityActivity.this, response);
				}

			}
		});
	}

	// //////// DRIVE
	// Removed Google component


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_help, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			return true;
		case R.id.menu_help:
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_help_backup, null);

			TextView tv = (TextView) view.findViewById(R.id.helpBackup1);
			UIUtils.setHtml(this, tv, R.string.help_backup_what);

			TextView t1 = (TextView) view.findViewById(R.id.helpBackup2);
			t1.setText(Html.fromHtml(getString(R.string.help_backup_local)));
			t1.setMovementMethod(LinkMovementMethod.getInstance());

			TextView t2 = (TextView) view.findViewById(R.id.helpBackup3);
			UIUtils.setHtml(this, t2, R.string.help_backup_drive1);

			t2 = (TextView) view.findViewById(R.id.helpBackup4);
			t2.setText(R.string.help_backup_drive2);

			mDialog = UIUtils.showHelpDialog(this, R.string.surespot_help, view, false);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
}
