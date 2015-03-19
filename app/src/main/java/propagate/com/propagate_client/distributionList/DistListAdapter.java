package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.utils.CommonFunctions;

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

    if(convertView == null){
      LayoutInflater layoutInflater = activity.getLayoutInflater();
      convertView = layoutInflater.inflate(group_row_view, parent, false);

      txtGroupName = (TextView) convertView.findViewById(R.id.customDistViewTxtGroupName);
      txtMembersCount = (TextView) convertView.findViewById(R.id.customDistViewTxtMembersCount);

      convertView.setTag(new DistViewHolder(txtGroupName,txtMembersCount));
    } else {
      DistViewHolder distViewHolder = (DistViewHolder) convertView.getTag();
      txtGroupName = distViewHolder.getTxtGroupName();
      txtMembersCount = distViewHolder.getTxtMembersNumber();
    }

    if(groupArrayList.get(position) != null){
      txtGroupName.setText(distListModule.getDist_name());
      txtMembersCount.setText(CommonFunctions.getPlurals(distListModule.getCount(), "Member"));
    }

    return convertView;
  }
}
