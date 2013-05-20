package com.exemple.aft_SMS_Project;

import com.example.aft_sms_project.R;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//Création d'une énumération pour les tests de cohérences
//Creating an enumeration for the consistency tests
enum avertissement_Message {MESSAGE_OK,CHAMP_CONTACT_VIDE,CHAMP_MESSAGE_VIDE};

public class MainActivity extends Activity implements OnClickListener{

	protected static final int SELECT_CONTACT = 1;
	//Création des editText
	//Creating editText
	EditText champ_Contact,champ_Message;
	TextView compteurCaract,compteurSMS;

	//Création des objets balises de fusion
	//Creating balise de fusion object
	BaliseDeFusion bdf_Nom;
	BaliseDeFusion bdf_Prenom;

	//Création d'une arraylist pour avoir la liste des messages
	//Creating arrayList to get the messages list
	ArrayList<String> liste_De_Message;

	//Création d'une arraylist pour avoir la liste de mes contacts
	//Creating arrayList to get the contacts list
	ArrayList<Contact> liste_De_Contact;

	//Création d'une arraylist pour avoir créer des SMS plus long
	//Creating arrayList to create longer message
	ArrayList<String> liste_De_SMS;

	//Création d'une variable pour réinitialiser le message
	//Creating a variable to re initialised the message
	String message_Temporaire;

	//Création des différentes string utilisée pour les algo
	//Creating few String using in algo
	String liste_Contact,message;

	//Création des variable pour les compteurs
	//Creating variable for counter
	Integer taille_Sms;
	Integer nb_Sms;

