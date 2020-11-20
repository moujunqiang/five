package com.example.task1.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.task1.myapplication.db.Rank;
import com.example.task1.myapplication.net.ConnectionItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends BaseAdapter {

    //声明一个动态数组用于存局域网中的设备、ip等数据
    private List<Rank> mData = new ArrayList<Rank>();
    private Context mContext;
   //构造函数，初始化变量
    public RankAdapter(Context context, List<Rank> data) {
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
            convertView = inflate.inflate(R.layout.rand_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.score = (TextView) convertView.findViewById(R.id.score);
            holder.num = (TextView) convertView.findViewById(R.id.num);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Rank item = mData.get(position);
        holder.name.setText(item.getUserId());
        holder.num.setText(position+"");
        holder.score.setText(item.getScore()+"");
        return convertView;
    }

    //列表刷新
    public void changeData(List<Rank> data){
        mData = data;
        notifyDataSetChanged();
    }

    //界面中展示的数据一个是设备名，一个是ip地址
    class ViewHolder{
        TextView name;
        TextView score;
        TextView num;
    }
}
