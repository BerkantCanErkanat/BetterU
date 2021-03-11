package com.berkantcanerkanat.betteru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berkantcanerkanat.betteru.todoandnotes.Todo;

import java.util.ArrayList;

public class TodListRecyclerViewAdapter extends RecyclerView.Adapter<TodListRecyclerViewAdapter.ViewHolder>{

    private final ArrayList<Todo> todoList;
    private OnToDoListener onToDoListener;
    public TodListRecyclerViewAdapter(ArrayList<Todo> todoList,OnToDoListener onToDoListener) {
        this.todoList = todoList;
        this.onToDoListener = onToDoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todolist_item, parent, false);// bir row baglantısı
        return new ViewHolder(view,onToDoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // input olmasasa da 1tane goruyor ona dikkat
     holder.title.setText(todoList.get(position).title);
     String [] items = todoList.get(position).content.split(",");
     String [] tfItems = todoList.get(position).tfContent.split(",");

     if(items.length >= 2){
         holder.item1.setText(items[0]);
         holder.item2.setText(items[1]);
         if(tfItems[0].equals("true")){
             holder.imageView.setImageResource(R.drawable.true_star);
         }else{
             holder.imageView.setImageResource(R.drawable.item_image);
         }
         if(tfItems[1].equals("true")){
             holder.imageView2.setImageResource(R.drawable.true_star);
         }else{
             holder.imageView2.setImageResource(R.drawable.item_image);
         }

     }else if(items.length == 1 && !items[0].equals("")){
         holder.item1.setText(items[0]);
         if(tfItems[0].equals("true")){
             holder.imageView.setImageResource(R.drawable.true_star);
         }else{
             holder.imageView.setImageResource(R.drawable.item_image);
         }
         holder.imageView2.setVisibility(View.INVISIBLE);
     }else{
         holder.imageView.setVisibility(View.INVISIBLE);
         holder.imageView2.setVisibility(View.INVISIBLE);
     }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView title,item1,item2;
        ImageView imageView,imageView2;
        OnToDoListener onToDoListener;
        public ViewHolder(@NonNull View itemView,OnToDoListener onToDoListener) {
            super(itemView);
            title = itemView.findViewById(R.id.todoTitleView);
            item1 = itemView.findViewById(R.id.todoItem1View);
            item2 = itemView.findViewById(R.id.todoItem2View);
            imageView = itemView.findViewById(R.id.firstImage_field);
            imageView2 = itemView.findViewById(R.id.secondImage);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.onToDoListener = onToDoListener;
        }

        @Override
        public void onClick(View view) {
            onToDoListener.ontodoClick(getAdapterPosition(),view);
        }

        @Override
        public boolean onLongClick(View view) {
            onToDoListener.onLongtodoClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnToDoListener{
        void ontodoClick(int position,View view);
        void onLongtodoClick(int position);
    }
}
