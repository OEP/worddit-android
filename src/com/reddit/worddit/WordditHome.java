package com.reddit.worddit;

import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;

public class WordditHome extends Activity implements APICallback {
	public static final String TAG = "WordditHome";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setup();
	}
	
	public Dialog onCreateDialog(int id) {
		switch(id) {
		default:
			Resources r = getResources();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String msg = "(null)";
			try { msg = r.getString(id); }
			catch(Exception e) { msg = String.format(r.getString(R.string.msg_not_found), id); }
			
			return builder.setMessage(msg)
				.setTitle(R.string.app_name)
				.setCancelable(true)
				.setNeutralButton(R.string.label_ok, null)
				.create();
		}
	}
	
	private void setup() {
		// Force confirm password field to reflect default state of checkbox
		doNewChecked(null);
		
		// Setup listeners
		setupListeners();
	}
	
	private void setupListeners() {
		findViewById(R.id.login_button_login)
			.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doButtonClick(v);
			}
		});
		
		findViewById(R.id.login_check_new)
			.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doNewChecked(v);
			}
		});
	}
	
	public void doNewChecked(View v) {
		CheckBox check = (CheckBox) findViewById(R.id.login_check_new);
		EditText confirm = (EditText) findViewById(R.id.login_input_confirmpassword);
		
		if(check.isChecked()) {
			confirm.setVisibility(View.VISIBLE);
		} else {
			confirm.setVisibility(View.GONE);
		}
	}
	
	public void doButtonClick(View v) {
		CheckBox check = (CheckBox) findViewById(R.id.login_check_new);
		if(check.isChecked()) {
			doCreate(v);
		}
		else {
			doLogin(v);
		}
	}
	
	public void doLogin(View v) {
		doLogin(getEmailField(), getPasswordField());
	}
	
	public void doCreate(View v) {
		String email = getEmailField(), password = getPasswordField(), confirm = getConfirmField();
		doCreate(email,password,confirm);
	}
	
	protected void doLogin(String email, String password) {
		boolean fail = false;
		int msg = 0;
		
		if(email.length() == 0 || password.length() == 0) {
			msg = R.string.msg_required_fields_missing;
			fail = true;
		}
		
		if(fail) {
			showDialog(msg);
		}
		else {
			try {
				APICall task = new APICall(this, Session.makeSession());
				task.login(email, password);
			} catch (MalformedURLException e) {
				//TODO: Find out what to do here 
			}
			
		}
	}
	

	protected void doCreate(String email, String password, String confirm) {
		boolean fail = false;
		int msg = 0;
		
		if(email.length() == 0 || password.length() == 0 || confirm.length() == 0) {
			msg = R.string.msg_required_fields_missing;
			fail = true;
		}
		else if(password.equals(confirm) == false) {
			msg = R.string.msg_confirm_password;
			fail = true;
		}
		
		if(fail) {
			showDialog(msg);
		}
		else {
			// TODO: Do create account stuff.
			try {
				new APICall(this, Session.makeSession()).createAccount(email, password);
			} catch (MalformedURLException e) {
				
			}
		}
	}
	
	protected String getEmailField() {
		EditText emailField = (EditText) this.findViewById(R.id.login_input_email);
		return emailField.getText().toString();
	}
	
	protected String getPasswordField() {
		EditText passwordField = (EditText) this.findViewById(R.id.login_input_password);
		return passwordField.getText().toString();
	}
	
	protected String getConfirmField() {
		EditText confirmField = (EditText) this.findViewById(R.id.login_input_confirmpassword);
		return confirmField.getText().toString();
	}

	@Override
	public void onCallComplete(boolean success, int resId, Session sess) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Session returned: " + success + " with code: " + sess.getLastResponse(), 1).show();
		Intent in = new Intent(this, GameList.class);
		
		startActivity(in);
	}
}