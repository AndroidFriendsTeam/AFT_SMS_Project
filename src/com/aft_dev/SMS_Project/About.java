package com.aft_dev.SMS_Project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class About {
	
	static String VersionName(Context context){
		
		String _version;
				
		try{
			_version = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
		}catch(NameNotFoundException e){
			_version = context.getString(R.string.str_Version_Unknown);
		}
						
		return _version;
		
	}
	
	public static void Show(Activity callingActivity){
		
		 //Use a Spannable to allow for links highlighting
		SpannableString _aboutText = new SpannableString("Version " + 
										VersionName(callingActivity) + "\n\n"+
										callingActivity.getString(R.string.str_About));
		
		//Generate views to pass to AlertDialog.Builder and to set the text
		View _about;
		TextView _txtAbout;
		
		try{
			//Inflate the custom view
			LayoutInflater _inflater = callingActivity.getLayoutInflater();
			_about = _inflater.inflate(R.layout.activity_about, (ViewGroup) callingActivity.findViewById(R.id.aboutView));
			_txtAbout = (TextView) _about.findViewById(R.id.aboutText);	
		}catch(InflateException e){
			 //Inflater can throw exception, unlikely but default to TextView if it occurs
			_about = _txtAbout = new TextView(callingActivity);
		}
		
		//Set the about text
		_txtAbout.setText(_aboutText);
		//Now Linkify the text
		Linkify.addLinks(_txtAbout, Linkify.ALL);
		//Build and show the dialog
		new AlertDialog.Builder(callingActivity)
		.setTitle("About " + callingActivity.getString(R.string.app_name))
		.setCancelable(true)
		.setIcon(R.drawable.ic_launcher)
		.setPositiveButton("OK", null)
		.setView(_about)
		.show(); 

		
		
	}
	

}