	public MainActivity() {
		super();
		bdf_Nom = new BaliseDeFusion("<Nom>");
		bdf_Prenom = new BaliseDeFusion("<Prénom>");
		liste_Contact = "";
		liste_De_Message = new ArrayList<String>();
		liste_De_Contact = new ArrayList<Contact>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 

		//attribution des différents objets aux ID
		//matching object with ID
		champ_Contact = (EditText) findViewById(R.id.EditText_Contacts);
		champ_Message = (EditText) findViewById(R.id.EditText_Message);
		compteurCaract = (TextView) findViewById(R.id.tevNbCaract);
		compteurSMS = (TextView) findViewById(R.id.tevNbSMS);

		findViewById(R.id.button_Contact).setOnClickListener(this);
		findViewById(R.id.button_BDF_Nom).setOnClickListener(this);
		findViewById(R.id.button_BDF_Prenom).setOnClickListener(this);
		findViewById(R.id.button_Envoyer).setOnClickListener(this);

		//Initialisation des compteurs
		//Initialisation of counter
		taille_Sms = nb_Sms = 0;

		//Création d'un écouteur permettant de gérer les compteurs
		//Creating ligtener to manage counter
		champ_Message.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {

				//calcul du nombre de SMS
				//calculating the number of SMS
				nb_Sms = (int) Math.floor((champ_Message.length() / 160) + 1) ; 

				//calcul du nombre de caractère restant dans le SMS en cours
				//calculating how many caracters left in the current message
				taille_Sms = 160 - (champ_Message.length() - (160 * (nb_Sms-1)));

				//affichage du compteur
				//displaying the counter
				compteurCaract.setText(taille_Sms + "/160");
				compteurSMS.setText(nb_Sms + " SMS");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//on ne fait rien avant que le texte ne soit modifié
				//nothing hapend before the text is changed
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//on ne fait rien pendant que le text est modifié
				//nothing hapend wile the text is changed
			}
		});

	}

	//gestion des clics sur les boutons
	//managing the clic on the button
	public void onClick(View arg0) {
		switch (arg0.getId())
		{
		case R.id.button_Contact : selectContact();		
		break;
		case R.id.button_BDF_Nom : insert_BDF_Nom();
		break;
		case R.id.button_BDF_Prenom: insert_BDF_Prenom();
		break;
		case R.id.button_Envoyer: send_SMS();
		break;
		default :
		}
	}

	//Clic sur le bouton Contact
	//Clic on the contact button
	private void selectContact() {

		//Création et ouverture de l'activité ActivityGetContact pour ouvrir la sélection des contacts
		//Creating and opening the ActivityGetContact activity to open the contact selection
		Intent i = new Intent(MainActivity.this,ActivityGetContact.class);
		startActivityForResult(i,SELECT_CONTACT);

	}

	//Clic sur le bouton Nom
	//Clic on the last name button
	private void insert_BDF_Nom() {

		//Ajouter la balise de fusion dans le message
		//Add the text object in the message
		message = champ_Message.getText() + " <Nom> ";
		champ_Message.setText(message);
		//placer le curseur en fin de texte
		//Set the cursor at the end of the text
		champ_Message.setSelection(champ_Message.getText().length());

	}

	//Clic sur le bouton BDF Prénom
	//Clic on the first name button
	private void insert_BDF_Prenom() {

		//Ajouter la balise de fusion dans le message
		//Add the text object in the message
		message = champ_Message.getText() + " <Prénom> ";
		champ_Message.setText(message);
		//placer le curseur en fin de texte
		//Set the cursor at the end of the text
		champ_Message.setSelection(champ_Message.getText().length());

	}

	//Clic sur le bouton envoyer
	//Clic on the send button
	private void send_SMS() {

		//Test de cohérance avant envoi du SMS
		//Consistency test before sending the SMS

		//j'initialise l'état du message à OK
		//Initializing the message's state at : OK 
		avertissement_Message valid = avertissement_Message.MESSAGE_OK;

		//Test pour voir si il y a au moins un contact
		//Test to check if there is at least one contact 
		if((champ_Contact.getText().toString() + "") == ""){
			valid = avertissement_Message.CHAMP_CONTACT_VIDE;
		}
		//Test pour voir si le message n'est pas vide
		//Test to check if the message is 
		else if(champ_Message.getText().length() == 0){
			valid = avertissement_Message.CHAMP_MESSAGE_VIDE;
		}

		switch(valid){
		case MESSAGE_OK : //cas si on trouve que le message est valide suite aux tests de cohérences : on demande une confirmation d'envoi 
		{

			Integer nb_Contact = 0;

			//Parcours du tableau de contact
			for(Contact c : liste_De_Contact){

				nb_Contact += 1 ;

			}

			AlertDialog.Builder adb_mess_confirm = new AlertDialog.Builder(MainActivity.this);
			adb_mess_confirm.setTitle(getString(R.string.str_Sending_Confirm_1) + " " + nb_Contact + " " + getString(R.string.str_Sending_Confirm_2) );
			adb_mess_confirm.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//Lorsque l'on cliquera sur non on ne fera rien

				} });
			adb_mess_confirm.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//Lorsque l'on cliquera sur oui on envoi les SMS

					//Récupération de ce qui se trouve dans le message
					message = champ_Message.getText().toString();

					//Parcours du tableau de contact
					for(Contact c : liste_De_Contact){

						message_Temporaire = message;

						message_Temporaire = RechercheEtRemplace(message_Temporaire,bdf_Nom,c);

//						Toast.makeText(getApplicationContext(), message_Temporaire, Toast.LENGTH_SHORT).show();

						message_Temporaire = RechercheEtRemplace(message_Temporaire,bdf_Prenom,c);

//						Toast.makeText(getApplicationContext(), message_Temporaire, Toast.LENGTH_SHORT).show();
//
//						Toast.makeText(getApplicationContext(), c.getFamilyName() + "family name", Toast.LENGTH_SHORT).show();
//
//						Toast.makeText(getApplicationContext(), c.getGivenName() + "given name", Toast.LENGTH_SHORT).show();

						//Création du multi-SMS si besoin et envoi du SMS	
						liste_De_Message = SmsManager.getDefault().divideMessage(message_Temporaire);

						SmsManager.getDefault().sendMultipartTextMessage(c.getPhoneNumber(), null, liste_De_Message, null, null);
					}

					Toast toast= Toast.makeText(getApplicationContext(), R.string.str_Sending_Ok, Toast.LENGTH_LONG);  
					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();

				} });
			adb_mess_confirm.create();
			adb_mess_confirm.show();

		}
		break;
		case CHAMP_CONTACT_VIDE : //cas si on ne trouve aucun contacts sélectionnés suite aux tests de cohérences : on n'envoi pas le SMS
		{
			Toast.makeText(getApplicationContext(), R.string.str_No_Contact, Toast.LENGTH_SHORT).show();
		}
		break;
		case  CHAMP_MESSAGE_VIDE : //cas si on ne trouve aucun messages suite aux tests de cohérences : on n'envoi pas le SMS
		{
			Toast.makeText(getApplicationContext(), R.string.str_No_Message, Toast.LENGTH_SHORT).show();
		}
		break;
		default : Toast.makeText(getApplicationContext(), "BUUUUUUUUUUUUUUG !!!!!!!!!!!", Toast.LENGTH_SHORT).show();
		break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//ici on pourra mettre l'option pour envoyer à plusieurs destinataires
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == SELECT_CONTACT){

			if(resultCode == RESULT_OK){

				//Getting the parameters 
				Bundle _param = data.getExtras();

				//Getting the array in the bundle
				liste_De_Contact = _param.getParcelableArrayList("selectedItems");

				//remplissage auto de la liste de contact et liaison au champ concerné
				liste_Contact="";
				for(Contact c : liste_De_Contact){
					liste_Contact = liste_Contact + " " + c.getFamilyName() + " " + c.getGivenName() + " ; ";
				}
				champ_Contact.setText(liste_Contact);

			}

		}

	}

	//Création de la fonction recherche et remplace pour remplacer les balises de fusions dans les SMS
	public String RechercheEtRemplace(String source,BaliseDeFusion BDF, Contact c){

		int index_0, index_1;
		String avant, pendant, apres, resultat;

		int index = 0;
		boolean trouve = true;

		index_0 = source.indexOf(BDF.getNom(), 0);
		index_1 = source.indexOf(BDF.getNom(), 0) + BDF.getNom().length();

		//boucle de recherche pour rechercher et remplacer toutes les occurences de la balise BDF
		while(trouve && index < source.length()){

			//test pour savoir je trouve ma balise de fusion BDF
			if(source.indexOf(BDF.getNom(), 0) != -1){

				//action si je trouve la balise : créer un message dans le tableau de message où je remplace la balise par le nom de chacun de mes contacts

				//index de là où je trouve ma balise
				index_0 = source.indexOf(BDF.getNom(), 0);

				//index de là où se fini ma balise
				index_1 = source.indexOf(BDF.getNom(), 0) + BDF.getNom().length();

				//récupération de la première partie du message 
				avant = source.substring(0, index_0);

				//remplacement du mot recherché
				//Test pour savoir si notre balise de fusion est un nom ou un prénom
				if(BDF.getNom() == "<Nom>"){
					pendant = c.getFamilyName();
				}
				else{
					pendant = c.getGivenName();
				}

				//récupération de la fin de la chaîne
				apres = source.substring(index_1 , source.length());

				//création de la chaîne final
				resultat = avant + "" + pendant + "" + apres;

				//modification du message incluant les modifications
				source = resultat;

			}else{
				//action si je ne trouve pas la balise <Nom> : je sors de la bouble
				trouve = false;
			}
			index++;
		}
		return source;
	}

	@Override
	public void onBackPressed() {
		// TODO vider tous les champs à la fermeture du programme

		champ_Contact.setText("");
		champ_Message.setText("");

		super.onBackPressed();
	}

}