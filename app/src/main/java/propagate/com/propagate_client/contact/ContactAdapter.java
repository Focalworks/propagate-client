package propagate.com.propagate_client.contact;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.utils.RoundImage;

/**
 * Created by kaustubh on 21/1/15.
 */
public class ContactAdapter extends ArrayAdapter<Contact> implements Filterable{

  private ArrayList<Contact> contactArrayList;
  private ArrayList<Contact> temp_contactArrayList;
  private ArrayList<Contact> suggestions;
  private Activity activity;
  private String action;
  private int row_contactID;
  private GroupInterface groupInterface;

  public ContactAdapter(Activity activity,int row_contactID,ArrayList<Contact> contactArrayList,String action) {
    super(activity, row_contactID, contactArrayList);
    this.activity = activity;
    this.contactArrayList = contactArrayList;
    this.temp_contactArrayList = (ArrayList<Contact>) contactArrayList.clone();
    this.suggestions = new ArrayList<Contact>();
    this.action = action;
    this.row_contactID = row_contactID;
    groupInterface = (GroupInterface) activity;
  }

  @Override
  public Contact getItem(int position) {
    return contactArrayList.get(position);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    final Contact contact = (Contact) this.getItem( position );

    RoundImage roundedImage;
    TextView txtName;
    CheckBox chkSelect;
    ImageView profileImg;
    ImageView imgRemove;

    if(convertView == null){
      LayoutInflater inflater = activity.getLayoutInflater();
      convertView = inflater.inflate(row_contactID, parent, false);

      txtName = (TextView) convertView.findViewById(R.id.customContactTxtMemberName);
      profileImg = (ImageView) convertView.findViewById(R.id.customContactImgContact);
      chkSelect = (CheckBox) convertView.findViewById(R.id.customContactChkSelect);
      imgRemove = (ImageView) convertView.findViewById(R.id.customContactImgRemove);

      if(action.equals("addContactList")) {
        chkSelect.setVisibility(View.VISIBLE);
      }else if(action.equals("distMembersList")){
        imgRemove.setVisibility(View.VISIBLE);
      }

      // store the holder with the view.
      convertView.setTag(new ContactViewHolder(txtName,chkSelect,profileImg,imgRemove));

    }else{
      ContactViewHolder contactViewHolder = (ContactViewHolder) convertView.getTag();
      txtName = contactViewHolder.getTxtName();
      chkSelect = contactViewHolder.getChkSelect();
      profileImg = contactViewHolder.getProfileImg();
      imgRemove = contactViewHolder.getImgRemove();
    }
    try {
      if( contactArrayList.get(position) != null){
        txtName.setText(contact.getName());

        if(contactArrayList.get(position).getProfile_pic() != null){
          try {
            InputStream photo_stream = activity.getContentResolver().openInputStream(Uri.parse(contact.getProfile_pic()));
            Bitmap bitmap = BitmapFactory.decodeStream(photo_stream);
            roundedImage = new RoundImage(bitmap);
            profileImg.setImageDrawable(roundedImage);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }else{
          Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.default_contact);
          roundedImage = new RoundImage(bitmap);
          profileImg.setImageDrawable(roundedImage);
        }
        chkSelect.setChecked(contact.isChecked());
        imgRemove.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            groupInterface.OnRemoveContactClick(position,contact);
          }
        });

      }
    }
    catch (Exception e) {
      Log.e("Exception",""+e);
    }
    return convertView;
  }

  @Override
  public Filter getFilter() {
    return nameFilter;
  }

  Filter nameFilter = new Filter() {
    public String convertResultToString(Object resultValue) {
      String str = ((Contact) (resultValue)).getName();
      return str;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      FilterResults filterResults = new FilterResults();
      if (constraint != null) {
        suggestions.clear();
        for (Contact contact : temp_contactArrayList) {
          if (contact.getName().toLowerCase().contains(constraint.toString().toLowerCase()) && !contact.isChecked()) {
            suggestions.add(contact);
          }
        }

        filterResults.values = suggestions;
        filterResults.count = suggestions.size();
      }
      return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint,FilterResults results) {
      clear();
      if (results != null && results.count > 0) {
        // we have filtered results
        addAll((ArrayList<Contact>) results.values);
      } else {
        // no filter, add entire original list back in
        addAll(contactArrayList);
      }
      notifyDataSetChanged();
    }
  };

  public interface GroupInterface{
    public void OnRemoveContactClick(int pos, Contact contact);
  }

}
