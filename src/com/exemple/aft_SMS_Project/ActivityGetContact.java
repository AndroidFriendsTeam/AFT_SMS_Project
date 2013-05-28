package com.exemple.aft_SMS_Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.aft_sms_project.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityGetContact extends Activity implements OnClickListener   {
	
	Button _btn_ok;
	Button _btn_cancel;
	ListView _lsv_contact;
	MyCustomAdapter _adapter = null;
	ArrayList<Contact> _contacts;
	Integer _id,cpt;

	//Variable pour r�cup�rer le num�ro de GSM en cours
	String phoneNumberEnCours,idPhoneNumberEnCours;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Link with the layout
		setContentView(R.layout.activity_get_contact);

		//Initialization of the members of the class
		findViewById();

		//Initialization of array of contact
		_contacts = getListContactPhone();

		//Get the ArrayList passed by parameter to the activity 
		Bundle _param =  this.getIntent().getExtras();	
		ArrayList<Contact> _selectedContact = _param.getParcelableArrayList("selectedItems");

		//Check contact 
		checkContactAlreadySelected(_selectedContact);

		//Initialization of the adapter
		this._adapter = new MyCustomAdapter(this,R.layout.activity_contact_list,_contacts);

		//Initialize the ListView with the adapter
		this._lsv_contact.setAdapter(this._adapter);

		//Initialize a listener on the button for get the clic
		this._btn_ok.setOnClickListener(this);
		this._btn_cancel.setOnClickListener(this);

		//activate the filter on the ListView
		_lsv_contact.setTextFilterEnabled(true);

		EditText rech = (EditText) findViewById(R.id.editText_rech);

		//Add TextWatcher on the EditText
		rech.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {		

				//				_adapter.getFilter().filter(s.toString());
				//				Integer j = 0 ;
				//				j = _adapter.getCount();
				//				Toast.makeText(getApplicationContext(), j.toString(), Toast.LENGTH_SHORT).show();

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {    
				//Set the filter
				//				_adapter.getFilter().filter(s.toString());


				//				Integer j = 0;
				//				Toast.makeText(getApplicationContext(), j ,Toast.LENGTH_SHORT).show() ;

				//				for(int i = 0 ; i <= _adapter.getCount() ; i++){				
				//
				//					//test permettant de tester l'�tat du contact coch� ou d�coch�
				//					if(_adapter.getItem(i).getIs_Checked()){
				//						_lsv_contact.setItemChecked(i , true);
				//					}
				//					else{
				//						_lsv_contact.setItemChecked(i , false);
				//					}
				//				}
				//				Toast.makeText(getApplicationContext(), _adapter.getItem(j).getDisplayName() ,Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void findViewById(){
		this._btn_ok = (Button)findViewById(R.id.btn_ok);
		this._btn_cancel = (Button)findViewById(R.id.btn_cancel);
		this._lsv_contact = (ListView)findViewById(R.id.lsv_contact);
	}

	public ArrayList<Contact> getListContactPhone()
	{	

		String _display, _phone, _id;
		int _idx_display, _idx_phone, _idx_id;
		ArrayList<Contact> _contacts = new ArrayList<Contact>();

		//Initialization of the query 
		Cursor _phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, "DISPLAY_NAME");

		//Save the position of the column index
		_idx_display 	= _phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		_idx_phone 		= _phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		_idx_id			= _phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

		//Fetch the result
		while (_phones.moveToNext())
		{
			//Get the field of the query
			_display = _phones.getString(_idx_display);
			_phone  = _phones.getString(_idx_phone);
			_id  = _phones.getString(_idx_id);

			//Create a new contact
			Contact _contact = new Contact(_display,_phone,_id);

			//you test if the display name and the phone are not empty
			if(_display != null && _phone != "" && !_contacts.contains(_contact)){

				_contacts.add(_contact);

			};

		}

		_phones.close();

		return _contacts;

	}

	public void checkContactAlreadySelected(ArrayList<Contact> in_SelectedItem)
	{
		int _index;

		//Browse the contact into the array
		for(Contact c : in_SelectedItem){

			//Search the contact into the list 
			_index = _contacts.indexOf(c);

			//If you find it, you checked it ! 
			if(_index != -1){
				c.setChecked(true);
				_contacts.get(_index).Copy(c);
			}

		}
	}

	public Contact[] getListContact()
	{
		ArrayList<Contact> _contacts = new ArrayList<Contact>();	

		String whereName = ContactsContract.Data.MIMETYPE + " = ?";
		String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };

		String given, family, display, contact_id;
		int _idx_given, _idx_family, _idx_display, _idx_contact_id;


		Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

		_idx_given 		= nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
		_idx_family 	= nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
		_idx_display 	= nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
		_idx_contact_id = nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID);


		while (nameCur.moveToNext()) {

			given 		= nameCur.getString(_idx_given);
			family 		= nameCur.getString(_idx_family);
			display 	= nameCur.getString(_idx_display);
			contact_id 	= nameCur.getString(_idx_contact_id);


			if(display != null){
				Contact _contact = new Contact(family,given,display,contact_id);

				ArrayList<phoneNumber> _phones = _contact.getPhoneNumbers(getApplicationContext(), contact_id);

				if(_phones.size() > 0)
					_contacts.add(_contact);

			};

		}
		nameCur.close();

		return _contacts.toArray(new Contact[_contacts.size()]);
	}

	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.btn_cancel){
			
			//Initialize a new Intent
			Intent _intent_result = new Intent();
			//Set a cancel result
			setResult(RESULT_CANCELED,_intent_result);
			//Close the activity
			finish();
			
		}
		else{


			//Fill a array of String with the selected items
			Contact _contact_tmp;
			ArrayList<Contact> _contacts		= _adapter._contacts;
			ArrayList<Contact> _selected_items 	= new ArrayList<Contact>();

			//Loop checked contact for fill the array of items
			for(int i = 0 ; i < _contacts.size() ; i++){

				//if contact is checked add in the array of items
				if(_contacts.get(i).getIs_Checked())
				{
					_contact_tmp = _contacts.get(i);
					_contact_tmp.InitInfoContact(this);				
					_selected_items.add(_contact_tmp);	
				}
			}



			//Initialise a new Intent
			Intent _intent_result = new Intent();

			//Create a bundle object to put in parameter of the intent
			Bundle _param = new Bundle();
			_param.putParcelableArrayList("selectedItems", _selected_items);

			//Add the bundle into the intent
			_intent_result.putExtras(_param);

			//		Start the result activity
			//		startActivity(_intent_result);
			setResult(RESULT_OK,_intent_result);

			finish();

		} //else if(v.getId()
	}

	public void PhoneNumberSelection(Contact in_contact){

		//R�cup�ration du contact en cours
		final Contact c = in_contact;

		//final int _pos = in_pos;

		//Cr�ation d'une ArrayList contenant tous les num�ros de t�l�phone du contact
		final ArrayList<phoneNumber> phoneNumber_List = c.getPhoneNumbers(getApplicationContext(), c.getId_Contact());

		//Test du nombre de num�ro de t�l�phone
		//Si il y en a plus qu'un alors j'ouvre une AlertDialog contenant tous les num�ros
		if(phoneNumber_List.size() > 1 ){

			List<HashMap<String, String>> list_Numbers = new ArrayList<HashMap<String, String>>();  
			HashMap<String, String> element;
			for(phoneNumber p : phoneNumber_List){
				element = new HashMap<String, String>();
				element.put("phone", p.getPhoneNumber());
				element.put("id", p.getIdPhoneNumber());
				list_Numbers.add(element);
			}

			final ListAdapter adapter = new SimpleAdapter(ActivityGetContact.this,
					list_Numbers,android.R.layout.simple_list_item_1,
					new String[]{"phone","id"},
					new int[] {android.R.id.text1,android.R.id.text2}
					);

			AlertDialog.Builder builder = new AlertDialog.Builder(ActivityGetContact.this);
			builder	.setTitle(R.string.str_Num_Contacts)
			.setAdapter(adapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// The 'which' argument contains the index position of the selected item 	
					//Variable pour r�cup�rer le num�ro de GSM en cours
					phoneNumberEnCours = phoneNumber_List.get(which).getPhoneNumber();
					idPhoneNumberEnCours = phoneNumber_List.get(which).getIdPhoneNumber();

					//					_contacts.get(_pos).setPhoneNumber(phoneNumberEnCours);
					//					_contacts.get(_pos).setId_PhoneNumber(idPhoneNumberEnCours);

					c.setPhoneNumber(phoneNumberEnCours);
					c.setId_PhoneNumber(idPhoneNumberEnCours);

				}
			})
			.create()
			.show();
		}
		//Si je n'en trouve qu'un alors je ne fais rien ;)
		else{

			//Variable pour r�cup�rer le num�ro de GSM en cours
			phoneNumberEnCours = phoneNumber_List.get(0).getPhoneNumber();
			idPhoneNumberEnCours = phoneNumber_List.get(0).getIdPhoneNumber();

			//			_contacts.get(_pos).setPhoneNumber(phoneNumberEnCours);
			//			_contacts.get(_pos).setId_PhoneNumber(idPhoneNumberEnCours);

			c.setPhoneNumber(phoneNumberEnCours);
			c.setId_PhoneNumber(idPhoneNumberEnCours);

		}

	}//End OnItemClick


	//Begin of the customAdapter
	public class MyCustomAdapter extends ArrayAdapter<Contact> {

		private ArrayList<Contact> _contacts;

		public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Contact> in_contacts) {
			super(context, textViewResourceId, in_contacts);

			//Allocated a new ArrayList<Contact>
			this._contacts = new ArrayList<Contact>();
			//Copy all contacts from the param in_contacts to the member _contacts
			this._contacts.addAll(in_contacts);		
		}

		private class ViewHolder{
			TextView displayName;
			TextView phoneNumber;
			CheckBox checkSelection;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder _holder = null;

			if(convertView == null){
				LayoutInflater _vi 	= (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView 		= _vi.inflate(R.layout.activity_contact_list, null);

				_holder = new ViewHolder();
				_holder.displayName 	= (TextView) convertView.findViewById(R.id.txt_name);
				//					_holder.displayName.onTouchEvent(null);
				_holder.phoneNumber 	= (TextView) convertView.findViewById(R.id.txt_phone);
				_holder.checkSelection 	= (CheckBox) convertView.findViewById(R.id.ckb_selection);
				convertView.setTag(_holder);

				_holder.checkSelection.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						CheckBox 	_chk 		= (CheckBox)v;
						Contact 	_contact 	= (Contact)_chk.getTag();

						_contact.setChecked((_chk.isChecked()));

						if(_contact.getIs_Checked())
							PhoneNumberSelection(_contact);			

					}

				});


			}
			else {
				_holder = (ViewHolder) convertView.getTag();
			}

			Contact _contact = _contacts.get(position);
			_holder.displayName.setText(_contact.getDisplayName());
			_holder.phoneNumber.setText(_contact.getPhoneNumber());
			_holder.checkSelection.setChecked(_contact.getIs_Checked());
			_holder.checkSelection.setTag(_contact);

			return convertView;
		}

		//TODO for davy overide the GetFilter m�thode

		//


	}//End CustomAdapter


}//End Class
