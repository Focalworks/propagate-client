package propagate.com.propagate_client.distributionList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.contact.Contact;
import propagate.com.propagate_client.contact.ContactAdapter;
import propagate.com.propagate_client.database.DistListModule;

/**
 * Created by kaustubh on 20/3/15.
 */
public class DistributionListDetailActivity extends ActionBarActivity implements ContactAdapter.GroupInterface  {

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

    btnDeleteDistList = (Button) findViewById(R.id.dist_list_details_btn_delete);
    btnDeleteDistList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DistListModule.getInstance().deleteDistList(getApplicationContext(),distListId);
      }
    });
  }

  @Override
  public void OnRemoveContactClick(int pos, Contact contact) {

  }


}
