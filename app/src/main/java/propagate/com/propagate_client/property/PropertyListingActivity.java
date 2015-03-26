package propagate.com.propagate_client.property;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.Requirement.RequirementListingActivity;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.utils.CustomAdapterInterface;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;
import propagate.com.propagate_client.volleyRequest.VolleyStringRequest;

/**
 * Created by kaustubh on 19/3/15.
 */
public class PropertyListingActivity extends Activity implements APIHandlerInterface,CustomAdapterInterface{

  ListView propertyListView;
  ImageView imgAddProperty;
  PropertyListAdapter propertyListAdapter;
  long property_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    imgAddProperty = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddProperty.setVisibility(View.VISIBLE);
    imgAddProperty.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadAddPropertyActivity();
      }
    });

    /*
    * pass id:0 to get all records and pass individual id to get individual record
    * */
    ArrayList<PropertyModule> propertyList = PropertyModule.getInstance().getPropertyInfo(this, 0);

    propertyListAdapter = new PropertyListAdapter(this, R.layout.custom_property_view,propertyList);

    propertyListView = (ListView) findViewById(R.id.listingListView);
    propertyListView.setAdapter(propertyListAdapter);

    propertyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PropertyModule propertyModule = propertyListAdapter.getItem(position);
        showRemovePopup(propertyModule);
        return false;
      }
    });

    propertyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PropertyModule propertyModule = propertyListAdapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(),PropertyDetailActivity.class);
        intent.putExtra("propertyModule",propertyModule);
        startActivity(intent);
        finish();
      }
    });

//    testErrorMessage();
  }

  private void loadAddPropertyActivity(){
    Intent intent = new Intent(getApplicationContext(),AddPropertyActivity.class);
    startActivity(intent);
    finish();
  }

  private void testErrorMessage(){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.GET,
        Constants.test,
        null,
        requestListener,
        requestErrorListener,
        getApplicationContext()
    );
    AppController.getInstance().addToRequestQueue(postRequest);
  }

  Response.Listener<String> requestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
    }
  };

  Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      String json = null;
      NetworkResponse response = error.networkResponse;
      if(response != null && response.data != null){
        switch(response.statusCode){
          case 422:
            json = new String(response.data);
            json = CommonFunctions.trimMessage(json, "message");
            if(json != null) Toast.makeText(getApplicationContext(),""+json,Toast.LENGTH_SHORT).show();
            break;

          case 500:
            json = new String(response.data);
            json = CommonFunctions.trimMessage(json, "message");
            if(json != null) Toast.makeText(getApplicationContext(),""+json,Toast.LENGTH_SHORT).show();
            break;
        }
      }
    }
  };

  private void showRemovePopup(final PropertyModule propertyModule){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    // set dialog message
    alertDialogBuilder
        .setMessage("Remove property "+propertyModule.getTitle())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {
            PropertyModule.getInstance().deleteProperty(getApplicationContext(),propertyModule.getP_id());
            propertyListAdapter.remove(propertyModule);
            propertyListAdapter.notifyDataSetChanged();
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

    MenuItem item = menu.findItem(R.id.action_property);
    item.setVisible(false);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.action_dist_list:
        Intent intent = new Intent(this, DistListingActivity.class);
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

  @Override
  public void OnRequestResponse(String response) {

  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    if(error instanceof NoConnectionError)
      Log.e("error response", "NoConnectionError");
    else if(error.networkResponse != null){
      Log.e("error code", "" + error.networkResponse.statusCode);
    }
  }

  @Override
  public void OnBtnClick(long id) {
    property_id = id;
  }
}
