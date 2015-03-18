package propagate.com.propagate_client.distributionList;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kaustubh on 4/2/15.
 */
public class DistViewHolder {

  TextView txtGroupName;
  TextView txtMembersNumber;

  public DistViewHolder(TextView txtGroupName, TextView txtMembersNumber) {
    this.txtGroupName = txtGroupName;
    this.txtMembersNumber = txtMembersNumber;
  }

  public TextView getTxtGroupName() {
    return txtGroupName;
  }

  public void setTxtGroupName(TextView txtGroupName) {
    this.txtGroupName = txtGroupName;
  }

  public TextView getTxtMembersNumber() {
    return txtMembersNumber;
  }

  public void setTxtMembersNumber(TextView txtMembersNumber) {
    this.txtMembersNumber = txtMembersNumber;
  }

}
