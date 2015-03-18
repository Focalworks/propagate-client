package propagate.com.propagate_client.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.distributionList.CreateDistListActivity;

/**
 * Created by kaustubh on 20/1/15.
 */
public class AddContactsActivity extends ActionBarActivity implements ContactAdapter.GroupInterface {

  ListView listContact;
  EditText etSearch;
  ContactAdapter contactAdapter;
  ArrayList<Contact> contactArrayList;
  ArrayList<HashMap<String, String>> contactList,tempContactList;
  String groupName;
  CheckBox checkAll;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_contact);

    checkAll = (CheckBox) findViewById(R.id.checkAll);
    //Visible in dev mode
    checkAll.setVisibility(View.VISIBLE);

    listContact = (ListView) findViewById(R.id.addContactList);
    contactArrayList = new ArrayList<Contact>();
    if(savedInstanceState == null  || !savedInstanceState.containsKey("contactArrayList")) {

      Bundle bundle = getIntent().getExtras();

      if(bundle != null) {
        contactList = (ArrayList<HashMap<String,String>>) bundle.getSerializable("contactList");
        tempContactList = contactList;
        groupName = bundle.getString("groupName");
        checkAll.setChecked(bundle.getBoolean("allSelected"));
      }
      for (HashMap<String, String> list : contactList) {
        contactArrayList.add(new Contact(Long.parseLong(list.get("contact_id")), list.get("display_name"), list.get("phone_number"), list.get("profile_pic"), Boolean.parseBoolean(list.get("isSelected"))));
      }

    /*Collections.sort(contactArrayList, new Comparator<Contact>(){
      public int compare(Contact obj1, Contact obj2)
      {
        return obj1.getName().compareToIgnoreCase(obj2.getName());
      }
    });*/

      Collections.sort(contactArrayList);
    }else{
      contactArrayList = (ArrayList<Contact>)savedInstanceState.getSerializable("contactArrayList");
      groupName = savedInstanceState.getString("groupName");
    }

    contactAdapter = new ContactAdapter(this,R.layout.custom_contact_view,contactArrayList,"addContactList");
    listContact.setAdapter(contactAdapter);
    listContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = contactAdapter.getItem( position );
        contact.toggleChecked();
        ContactViewHolder viewHolder = (ContactViewHolder) view.getTag();
        viewHolder.getChkSelect().setChecked(contact.isChecked());

        for(HashMap<String,String> c : contactList){
          if(Long.parseLong(c.get("contact_id")) == contact.getContact_id())
            c.put("isSelected",""+contact.isChecked());
        }
      }
    });

    etSearch = (EditText) findViewById(R.id.addContactEtSearch);
    etSearch.addTextChangedListener(new TextWatcher() {

      @Override
      public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1,
                                    int arg2, int arg3) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        /*contactAdapter.getFilter().filter(s.toString());*/
        int textlength = s.length();
        ArrayList<Contact> tempArrayList = new ArrayList<Contact>();
        for (Contact c : contactArrayList) {
          if (textlength <= c.getName().length()) {
            if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
              tempArrayList.add(c);
            }
          }
        }
        contactAdapter = new ContactAdapter(AddContactsActivity.this, R.layout.custom_contact_view, tempArrayList, "addContactList");
        listContact.setAdapter(contactAdapter);
      }
    });

    checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        for (int i = 0;i<contactList.size(); i++) {
          Contact contact = contactAdapter.getItem(i);
          if(!contact.isChecked()) {
            contactAdapter.getItem(i).setChecked(true);
            contactList.get(i).put("isSelected", "" + contact.isChecked());
          }
          if(!isChecked){
            contactAdapter.getItem(i).setChecked(false);
            contactList.get(i).put("isSelected", "" + contact.isChecked());
          }
        }
        contactAdapter.notifyDataSetChanged();
      }
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putSerializable("contactArrayList", contactArrayList);
    outState.putString("groupName", groupName);
    super.onSaveInstanceState(outState);
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_btn_create_done, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.menu_done:
        loadCreateGroupActivity(contactList);
        return true;
    }

    return false;
  }

  private void loadCreateGroupActivity(ArrayList<HashMap<String,String>> contactList){
    Intent intent = new Intent(this,CreateDistListActivity.class);
    intent.putExtra("groupName",groupName);
    intent.putExtra("contactList",contactList);
    intent.putExtra("allSelected",checkAll.isChecked());
    startActivity(intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    loadCreateGroupActivity(tempContactList);
    super.onBackPressed();
  }

  @Override
  public void OnRemoveContactClick(int pos, Contact contact) {
  }
}
