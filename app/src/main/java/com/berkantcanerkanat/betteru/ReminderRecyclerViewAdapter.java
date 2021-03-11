package com.berkantcanerkanat.betteru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReminderRecyclerViewAdapter extends RecyclerView.Adapter<ReminderRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<Reminder> reminderArrayList;
    private OnToDoListener onToDoListener;

    public ReminderRecyclerViewAdapter(ArrayList<Reminder> reminderArrayList, OnToDoListener onToDoListener) {
        this.reminderArrayList = reminderArrayList;
        this.onToDoListener = onToDoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ViewHolder(view,onToDoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(reminderArrayList.get(position).title);
        holder.content.setText(reminderArrayList.get(position).content);
        holder.time.setText(reminderArrayList.get(position).time);
    }


    @Override
    public int getItemCount() {
        return reminderArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView title,content,time;
        OnToDoListener onToDoListener;
        public ViewHolder(@NonNull View itemView, OnToDoListener onToDoListener) {
            super(itemView);
            this.onToDoListener = onToDoListener;
            itemView.setOnLongClickListener(this);
            title = itemView.findViewById(R.id.rtitleTextView);
            content = itemView.findViewById(R.id.rcontentTextView);
            time = itemView.findViewById(R.id.rtimeTextView);
        }

        @Override
        public boolean onLongClick(View view) {
            onToDoListener.onLongtodoClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnToDoListener{
        void onLongtodoClick(int position);
    }
}
