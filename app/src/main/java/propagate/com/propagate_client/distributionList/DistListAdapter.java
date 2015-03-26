package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 4/2/15.
 */
public class DistListAdapter extends ArrayAdapter<DistListModule> {

  private ArrayList<DistListModule> groupArrayList;
  private Activity activity;
  private int group_row_view;

  public DistListAdapter(Activity activity, int group_row_view, ArrayList<DistListModule> groupArrayList) {
    super(activity, group_row_view, groupArrayList);
    this.activity = activity;
    this.groupArrayList = groupArrayList;
    this.group_row_view = group_row_view;
  }

  @Override
  public DistListModule getItem(int position) {
    return groupArrayList.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final DistListModule distListModule = (DistListModule) this.getItem(position);

    TextView txtGroupName;
    TextView txtMembersCount;
    ImageView imgRetry;

    if(convertView == null){
      LayoutInflater layoutInflater = activity.getLayoutInflater();
      convertView = layoutInflater.inflate(group_row_view, parent, false);

      txtGroupName = (TextView) convertView.findViewById(R.id.customDistViewTxtGroupName);
      txtMembersCount = (TextView) convertView.findViewById(R.id.customDistViewTxtMembersCount);
      imgRetry = (ImageView) convertView.findViewById(R.id.customDistViewImgRetry);

      convertView.setTag(new DistViewHolder(txtGroupName,txtMembersCount,imgRetry));
    } else {
      DistViewHolder distViewHolder = (DistViewHolder) convertView.getTag();
      txtGroupName = distViewHolder.getTxtGroupName();
      txtMembersCount = distViewHolder.getTxtMembersNumber();
      imgRetry = distViewHolder.getImgRetry();
    }

    if(distListModule != null){
      txtGroupName.setText(distListModule.getDist_name());
      txtMembersCount.setText(CommonFunctions.getPlurals(distListModule.getCount(), "Member"));

      if(Integer.parseInt(distListModule.getStatus()) == 0){
        imgRetry.setVisibility(View.VISIBLE);
        imgRetry.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AppController.getInstance().postCreateDistList(activity,distListModule.getDist_id());
          }
        });
      }
    }

    return convertView;
  }

  @Override
  public boolean isEnabled(int position) {
    final DistListModule distListModule = (DistListModule) this.getItem(position);
    if(Integer.parseInt(distListModule.getStatus()) == 0)
    {
      return false;
    }
    return super.isEnabled(position);
  }
}
