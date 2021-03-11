package com.berkantcanerkanat.betteru;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.singleton.User;

import java.util.HashMap;
import java.util.List;


public class MygoalsRecyclerViewAdapter extends RecyclerView.Adapter<MygoalsRecyclerViewAdapter.ViewHolder> {

    private final List<Goals> myGoals;
    User user = User.getInstance();
    Goals goal;
    private OnGoalListener mOnGoalListener;


    public MygoalsRecyclerViewAdapter(List<Goals> items,OnGoalListener mOnGoalListener){
        myGoals = items;
        this.mOnGoalListener = mOnGoalListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view,mOnGoalListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(myGoals.get(position).title);
        if(myGoals.get(position).isCameTrue.equals("true")){
            System.out.println("girdi");
            holder.isDone.setChecked(true);
        }else{
            System.out.println("iscame hala false");
        }
        holder.isDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                if(checked){
                    myGoals.get(position).isCameTrue = "true";
                    goal = new Goals(myGoals.get(position).title,myGoals.get(position).madeDate,myGoals.get(position).isCameTrue,myGoals.get(position).uid,myGoals.get(position).checkDate);
                    user.updatedDatas.put(myGoals.get(position).uid,goal);
                }else{
                    myGoals.get(position).isCameTrue = "false";
                    goal = new Goals(myGoals.get(position).title,myGoals.get(position).madeDate,myGoals.get(position).isCameTrue,myGoals.get(position).uid,myGoals.get(position).checkDate);
                    user.updatedDatas.put(myGoals.get(position).uid,goal);
                }
            }
        });
       holder.dates.setText(myGoals.get(position).madeDate+"-"+myGoals.get(position).checkDate);
       holder.itemView.setBackgroundColor(Color.parseColor("#EBF6F8"));
    }


    @Override
    public int getItemCount() {
        return myGoals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public final TextView title;
        public final TextView dates;
        public CheckBox isDone;
        OnGoalListener onGoalListener;

        public ViewHolder(View view,OnGoalListener onGoalListener){
            super(view);
            title = view.findViewById(R.id.titleView);
            dates = view.findViewById(R.id.dateView);
            isDone = view.findViewById(R.id.isDoneView);
            view.setOnLongClickListener(this);
            this.onGoalListener = onGoalListener;
        }

        @Override
        public boolean onLongClick(View view) {
            onGoalListener.onGoalClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnGoalListener{
        void onGoalClick(int position);
    }
}