package com.example.hoyo1.whereis.Recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoyo1.whereis.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private ArrayList<RecyclerItem> items=new ArrayList<RecyclerItem>();


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private TextView date;

        ViewHolder(View itemView) {
            super(itemView) ;

            content=itemView.findViewById(R.id.content);
            date=itemView.findViewById(R.id.date);
        }

        void onBind(RecyclerItem item){
            content.setText(item.getContent());
            date.setText(item.getDate());

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(RecyclerItem item){
        items.add(item);
    }

    public void clear(){
        items.clear();
    }

}
