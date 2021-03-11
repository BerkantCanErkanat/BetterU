package com.berkantcanerkanat.betteru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class AchListAdapter extends BaseAdapter {
   Context context;
   LayoutInflater layoutInflater;
   ArrayList<String> achs;

    public AchListAdapter(Context context, ArrayList<String> achs) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.achs = achs;
    }

    @Override
    public int getCount() {
        return achs.size();
    }

    @Override
    public Object getItem(int i) {
        return achs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View customView = layoutInflater.inflate(R.layout.ach_row,null);
        ImageView imageView = customView.findViewById(R.id.imageAchView);
        TextView title = customView.findViewById(R.id.titleAchView);
        TextView date = customView.findViewById(R.id.dateAchView);
        String [] split = achs.get(i).split("--");
        title.setText(split[0]);
        date.setText("Done date : "+split[1]);
        imageView.setVisibility(View.VISIBLE);
        return customView;
    }

}

