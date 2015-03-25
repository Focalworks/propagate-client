package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.Requirement.RequirementListingActivity;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.property.PropertyListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.volleyRequest.AppController;
import propagate.com.propagate_client.volleyRequest.VolleyStringRequest;

/**
 * Created by kaustubh on 4/2/15.
 */
public class DistListingActivity extends Activity {

  ListView groupListView;
  ImageView imgAddGroup;
  DistListAdapter distListAdapter;
  final String[] groupItems = {
      "Group Info", "Delete Group"
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

//    OauthToken();
//    getToken();
    imgAddGroup = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddGroup.setVisibility(View.VISIBLE);
    imgAddGroup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),CreateDistListActivity.class);
        startActivity(intent);
        finish();
      }
    });

    /*
    * pass id:0 to get all records and pass individual id to get individual record
    * */
    ArrayList<DistListModule> distArrayList = DistListModule.getInstance().getDistLists(this, 0);

    distListAdapter = new DistListAdapter(this, R.layout.custom_dist_view,distArrayList);

    groupListView = (ListView) findViewById(R.id.listingListView);
    groupListView.setAdapter(distListAdapter);

    groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DistListModule distList = distListAdapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(),DistListDetailActivity.class);
        intent.putExtra("distListId",distList.getDist_id());
        startActivity(intent);
      }
    });

    groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        DistListModule distList = distListAdapter.getItem(position);
        showRemovePopup(distList);
        return false;
      }
    });

  }

  private void showRemovePopup(final DistListModule distListModule){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    // set dialog message
    alertDialogBuilder
        .setMessage("Remove Distribution List "+distListModule.getDist_name())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {

            DistListModule.getInstance().deleteDistList(getApplicationContext(),distListModule.getDist_id());

            distListAdapter.remove(distListModule);
            distListAdapter.notifyDataSetChanged();
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

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    MenuItem item = menu.findItem(R.id.action_dist_list);
    item.setVisible(false);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.action_property:
        Intent intent = new Intent(this, PropertyListingActivity.class);
        startActivity(intent);
        finish();
        return true;

      case R.id.action_requirement:
        Intent i = new Intent(this, RequirementListingActivity.class);
        startActivity(i);
        finish();
        return true;
    }

    return false;
  }

  private void OauthToken(){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        "http://192.168.7.102/propaget/public/oauth/token",
        getOauthParams(),
        requestListener,
        requestErrorListener,
        getApplicationContext()
    );
    AppController.getInstance().addToRequestQueue(postRequest);
  }

  public Map<String,String> getOauthParams(){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("username", "amitav.roy@focalworks.in");
    jsonParams.put("password", "password");
    jsonParams.put("client_id", "testclient");
    jsonParams.put("client_secret", "testpass");
    jsonParams.put("grant_type", "password");
//    b587d71a30e4c010eee38d014e2f12a8d6b54088
    return jsonParams;
  }

  Response.Listener<String> requestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
      Log.i("Reponse",response);
    }
  };

  Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.i("Error Reponse",error.toString());
    }
  };


  private void getToken(){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.GET,
        "http://192.168.7.102/propaget/public/dist-list?access_token=1f90e4fc073953cda7c2edaee759807af1d74fecc",
        null,
        requestListener,
        requestErrorListener,
        getApplicationContext()
    );
    AppController.getInstance().addToRequestQueue(postRequest);
  }
}
