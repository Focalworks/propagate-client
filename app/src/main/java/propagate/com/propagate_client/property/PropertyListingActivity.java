package propagate.com.propagate_client.property;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.Requirement.RequirementListAdapter;
import propagate.com.propagate_client.Requirement.RequirementListingActivity;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.utils.CustomAdapterInterface;
import propagate.com.propagate_client.volleyRequest.APIHandler;
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
  PropertyModule propertyModule;
  private ProgressDialog ringProgressDialog;
  private Animation animUP;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    animUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.plus_slide_up);

    imgAddProperty = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddProperty.setVisibility(View.VISIBLE);
    imgAddProperty.startAnimation(animUP);
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

  }

  private void loadAddPropertyActivity(){
    Intent intent = new Intent(getApplicationContext(),AddPropertyActivity.class);
    startActivity(intent);
    finish();
  }

  private void deleteProperty(long prop_id){
    APIHandler.getInstance(PropertyListingActivity.this).restAPIRequest(
        Request.Method.DELETE,
        Constants.postPropertyUrl+"/"+prop_id,
        null,
        null
    );
    launchProgressDialog("Deleting Property ...");
  }

  private void showRemovePopup(final PropertyModule propertyModule){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    this.propertyModule = propertyModule;
    // set dialog message
    alertDialogBuilder
        .setMessage("Remove property "+propertyModule.getTitle()+" "+propertyModule.getServer_prop_id())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {
            dialog.dismiss();
            property_id = propertyModule.getP_id();
            deleteProperty(propertyModule.getServer_prop_id());
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

  public void launchProgressDialog(String msg) {
    ringProgressDialog = ProgressDialog.show(PropertyListingActivity.this, "Please wait ...", msg, true);
    ringProgressDialog.setCancelable(false);
  }

  public void dismissProgressDialog() {
    ringProgressDialog.dismiss();
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
    if(response != null){
      String message = CommonFunctions.trimMessage(response, "message");
      Log.i("message", message);
      if(message != null)
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

      String data = CommonFunctions.trimMessage(response, "data");
      if(data != null) {
        try {
          Log.i("data", data);
          JSONObject jsonObj = new JSONObject(data);
          String type = jsonObj.getString("type");
          switch (type) {
            case "save":
              JSONObject list = new JSONObject(jsonObj.getString("prop"));
              long server_prop_id = list.getLong("id");
              PropertyModule.getInstance().updatePropertyStatus(getApplicationContext(), property_id, server_prop_id);
              ArrayList<PropertyModule> propertyList = PropertyModule.getInstance().getPropertyInfo(this, 0);
              propertyListAdapter = new PropertyListAdapter(this, R.layout.custom_property_view, propertyList);
              propertyListView.setAdapter(propertyListAdapter);

              break;
            case "delete":
              PropertyModule.getInstance().deleteProperty(getApplicationContext(), property_id);
              propertyListAdapter.remove(propertyModule);
              propertyListAdapter.notifyDataSetChanged();
              break;
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      dismissProgressDialog();
    }
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    if(error instanceof NoConnectionError)
      Toast.makeText(getApplicationContext(),"No Connection Error",Toast.LENGTH_SHORT).show();
    else if(error.networkResponse != null){
      CommonFunctions.errorResponseHandler(getApplicationContext(), error);
    }
    dismissProgressDialog();
  }

  @Override
  public void OnBtnClick(long id) {
    property_id = id;
    launchProgressDialog("Creating Property ...");
    AppController.getInstance().postCreateProperty(PropertyListingActivity.this,property_id);
  }
}
