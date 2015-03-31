package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.login.LoginSessionManager;
import propagate.com.propagate_client.contact.AddContactsActivity;
import propagate.com.propagate_client.contact.Contact;
import propagate.com.propagate_client.contact.ContactAdapter;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

public class CreateDistListActivity extends Activity implements ContactAdapter.GroupInterface,APIHandlerInterface{

  ProgressDialog ringProgressDialog;
  ImageView btnAddMember;
  EditText etGroupName;
  ListView groupContactListView;
  AutoCompleteTextView etAddMember;
  ArrayList<HashMap<String, String>> contactList;
  String groupName;
  ArrayList<Contact> contactArrayList,selectedContactArrayList;
  ContactAdapter contactAdapter,selectedContactAdapter;
  Boolean selectAll = false;
  private long group_id;
  private LoginSessionManager loginSessionManager;
  private String isoCode;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_dist_list);

    isoCode = CommonFunctions.getIsoCode(getApplicationContext());

    loginSessionManager = new LoginSessionManager(this);
    contactArrayList = new ArrayList<Contact>();
    selectedContactArrayList = new ArrayList<Contact>();
    contactList = new ArrayList<HashMap<String, String>>();

    etGroupName = (EditText)findViewById(R.id.createDistListEtGroupName);

    Bundle bundle = getIntent().getExtras();
    if(bundle != null){
      if(bundle.containsKey("contactList")) {
        contactList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("contactList");
        groupName = bundle.getString("groupName");
        selectAll = bundle.getBoolean("allSelected");
        etGroupName.setText(groupName);
      }
    }

    if (contactList.isEmpty()) {
      contactList = CommonFunctions.getContactList(CreateDistListActivity.this);
      for (HashMap<String,String> contact : contactList){
        contactArrayList.add(new Contact(Long.parseLong(contact.get("contact_id")), contact.get("display_name"), contact.get("profile_pic"), Boolean.parseBoolean(contact.get("isSelected"))));
      }
    }else{
      for (HashMap<String, String> list : contactList) {
        if(Boolean.parseBoolean(list.get("isSelected")))
          selectedContactArrayList.add(new Contact(Long.parseLong(list.get("contact_id")), list.get("display_name"), list.get("profile_pic"), Boolean.parseBoolean(list.get("isSelected"))));

        contactArrayList.add(new Contact(Long.parseLong(list.get("contact_id")), list.get("display_name"), list.get("profile_pic"), Boolean.parseBoolean(list.get("isSelected"))));
      }
    }
    Collections.sort(contactArrayList);

    btnAddMember = (ImageView) findViewById(R.id.createDistListBtnAddMembers);
    btnAddMember.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), AddContactsActivity.class);
        intent.putExtra("groupName", etGroupName.getText().toString());
        intent.putExtra("contactList", contactList);
        intent.putExtra("allSelected", selectAll);
        startActivity(intent);
        finish();
      }
    });

    contactAdapter = new ContactAdapter(this, R.layout.custom_contact_view, contactArrayList, "autoCompleteContactList");
    etAddMember = (AutoCompleteTextView) findViewById(R.id.createDistListEtAddMember);

    etAddMember.setAdapter(contactAdapter);
    etAddMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Contact contact = (Contact) adapterView.getItemAtPosition(pos);
        contact.setChecked(true);
        etAddMember.setText("");

        for (HashMap<String, String> c : contactList) {
          if (Long.parseLong(c.get("contact_id")) == contact.getContact_id()) {
            c.put("isSelected", "" + contact.isChecked());

            selectedContactArrayList.add(new Contact(contact.getContact_id(), contact.getName(), contact.getProfile_pic(), contact.isChecked()));
            Collections.sort(selectedContactArrayList);
            selectedContactAdapter.notifyDataSetChanged();
          }
        }

        CommonFunctions.hideKeyboard(CreateDistListActivity.this);
      }
    });

    selectedContactAdapter = new ContactAdapter(this, R.layout.custom_contact_view, selectedContactArrayList, "distMembersList");
    groupContactListView = (ListView) findViewById(R.id.createDistListGroupContactList);
    groupContactListView.setAdapter(selectedContactAdapter);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_done, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.findItem(R.id.menu_done).setTitle("Create");
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.menu_done:
        if (checkValidation()) {
          new postCreateTask().execute();
        }
        break;
    }
    return false;
  }

  private boolean checkValidation(){
    if(TextUtils.isEmpty(etGroupName.getText().toString())){
      etGroupName.setError("Field can not be blank.");
      return false;
    }else if(selectedContactArrayList.size() == 0){
      Toast.makeText(getApplicationContext(),"Select at least one Member",Toast.LENGTH_LONG).show();
      return false;
    }
    return true;
  }

  private void loadGroupListing(){
    Intent intent = new Intent(getApplicationContext(), DistListingActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    loadGroupListing();
  }

  @Override
  public void OnRemoveContactClick(int pos, Contact contact) {
    selectedContactArrayList.remove(pos);
    selectedContactAdapter.notifyDataSetChanged();

    contact.setChecked(false);
    for(HashMap<String,String> c : contactList) {
      if(Long.parseLong(c.get("contact_id")) == contact.getContact_id()) {
        c.put("isSelected", ""+contact.isChecked());
        contactArrayList.add(new Contact(contact.getContact_id(), contact.getName(), contact.getProfile_pic(), contact.isChecked()));
      }else{
        contactArrayList.add(new Contact(Long.parseLong(c.get("contact_id")), c.get("display_name"), c.get("profile_pic"), Boolean.parseBoolean(c.get("isSelected"))));
      }
    }
    Collections.sort(contactArrayList);
    contactAdapter = new ContactAdapter(CreateDistListActivity.this, R.layout.custom_contact_view, contactArrayList, "autoCompleteContactList");
    etAddMember.setAdapter(contactAdapter);
  }

  public void launchProgressDialog() {
    ringProgressDialog = ProgressDialog.show(CreateDistListActivity.this, "Please wait ...",	"Creating Distribution List...", true);
    ringProgressDialog.setCancelable(false);
  }

  public void dismissProgressDialog() {
    ringProgressDialog.dismiss();
  }

  @Override
  public void OnRequestResponse(String response) {
    if(response != null){
      String message = CommonFunctions.trimMessage(response, "message");
      Log.i("message", message);
      if(message != null)
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

      String data = CommonFunctions.trimMessage(response, "data");
      if(data != null)
        try {
          Log.i("data",data);
          JSONObject jsonObj = new JSONObject(data);
          String type = jsonObj.getString("type");
          switch (type){
            case "save":
              JSONObject list = new JSONObject(jsonObj.getString("list"));
              long server_group_id = list.getLong("id");
              DistListModule.getInstance().updateDistListStatus(getApplicationContext(), group_id,server_group_id);
              dismissProgressDialog();
              loadGroupListing();
              break;
            case "delete":

              break;
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
    }
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    if(error instanceof NoConnectionError)
      Toast.makeText(getApplicationContext(),"No Connection Error",Toast.LENGTH_SHORT).show();
    else if(error.networkResponse != null){
      CommonFunctions.errorResponseHandler(getApplicationContext(),error);
    }
    dismissProgressDialog();
    loadGroupListing();
  }

  private class postCreateTask extends AsyncTask<Void,Void,Void>{

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      launchProgressDialog();
    }

    @Override
    protected Void doInBackground(Void... params) {
      group_id = DistListModule.getInstance().addDistList(getApplicationContext(), new DistListModule(etGroupName.getText().toString(), "1", selectedContactArrayList.size()));
      for (Contact contact : selectedContactArrayList) {
        DistListModule.getInstance().addDistListMembers(getApplicationContext(), new DistListModule(group_id, contact.getName(), Long.toString(contact.getContact_id()), contact.getProfile_pic()));
      }

      AppController.getInstance().postCreateDistList(CreateDistListActivity.this,group_id);
      return null;
    }
  }
}
