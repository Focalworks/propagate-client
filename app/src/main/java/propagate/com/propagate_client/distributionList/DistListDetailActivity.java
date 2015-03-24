package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.contact.AddContactsActivity;
import propagate.com.propagate_client.contact.Contact;
import propagate.com.propagate_client.contact.ContactAdapter;
import propagate.com.propagate_client.database.DistListModule;

/**
 * Created by kaustubh on 20/3/15.
 */
public class DistListDetailActivity extends Activity implements ContactAdapter.GroupInterface  {

  private TextView txtDistListTitle;
  private ListView membersListView;
  private Button btnDeleteDistList;
  private long distListId;
  private ArrayList<DistListModule> distArrayList,membersArrayList;
  private ArrayList<Contact> contactArrayList;
  private ContactAdapter contactAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dist_list_details);

    Bundle bundle = getIntent().getExtras();

    if(bundle != null){
      distListId = bundle.getLong("distListId");
    }

    contactArrayList = new ArrayList<Contact>();
    distArrayList = DistListModule.getInstance().getDistLists(this,distListId);
    membersArrayList = DistListModule.getInstance().getDistListMembers(this, distListId);

    for (DistListModule members : membersArrayList){
      contactArrayList.add(new Contact(Long.parseLong(members.getContact_id()),members.getMember_name(),members.getPhoto_uri()));
    }

    txtDistListTitle = (TextView) findViewById(R.id.dist_list_details_title);
    txtDistListTitle.setText(distArrayList.get(0).getDist_name());

    membersListView = (ListView) findViewById(R.id.dist_list_details_list_view);

    contactAdapter = new ContactAdapter(this,R.layout.custom_contact_view,contactArrayList,"distListDetails");
    membersListView.setAdapter(contactAdapter);
    membersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showRemovePopup(position);
        return false;
      }
    });

    btnDeleteDistList = (Button) findViewById(R.id.dist_list_details_btn_delete);
    btnDeleteDistList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DistListModule.getInstance().deleteDistList(getApplicationContext(),distListId);
        loadDistListActivity();
      }
    });
  }

  private void showRemovePopup(final int position){
    final DistListModule distListModule = (DistListModule) membersArrayList.get(position);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    // set dialog message
    alertDialogBuilder
        .setMessage("Remove Member "+distListModule.getMember_name())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {

            Contact contact = (Contact) contactAdapter.getItem(position);
            DistListModule.getInstance().deleteMember(getApplicationContext(), distListId, distListModule.getMember_id());
            DistListModule.getInstance().updateDistListCount(getApplicationContext(),distListId,"remove");
            contactAdapter.remove(contact);
            contactAdapter.notifyDataSetChanged();
            membersArrayList.remove(position);
            dialog.dismiss();
          }
        })
        .setNegativeButton("No",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {
            dialog.dismiss();
          }
        });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
  }

  @Override
  public void OnRemoveContactClick(int pos, Contact contact) {
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_add_member,menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_add_member:
        Intent intent = new Intent(this, DistListAddMemberActivity.class);
        intent.putExtra("distListId",distListId);
        startActivity(intent);
        break;

      default:
        break;
    }

    return false;
  }

  private void loadDistListActivity(){
    Intent intent = new Intent(this,DistListingActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    loadDistListActivity();
  }
}
