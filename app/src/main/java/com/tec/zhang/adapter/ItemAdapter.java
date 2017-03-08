package com.tec.zhang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tec.zhang.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by zhang on 2017/1/21.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private List<ProjItem> projectList;
    private final Context mContext;
    private OnItemClickListener listener;
    public ItemAdapter(List<ProjItem> projectList,Context mContext){
        this.projectList = projectList;
        this.mContext = mContext;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (projectList !=null) {
            ProjItem i = projectList.get(position);
            Picasso.with(mContext)
                    .load(i.imageView)
                    .resizeDimen(R.dimen.kuan,R.dimen.gao)
                    .into(holder.cir);
            holder.fullName.setText(i.fullNme);
            holder.state.setText(i.state);
            String man = null;
            if (i.orderMan.contains("，")) {
                man = i.orderMan.substring(i.orderMan.lastIndexOf("，") + 1);
            } else {
                man = i.orderMan;
            }
            holder.orderMan.setText(man);
            final String finalMan = man;
            holder.orderMan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onOrderManClick(holder.orderMan,position);
                    }
                    /*List<ProjItem> personalList = new ArrayList<>();
                    for (ProjItem item : projectList){
                        if (item.orderMan.equals(finalMan)){
                            personalList.add(item);
                        }
                    }
                    if (!personalList.isEmpty()){
                        projectList.clear();
                        for (ProjItem item : personalList){
                            projectList.add(item);
                        }
                        ItemAdapter.this.notifyDataSetChanged();
                    }*/
                }
            });
            if (listener !=null){
               holder.layout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       listener.onClick(holder.layout,position);
                   }
               });
            }
        }
    }
    @Override
    public int getItemCount() {
        return projectList.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        ImageView cir;
        TextView fullName;
        TextView state;
        TextView orderMan;
        ItemViewHolder(View view){
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.linear_layout_rec);
            cir = (ImageView) view.findViewById(R.id.circle_image);
            fullName = (TextView) view.findViewById(R.id.full_project_name);
            state = (TextView) view.findViewById(R.id.state_name);
            orderMan = (TextView) view.findViewById(R.id.order_man);
        }
    }
    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void addDatas(ProjItem p,int position){
        projectList.add(p);
        notifyItemInserted(position);
    }
    public void removeDatas(ProjItem p,int position){
        projectList.remove(p);
        notifyItemRemoved(position);
    }
    public interface OnItemClickListener{
        void  onClick(View v, int position);
        void onOrderManClick(TextView textView, int position);
    }
}
