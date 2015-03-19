package propagate.com.propagate_client.property;

import android.widget.TextView;

/**
 * Created by kaustubh on 19/3/15.
 */
public class PropertyViewHolder {

  private TextView txtTitle;
  private TextView txtLocation;
  private TextView txtArea;
  private TextView txtPrice;
  private TextView txtType;

  public PropertyViewHolder(TextView txtTitle, TextView txtLocation, TextView txtArea, TextView txtPrice, TextView txtType) {
    this.txtTitle = txtTitle;
    this.txtLocation = txtLocation;
    this.txtArea = txtArea;
    this.txtPrice = txtPrice;
    this.txtType = txtType;
  }

  public TextView getTxtTitle() {
    return txtTitle;
  }

  public void setTxtTitle(TextView txtTitle) {
    this.txtTitle = txtTitle;
  }

  public TextView getTxtLocation() {
    return txtLocation;
  }

  public void setTxtLocation(TextView txtLocation) {
    this.txtLocation = txtLocation;
  }

  public TextView getTxtArea() {
    return txtArea;
  }

  public void setTxtArea(TextView txtArea) {
    this.txtArea = txtArea;
  }

  public TextView getTxtPrice() {
    return txtPrice;
  }

  public void setTxtPrice(TextView txtPrice) {
    this.txtPrice = txtPrice;
  }

  public TextView getTxtType() {
    return txtType;
  }

  public void setTxtType(TextView txtType) {
    this.txtType = txtType;
  }

}
