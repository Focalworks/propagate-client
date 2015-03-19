package propagate.com.propagate_client.Requirement;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.RequirementModule;

/**
 * Created by kaustubh on 19/3/15.
 */
public class RequirementListAdapter extends ArrayAdapter<RequirementModule> {

  private ArrayList<RequirementModule> requestArrayList;
  private Activity activity;
  private int property_row_view;

  public RequirementListAdapter(Activity activity, int resource, ArrayList<RequirementModule> propertyArrayList) {
    super(activity, resource, propertyArrayList);
    this.activity = activity;
    this.property_row_view = resource;
    this.requestArrayList = propertyArrayList;
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

    if(convertView == null){
      LayoutInflater layoutInflater = activity.getLayoutInflater();
      convertView = layoutInflater.inflate(property_row_view,parent,false);

      txtTitle = (TextView) convertView.findViewById(R.id.property_listing_title);
      txtLocation = (TextView) convertView.findViewById(R.id.property_listing_location);
      txtArea = (TextView) convertView.findViewById(R.id.property_listing_area);
      txtPrice = (TextView) convertView.findViewById(R.id.property_listing_price);
      txtType = (TextView) convertView.findViewById(R.id.property_listing_type);

      convertView.setTag(new RequirementViewHolder(txtTitle,txtLocation,txtArea,txtPrice,txtType));
    }else{
      RequirementViewHolder requirementViewHolder = (RequirementViewHolder) convertView.getTag();
      txtTitle = requirementViewHolder.getTxtTitle();
      txtLocation = requirementViewHolder.getTxtLocation();
      txtArea = requirementViewHolder.getTxtArea();
      txtPrice = requirementViewHolder.getTxtPrice();
      txtType = requirementViewHolder.getTxtType();
    }

    if(requestArrayList.get(position) != null){
      txtTitle.setText(requestArrayList.get(position).getTitle());
      txtLocation.setText(requestArrayList.get(position).getLocation());
      txtArea.setText(requestArrayList.get(position).getArea());
      txtPrice.setText(requestArrayList.get(position).getPrice());
      txtType.setText(requestArrayList.get(position).getType());
    }

    return convertView;
  }
}
