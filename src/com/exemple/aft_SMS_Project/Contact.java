package com.exemple.aft_SMS_Project;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

public class Contact implements Parcelable, Comparable<Contact>{

	private String familyName, givenName, displayName, phoneNumber, id_Contact, id_PhoneNumber;
	private boolean checked;

	public Contact(String displayName, String phoneNumber, String id_Contact) {
		super();
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
		this.id_Contact = id_Contact;
	}


	public Contact(String familyName, String givenName, String displayName,
			String phoneNumber, String id_Contact, String id_PhoneNumber, boolean checked) {
		super();
		this.familyName = familyName;
		this.givenName = givenName;
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
		this.id_Contact = id_Contact;
		this.id_PhoneNumber = id_PhoneNumber;
		this.checked = checked;
	}

	public Contact(String familyName, String givenName, String displayName,
			String phoneNumber, String id_Contact, String id_PhoneNumber) {
		super();
		this.familyName = familyName;
		this.givenName = givenName;
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
		this.id_Contact = id_Contact;
		this.id_PhoneNumber = id_PhoneNumber;
	}

	public Contact(String familyName, String givenName, String displayName,
			String id_Contact , String phoneNumber) {
		super();
		this.familyName = familyName;
		this.givenName = givenName;
		this.displayName = displayName;
		this.id_Contact = id_Contact;
		this.phoneNumber = phoneNumber;
	}

	public Contact(String familyName, String givenName, String displayName,
			String id_Contact) {
		super();
		this.familyName = familyName;
		this.givenName = givenName;
		this.displayName = displayName;
		this.id_Contact = id_Contact;
	}

	//	public Contact(String familyName, String givenName, String phoneNumber) {
	//		super();
	//		this.familyName = familyName;
	//		this.givenName = givenName;
	//		this.phoneNumber = phoneNumber;
	//	}



	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getId_Contact() {
		return id_Contact;
	}

	public void setId_Contact(String id_Contact) {
		this.id_Contact = id_Contact;
	}

	public String getId_PhoneNumber() {
		return id_PhoneNumber;
	}

	public void setId_PhoneNumber(String id_PhoneNumber) {
		this.id_PhoneNumber = id_PhoneNumber;
	}

	public boolean getIs_Checked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String toString() {
		return this.displayName;
	}

	public ArrayList<phoneNumber> getPhoneNumbers(Context context, String id)
	{
		ArrayList<phoneNumber> phoneNumber_List = new ArrayList<phoneNumber>();

		Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
		while (pCur.moveToNext())
		{
			phoneNumber p = new phoneNumber(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)),pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			phoneNumber_List.add(p);
		}
		pCur.close();
		return(phoneNumber_List);
	}

	public void InitInfoContact(Context context){

		String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
		String[] whereNameParams = new String[] {ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE , this.id_Contact};

		int _idx_given, _idx_family;
		String _given = null, _family = null;

		Cursor nameCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, null);

		_idx_given 		= nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
		_idx_family 	= nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);

		if (nameCur.moveToNext()) {

			_given 	= nameCur.getString(_idx_given);
			_family = nameCur.getString(_idx_family);

		}

		nameCur.close();

		if(_given != null){
			this.setGivenName(_given);
		}
		else{
			this.setGivenName(this.displayName);
		}

		if(_family != null){
			this.setFamilyName(_family);
		}
		else{
			this.setFamilyName("");
		}

	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {

		dest.writeString(familyName);
		dest.writeString(givenName);
		dest.writeString(displayName);
		dest.writeString(phoneNumber);
		dest.writeString(id_Contact);
		dest.writeString(id_PhoneNumber);

	}

	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>(){
		@Override
		public Contact createFromParcel(Parcel source)
		{
			return new Contact(source);
		}

		@Override
		public Contact[] newArray(int size)
		{
			return new Contact[size];
		}
	};

	public Contact(Parcel in) {
		this.familyName = in.readString();
		this.givenName = in.readString();
		this.displayName = in.readString();
		this.phoneNumber = in.readString();
		this.id_Contact = in.readString();
		this.id_PhoneNumber = in.readString();
	}

	@Override
	public int compareTo(Contact arg0) {

		int id_a = Integer.parseInt(arg0.id_Contact);
		int id_b = Integer.parseInt(this.getId_Contact());

		if(id_a > id_b) return -1;
		else if(id_a <id_b) return  1;
		else if(id_a == id_b) return 0;

		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		
		boolean	isEqual = false;
		Contact c = (Contact)o;
	
		//Testing if contacts have the same ID 
		if(c.id_Contact.equals(id_Contact)) isEqual = true;
		
		//Log.v("Contact",c.id_Contact+"=="+id_Contact+"/"+isEqual);
		
		return isEqual;
		
		
	}

}


