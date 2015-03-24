package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.contact.Contact;
import propagate.com.propagate_client.contact.ContactAdapter;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.utils.CommonFunctions;

/**
 * Created by kaustubh on 24/3/15.
 */
public class DistListAddMemberActivity extends Activity implements ContactAdapter.GroupInterface {

  ListView listContact;
  ContactAdapter contactAdapter;
  ArrayList<Contact> contactArrayList;
  ArrayList<HashMap<String, String>> contactList;
  long distId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      distId = bundle.getLong("distListId");
    }

    contactArrayList = new ArrayList<Contact>();
    contactList = new ArrayList<HashMap<String, String>>();

    listContact = (ListView) findViewById(R.id.listingListView);

    contactList = CommonFunctions.getContactList(getApplicationContext());

    for (HashMap<String, String> list : contactList) {
      contactArrayList.add(new Contact(Long.parseLong(list.get("contact_id")), list.get("display_name"), list.get("profile_pic"), Boolean.parseBoolean(list.get("isSelected"))));
    }

    Collections.sort(contactArrayList);
    contactAdapter = new ContactAdapter(this,R.layout.custom_contact_view,contactArrayList,"searchContactList");
    listContact.setAdapter(contactAdapter);
    listContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = contactAdapter.getItem(position);
        DistListModule.getInstance().addDistListMembers(getApplicationContext(),new DistListModule(distId,contact.getName(),Long.toString(contact.getContact_id()),contact.getProfile_pic()));
        DistListModule.getInstance().updateDistListCount(getApplicationContext(),distId,"add");
        loadDistListDetailActivity();
      }
    });
  }

  private void loadDistListDetailActivity(){
    Intent intent = new Intent(this,DistListDetailActivity.class);
    intent.putExtra("distListId",distId);
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.menu_search,menu);
// Associate searchable configuration with the SearchView
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextChange(String searchText) {
        int length = searchText.length();
        ArrayList<Contact> tempArrayList = new ArrayList<Contact>();
        for (Contact c : contactArrayList) {
          if (length <= c.getName().length()) {
            if (c.getName().toLowerCase().contains(searchText.toLowerCase())) {
              tempArrayList.add(c);
            }
          }
        }
        contactAdapter = new ContactAdapter(DistListAddMemberActivity.this, R.layout.custom_contact_view, tempArrayList, "searchContactList");
        listContact.setAdapter(contactAdapter);
        return true;
      }

      @Override
      public boolean onQueryTextSubmit(String query) {

        return true;
      }
    };

    searchView.setOnQueryTextListener(queryTextListener);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void OnRemoveContactClick(int pos, Contact contact) {

  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    loadDistListDetailActivity();
  }
}
