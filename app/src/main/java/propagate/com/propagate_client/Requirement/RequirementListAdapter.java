package propagate.com.propagate_client.Requirement;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.utils.CustomAdapterInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 19/3/15.
 */
public class RequirementListAdapter extends ArrayAdapter<RequirementModule> {

  private ArrayList<RequirementModule> requestArrayList;
  private Activity activity;
  private int property_row_view;
  private CustomAdapterInterface customAdapterInterface;

  public RequirementListAdapter(Activity activity, int resource, ArrayList<RequirementModule> propertyArrayList) {
    super(activity, resource, propertyArrayList);
    this.activity = activity;
    this.property_row_view = resource;
    this.requestArrayList = propertyArrayList;
    customAdapterInterface = (CustomAdapterInterface) activity;
  }

  @Override
  public RequirementModule getItem(int position) {
    return requestArrayList.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final RequirementModule requirementModule = (RequirementModule) this.getItem(position);

    TextView txtTitle;
    TextView txtLocation;
    TextView txtArea;
    TextView txtPrice;
    TextView txtType;
    ImageView imgRetry;

    if(convertView == null){
      LayoutInflater layoutInflater = activity.getLayoutInflater();
      convertView = layoutInflater.inflate(property_row_view,parent,false);

      txtTitle = (TextView) convertView.findViewById(R.id.property_listing_title);
      txtLocation = (TextView) convertView.findViewById(R.id.property_listing_location);
      txtArea = (TextView) convertView.findViewById(R.id.property_listing_area);
      txtPrice = (TextView) convertView.findViewById(R.id.property_listing_price);
      txtType = (TextView) convertView.findViewById(R.id.property_listing_type);
      imgRetry = (ImageView) convertView.findViewById(R.id.property_listing_retry);

      convertView.setTag(new RequirementViewHolder(txtTitle,txtLocation,txtArea,txtPrice,txtType,imgRetry));
    }else{
      RequirementViewHolder requirementViewHolder = (RequirementViewHolder) convertView.getTag();
      txtTitle = requirementViewHolder.getTxtTitle();
      txtLocation = requirementViewHolder.getTxtLocation();
      txtArea = requirementViewHolder.getTxtArea();
      txtPrice = requirementViewHolder.getTxtPrice();
      txtType = requirementViewHolder.getTxtType();
      imgRetry = requirementViewHolder.getImgRetry();
    }

    if(requirementModule != null){
      txtTitle.setText(requirementModule.getTitle());
      txtLocation.setText(requirementModule.getLocation());
      txtArea.setText(requirementModule.getArea());
      txtPrice.setText(requirementModule.getPrice());
      txtType.setText(requirementModule.getType());

      Log.e("status",requirementModule.getStatus()+"");
      if(requirementModule.getStatus() == 0){
        imgRetry.setVisibility(View.VISIBLE);
        imgRetry.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            long req_id = requirementModule.getR_id();
            customAdapterInterface.OnBtnClick(req_id);
            AppController.getInstance().postCreateRequirement(activity,req_id);
          }
        });
      }
    }

    return convertView;
  }

  @Override
  public boolean isEnabled(int position) {
    final RequirementModule requirementModule = (RequirementModule) this.getItem(position);
    if(requirementModule.getStatus() == 0)
    {
      return false;
    }
    return super.isEnabled(position);
  }
}
