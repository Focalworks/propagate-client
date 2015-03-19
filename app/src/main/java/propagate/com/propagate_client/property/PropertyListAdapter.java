package propagate.com.propagate_client.property;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;

/**
 * Created by kaustubh on 19/3/15.
 */
public class PropertyListAdapter extends ArrayAdapter<PropertyModule> {

  private ArrayList<PropertyModule> propertyArrayList;
  private Activity activity;
  private int property_row_view;

  public PropertyListAdapter(Activity activity, int resource, ArrayList<PropertyModule> propertyArrayList) {
    super(activity, resource, propertyArrayList);
    this.activity = activity;
    this.property_row_view = resource;
    this.propertyArrayList = propertyArrayList;
  }

  @Override
  public PropertyModule getItem(int position) {
    return propertyArrayList.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final PropertyModule propertyModule = (PropertyModule) this.getItem(position);

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

      convertView.setTag(new PropertyViewHolder(txtTitle,txtLocation,txtArea,txtPrice,txtType));
    }else{
      PropertyViewHolder propertyViewHolder = (PropertyViewHolder) convertView.getTag();
      txtTitle = propertyViewHolder.getTxtTitle();
      txtLocation = propertyViewHolder.getTxtLocation();
      txtArea = propertyViewHolder.getTxtArea();
      txtPrice = propertyViewHolder.getTxtPrice();
      txtType = propertyViewHolder.getTxtType();
    }

    if(propertyArrayList.get(position) != null){
      txtTitle.setText(propertyArrayList.get(position).getTitle());
      txtLocation.setText(propertyArrayList.get(position).getLocation());
      txtArea.setText(propertyArrayList.get(position).getArea());
      txtPrice.setText(propertyArrayList.get(position).getPrice());
      txtType.setText(propertyArrayList.get(position).getType());
    }

    return convertView;
  }
}
