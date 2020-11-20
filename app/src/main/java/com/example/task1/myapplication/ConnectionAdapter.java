package com.example.task1.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.task1.myapplication.net.ConnectionItem;
import com.example.task1.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ConnectionAdapter extends BaseAdapter {

    //声明一个动态数组用于存局域网中的设备、ip等数据
    private List<ConnectionItem> mData = new ArrayList<ConnectionItem>();
    private Context mContext;
   //构造函数，初始化变量
    public ConnectionAdapter(Context context, List<ConnectionItem> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    //用来获得指定位置要显示的View 使用covertView重用资源
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflate = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.ip = (TextView) convertView.findViewById(R.id.ip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ConnectionItem item = mData.get(position);
        holder.name.setText(item.name);
        holder.ip.setText(item.ip);
        return convertView;
    }

    //列表刷新
    public void changeData(List<ConnectionItem> data){
        mData = data;
        notifyDataSetChanged();
    }

    //界面中展示的数据一个是设备名，一个是ip地址
    class ViewHolder{
        TextView name;
        TextView ip;
    }
}
