package propagate.com.propagate_client.Requirement;

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

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.property.PropertyListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.utils.CustomAdapterInterface;
import propagate.com.propagate_client.volleyRequest.APIHandler;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 19/3/15.
 */
public class RequirementListingActivity extends Activity implements APIHandlerInterface,CustomAdapterInterface {

  ListView requirementListView;
  ImageView imgAddReq;
  RequirementListAdapter requirementListAdapter;
  private long requirement_id;
  private RequirementModule requirementModule;
  private ProgressDialog ringProgressDialog;
  private Animation animUP;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    animUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.plus_slide_up);

    imgAddReq = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddReq.setVisibility(View.VISIBLE);
    imgAddReq.startAnimation(animUP);
    imgAddReq.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadAddRequirementActivity();
      }
    });

    /*
    * pass id:0 to get all records and pass individual id to get individual record
    * */
    ArrayList<RequirementModule> requirementList = RequirementModule.getInstance().getRequirementInfo(this, 0);

    requirementListAdapter = new RequirementListAdapter(this, R.layout.custom_property_view,requirementList);

    requirementListView = (ListView) findViewById(R.id.listingListView);
    requirementListView.setAdapter(requirementListAdapter);

    requirementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RequirementModule requirementModule = requirementListAdapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(),RequirementDetailActivity.class);
        intent.putExtra("requirementModule",requirementModule);
        startActivity(intent);
        finish();
      }
    });

    requirementListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        RequirementModule requirementModule = requirementListAdapter.getItem(position);
        showRemovePopup(requirementModule);
        return false;
      }
    });
  }

  private void loadAddRequirementActivity(){
    Intent intent = new Intent(getApplicationContext(),AddRequirementActivity.class);
    startActivity(intent);
    finish();
  }

  private void showRemovePopup(final RequirementModule requirementModule){
    this.requirementModule = requirementModule;
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    // set dialog message
    alertDialogBuilder
        .setMessage("Remove requirement "+requirementModule.getTitle())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {
            dialog.dismiss();
            requirement_id = requirementModule.getR_id();
            deleteRequirement(requirementModule.getServer_req_id());
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

  private void deleteRequirement(long req_id){
    Log.e("Delete req",req_id+"");
    launchProgressDialog("Deleting requirement ...");
    APIHandler.getInstance(RequirementListingActivity.this).restAPIRequest(
        Request.Method.DELETE,
        Constants.postRequirementUrl + "/" + req_id,
        null,
        null
    );
  }

  public void launchProgressDialog(String msg) {
    ringProgressDialog = ProgressDialog.show(RequirementListingActivity.this, "Please wait ...", msg, true);
    ringProgressDialog.setCancelable(false);
  }

  public void dismissProgressDialog() {
    ringProgressDialog.dismiss();
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    MenuItem item = menu.findItem(R.id.action_requirement);
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

      case R.id.action_property:
        Intent i = new Intent(this, PropertyListingActivity.class);
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
              JSONObject list = new JSONObject(jsonObj.getString("req"));
              long server_req_id = list.getLong("id");
              RequirementModule.getInstance().updateRequirementStatus(getApplicationContext(), requirement_id, server_req_id);
              ArrayList<RequirementModule> requirementList = RequirementModule.getInstance().getRequirementInfo(this, 0);
              requirementListAdapter = new RequirementListAdapter(this, R.layout.custom_property_view, requirementList);
              requirementListView.setAdapter(requirementListAdapter);
              break;
            case "delete":
              RequirementModule.getInstance().deleteProperty(getApplicationContext(), requirement_id);
              requirementListAdapter.remove(requirementModule);
              requirementListAdapter.notifyDataSetChanged();
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
    requirement_id = id;
    launchProgressDialog("Creating Requirement...");
    AppController.getInstance().postCreateRequirement(RequirementListingActivity.this,requirement_id);
  }
}
