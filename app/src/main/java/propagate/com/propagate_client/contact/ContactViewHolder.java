package propagate.com.propagate_client.contact;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kaustubh on 28/1/15.
 */
public class ContactViewHolder {
  TextView txtName;
  CheckBox chkSelect;
  ImageView profileImg;
  ImageView imgRemove;

  public ContactViewHolder(TextView txtName, CheckBox chkSelect, ImageView profileImg,ImageView imgRemove) {
    this.txtName = txtName;
    this.chkSelect = chkSelect;
    this.profileImg = profileImg;
    this.imgRemove = imgRemove;
  }

  public TextView getTxtName() {
    return txtName;
  }

  public void setTxtName(TextView txtName) {
    this.txtName = txtName;
  }

  public CheckBox getChkSelect() {
    return chkSelect;
  }

  public void setChkSelect(CheckBox chkSelect) {
    this.chkSelect = chkSelect;
  }

  public ImageView getProfileImg() {
    return profileImg;
  }

  public void setProfileImg(ImageView profileImg) {
    this.profileImg = profileImg;
  }

  public ImageView getImgRemove() {
    return imgRemove;
  }

  public void setImgRemove(ImageView imgRemove) {
    this.imgRemove = imgRemove;
  }
}
