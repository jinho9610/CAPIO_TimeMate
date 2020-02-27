package com.example.timemate;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.chrono.MinguoChronology;
import java.util.ArrayList;

public class OneDayAdapater extends RecyclerView.Adapter<OneDayAdapater.OneDayViewHolder> {

    private ArrayList<ODData> arrayList;
    private Context context;

    public OneDayAdapater(ArrayList<ODData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public OneDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_day, parent, false);
        OneDayViewHolder holder = new OneDayViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OneDayViewHolder holder, int position) {
        if (arrayList.get(position).getIconType() == 1) {
            holder.iv_icon.setImageResource(R.drawable.glasses2);
            holder.tv_activity.setText("업무");
        } else if (arrayList.get(position).getIconType() == 2) {
            holder.iv_icon.setImageResource(R.drawable.dumbbell2);
            holder.tv_activity.setText("운동");
        } else {
            holder.iv_icon.setImageResource(R.drawable.book2);
            holder.tv_activity.setText("공부");
        }

        long mTime = arrayList.get(position).getTime();
        String tempTime = String.format("%02dHR %02dMIN", mTime/1000/60/60,mTime/1000/60);
        holder.tv_time_Days.setText(tempTime);
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class OneDayViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_activity, tv_time_Days;

        public OneDayViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_icon = itemView.findViewById(R.id.iv_icon);
            this.tv_activity = itemView.findViewById(R.id.tv_activity);
            this.tv_time_Days = itemView.findViewById(R.id.tv_time_Days);
        }
    }
}
