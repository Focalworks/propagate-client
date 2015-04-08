package propagate.com.propagate_client.property;

import android.app.Activity;
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
import propagate.com.propagate_client.utils.CustomAdapterInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 19/3/15.
 */
public class PropertyListAdapter extends ArrayAdapter<PropertyModule> {

  private ArrayList<PropertyModule> propertyArrayList;
  private Activity activity;
  private int property_row_view;
  private CustomAdapterInterface customAdapterInterface;

  public PropertyListAdapter(Activity activity, int resource, ArrayList<PropertyModule> propertyArrayList) {
    super(activity, resource, propertyArrayList);
    this.activity = activity;
    this.property_row_view = resource;
    this.propertyArrayList = propertyArrayList;
    customAdapterInterface = (CustomAdapterInterface) activity;
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

      convertView.setTag(new PropertyViewHolder(txtTitle,txtLocation,txtArea,txtPrice,txtType,imgRetry));
    }else{
      PropertyViewHolder propertyViewHolder = (PropertyViewHolder) convertView.getTag();
      txtTitle = propertyViewHolder.getTxtTitle();
      txtLocation = propertyViewHolder.getTxtLocation();
      txtArea = propertyViewHolder.getTxtArea();
      txtPrice = propertyViewHolder.getTxtPrice();
      txtType = propertyViewHolder.getTxtType();
      imgRetry = propertyViewHolder.getImgRetry();
    }

    if(propertyModule != null){
      txtTitle.setText(propertyModule.getTitle());
      txtLocation.setText(propertyModule.getLocation());
      txtArea.setText(propertyModule.getArea());
      txtPrice.setText(propertyModule.getPrice());
      txtType.setText(propertyModule.getType());

      if(propertyModule.getStatus() == 0){
        imgRetry.setVisibility(View.VISIBLE);
        imgRetry.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            long prop_id = propertyModule.getP_id();
            customAdapterInterface.OnBtnClick(prop_id);
          }
        });
      }
    }

    return convertView;
  }

  @Override
  public boolean isEnabled(int position) {
    /*final PropertyModule propertyModule = (PropertyModule) this.getItem(position);
    if(propertyModule.getStatus() == 0)
    {
      return false;
    }*/
    return super.isEnabled(position);
  }
}
