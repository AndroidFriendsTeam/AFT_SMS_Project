package com.aft_dev.SMS_Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.aft_dev.SMS_Project.R;

import android.R.color;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ActivityGetContact extends Activity implements OnClickListener   {

	Button _btn_ok;
	Button _btn_cancel;
	ImageButton _btn_reset;
	EditText rech;
	ListView _lsv_contact;
	MyCustomAdapter _adapter = null;
	ArrayList<Contact> _contacts;
	Integer _id,cpt;

	//Variable pour récupérer le numéro de GSM en cours
	String phoneNumberEnCours,idPhoneNumberEnCours;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Link with the layout
		setContentView(R.layout.activity_get_contact);

		//Initialization of the members of the class
		findViewById();

		//Test pour savoir quelle est la version d'android
		//Check android version for switch text color
		if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){	
			rech.setBackgroundColor(color.background_dark);
		}

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
		this._btn_reset.setOnClickListener(this);

		//activate the filter on the ListView
		_lsv_contact.setTextFilterEnabled(true);

		//Masquer le clavier virtuel jusqu'on appui sur le editText
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//Add TextWatcher on the EditText
		rech.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {	
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {    
				_adapter.getFilter().filter(s.toString());    	
			}
		});

	}

	private void findViewById(){
		this._btn_ok = (Button)findViewById(R.id.btn_ok);
		this._btn_cancel = (Button)findViewById(R.id.btn_cancel);
		this._lsv_contact = (ListView)findViewById(R.id.lsv_contact);
		this._btn_reset = (ImageButton)findViewById(R.id.btn_reset);
		this.rech = (EditText) findViewById(R.id.editText_rech);
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

	//gestion des clics sur les boutons
	//managing the clic on the button
	public void onClick(View arg0) {
		switch (arg0.getId())
		{
		case R.id.btn_cancel : contact_cancel();		
		break;
		case R.id.btn_ok : contact_ok();
		break;
		case R.id.btn_reset : contact_reset();
		break;
		default :
		}
	}

	private void contact_cancel() {
		//Initialize a new Intent
		Intent _intent_result = new Intent();
		//Set a cancel result
		setResult(RESULT_CANCELED,_intent_result);
		//Close the activity
		finish();

	}

	private void contact_ok() {
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
	}

	private void contact_reset() {
		rech.setText("");
	}

	public void PhoneNumberSelection(Contact in_contact){

		//Récupération du contact en cours
		final Contact c = in_contact;

		//final int _pos = in_pos;

		//Création d'une ArrayList contenant tous les numéros de téléphone du contact
		final ArrayList<phoneNumber> phoneNumber_List = c.getPhoneNumbers(getApplicationContext(), c.getId_Contact());

		//Test du nombre de numéro de téléphone
		//Si il y en a plus qu'un alors j'ouvre une AlertDialog contenant tous les numéros
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
					//Variable pour récupérer le numéro de GSM en cours
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

			//Variable pour récupérer le numéro de GSM en cours
			phoneNumberEnCours = phoneNumber_List.get(0).getPhoneNumber();
			idPhoneNumberEnCours = phoneNumber_List.get(0).getIdPhoneNumber();

			c.setPhoneNumber(phoneNumberEnCours);
			c.setId_PhoneNumber(idPhoneNumberEnCours);

		}

	}//End OnItemClick

	//Begin of the customAdapter
	public class MyCustomAdapter extends ArrayAdapter<Contact> implements Filterable {

		private ArrayList<Contact> _contacts;
		private ArrayList<Contact> _contactsFiltered;

		public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Contact> in_contacts) {
			super(context, textViewResourceId, in_contacts);

			_contacts 			= in_contacts;
			_contactsFiltered 	= in_contacts;

		}

		private class ViewHolder{
			TextView displayName;
			TextView phoneNumber;
			CheckBox checkSelection;
		}

		//For this helper method, return based on filteredData
		public int getCount() 
		{
			return _contactsFiltered.size();
		}

		//This should return a data Contact, not an integer
		public Contact getItem(int position) 
		{
			return _contactsFiltered.get(position);
		}

		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder _holder = null;

			if(convertView == null){
				LayoutInflater _vi 	= (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView 		= _vi.inflate(R.layout.activity_contact_list, null);

				_holder = new ViewHolder();
				_holder.displayName 	= (TextView) convertView.findViewById(R.id.txt_name);
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
						
						setNbContactChecked();


					}

				});

				convertView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {		

						View 		_view		= (View)v;
						CheckBox 	_chk 		= (CheckBox)_view.findViewById(R.id.ckb_selection);
						Contact 	_contact 	= (Contact)_chk.getTag();

						_chk.setChecked(!_chk.isChecked());
						_contact.setChecked((_chk.isChecked()));

						if(_contact.getIs_Checked())
							PhoneNumberSelection(_contact);	
						
						setNbContactChecked();

					}
				});

			}
			else {
				_holder = (ViewHolder) convertView.getTag();
			}

			Contact _contact = _contactsFiltered.get(position);
			_holder.displayName.setText(_contact.getDisplayName());
			_holder.phoneNumber.setText(_contact.getPhoneNumber());
			_holder.checkSelection.setChecked(_contact.getIs_Checked());
			_holder.checkSelection.setTag(_contact);

			return convertView;
		}

		@Override
		public Filter getFilter() {

			return new Filter()
			{

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {

					FilterResults results = new FilterResults();

					// We implement here the filter logic
					if (constraint == null || constraint.length() == 0) {
						// No filter implemented we return all the list
						results.values = _contacts;
						results.count =  _contacts.size();
					}
					else {
						// We perform filtering operation
						ArrayList<Contact> nContacts = new ArrayList<Contact>();

						for (Contact c : _contacts) {
							if (c.getDisplayName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
								nContacts.add(c);
						}

						results.values 	= nContacts;
						results.count 	= nContacts.size();

					}

					return results;

				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,FilterResults results) {

					//Set the list of filtered contact on the display list
					_contactsFiltered = (ArrayList<Contact>) results.values;
					//We notify that the data have been changed
					notifyDataSetChanged();

				}	

			};

		}

		public void setNbContactChecked(){

			int _nbContactChecked = 0;
			
			// Count the number of checked contact
			for(Contact c:_contacts){

				if(c.getIs_Checked())
					_nbContactChecked++;

			}
			
			// Set the number of checked contact on the OK button label
			if(_nbContactChecked > 0)
				_btn_ok.setText("OK (" + _nbContactChecked + ")");
			else
				_btn_ok.setText("OK");



		}

	}//End CustomAdapter

}//End Class
