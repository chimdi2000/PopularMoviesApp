package android.dindu.com.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caneke on 6/7/15.
 */
public class GridImageAdapter extends BaseAdapter {


    static List<String> movieUrls = new ArrayList<>();
    static List<String> movieTitles = new ArrayList<>();
    private final LayoutInflater mInflater;
    final Context mContext;

    public GridImageAdapter(Context context) {
        this.mContext = context;

        mInflater = LayoutInflater.from(context);
    }

    public String getTitle(int i) {
        return movieTitles.get(i);
    }

    @Override
    public int getCount() {
        return movieUrls.size();
    }

    @Override
    public String getItem(int i) {
        return movieUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup container) {
        View convertView = view;
        ImageView thumbnail;
        TextView name;

        if (view == null) {
            convertView = mInflater.inflate(R.layout.grid_item, container, false);

            convertView.setTag(R.id.thumbnail, convertView.findViewById(R.id.thumbnail));
            convertView.setTag(R.id.mainTitleText, convertView.findViewById(R.id.mainTitleText));
        }

        thumbnail = (ImageView) convertView.getTag(R.id.thumbnail);
        name = (TextView) convertView.getTag(R.id.mainTitleText);
        name.setText(getTitle(i).toString());
        String urls = getItem(i).toString();

        Glide.with(mContext)
                .load(urls)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .crossFade()
                .into(thumbnail);

        return convertView;
    }


}
