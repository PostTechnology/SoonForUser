package com.soon.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.R;
import com.soon.android.adapterDataModels.Discover;
import com.soon.android.adapterDataModels.Image;
import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridAdapter;
import com.w4lle.library.NineGridlayout;

import java.util.List;

public class DiscoverListAdapter extends BaseAdapter {
    private Context context;
    private List<Discover> datalist;
    private NineGridAdapter adapter;

    public DiscoverListAdapter(Context context, List<Discover> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Discover getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Discover discover = getItem(position);
        List<Image> itemList = getItem(position).getImages();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.discover_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
            viewHolder.username = (TextView) convertView.findViewById(R.id.name);
            viewHolder.usercomment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.commenttime = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (itemList.isEmpty() || itemList.isEmpty()) {
            viewHolder.ivMore.setVisibility(View.GONE);
        } else {
            viewHolder.ivMore.setVisibility(View.VISIBLE);
            viewHolder.username.setText(discover.getUsername());
            viewHolder.usercomment.setText(discover.getComment());
            viewHolder.commenttime.setText(discover.getTime());
            handlerOneImage(viewHolder, itemList);
        }

        return convertView;
    }

    private void handlerOneImage(ViewHolder viewHolder, List<Image> image) {
        adapter = new Adapter(context, image);
        viewHolder.ivMore.setAdapter(adapter);
        viewHolder.ivMore.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
            @Override
            public void onItemClick(View view, int position) {
                //do some thing
                Toast.makeText(context, "点击了第" + position + "张图片", Toast.LENGTH_SHORT).show();
            }
        });
    }


    class ViewHolder {
        public NineGridlayout ivMore;
        public TextView username;
        public TextView usercomment;
        public TextView commenttime;
    }

    class Adapter extends NineGridAdapter {

        public Adapter(Context context, List list) {
            super(context, list);
        }

        @Override
        public int getCount() {
            return (list == null) ? 0 : list.size();
        }

        @Override
        public String getUrl(int position) {
            return getItem(position) == null ? null : ((Image)getItem(position)).getUrl();
        }

        @Override
        public Object getItem(int position) {
            return (list == null) ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view) {
            ImageView iv = null;
            if (view != null && view instanceof ImageView) {
                iv = (ImageView) view;
            } else {
                iv = new ImageView(context);
            }
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setBackgroundColor(context.getResources().getColor((android.R.color.transparent)));
            String url = getUrl(i);
            Picasso.with(context).load(getUrl(i)).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(iv);
            if (!TextUtils.isEmpty(url)) {
                iv.setTag(url);
            }
            return iv;
        }
    }
}