package com.berkantcanerkanat.betteru;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.itemdecoration.ItemDecorrater;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class goalsFragment extends Fragment implements MygoalsRecyclerViewAdapter.OnGoalListener {


    private ArrayList<Goals> myGoals;
    SQLiteDatabase db;
    User user;
    String[] uidArray;
    RecyclerView.Adapter adapter;
    TextView textView;
    RecyclerView recyclerView;
    private AdView mAdView;
    public goalsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.goalitemlistadView);//menu banner
        AdRequest adRequestformenuban = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestformenuban);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingButtonView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = goalsFragmentDirections.actionGoalsFragmentToNewGoalFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
        myGoals = new ArrayList<>();
        user = User.getInstance();
        if(user.updatedDatas.size() > 0) updateFirst();
        getDatasFromGoalsTable();
        ItemDecorrater itemDecorrater = new ItemDecorrater(2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.recyclerview_divider));
        // Set the adapter
        Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(itemDecorrater);//item decorator her item arası bosluk bırakalım dıye
            recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MygoalsRecyclerViewAdapter(myGoals,this);
        recyclerView.setAdapter(adapter);
        textView = view.findViewById(R.id.listemptyView);
        if(user.goals_listuid.size() <= 0){
            textView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(View.INVISIBLE);
        }
    }
    public void updateFirst(){
        //title, madedate, checkdate, ict, uid, dbd
        //myDB.update(TableName, cv, "_id = ?", new String[]{id});
        try {
            for (Map.Entry<String,Goals> entry : user.updatedDatas.entrySet()){
                db = requireActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
                ContentValues cv = new ContentValues();
                cv.put("title",entry.getValue().title);
                cv.put("madedate",entry.getValue().madeDate);
                cv.put("checkdate",entry.getValue().checkDate);
                cv.put("ict",entry.getValue().isCameTrue);
                cv.put("uid",entry.getValue().uid);
                cv.put("dbd",entry.getValue().daysBetweenDates);
                db.update("goals",cv,"uid = ?",new String[]{entry.getKey()});
                System.out.println("guncellendı");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            user.updatedDatas.clear();
        }
    }
    public void getDatasFromGoalsTable(){
        uidArray = user.goals_listuid.toArray(new String[0]);
        try {
            for(String uid: uidArray){
                db = requireActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
                Cursor cursor = db.rawQuery("SELECT * FROM goals WHERE uid = ?",new String[]{uid});
                int titleIx = cursor.getColumnIndex("title");
                int madeDateIx = cursor.getColumnIndex("madedate");
                int checkDateIx = cursor.getColumnIndex("checkdate");
                int isCameTrueIx = cursor.getColumnIndex("ict");
                int uidIx = cursor.getColumnIndex("uid");
                while (cursor.moveToNext()){
                    Goals goal = new Goals(cursor.getString(titleIx),cursor.getString(madeDateIx),cursor.getString(isCameTrueIx),cursor.getString(uidIx),cursor.getString(checkDateIx));
                    myGoals.add(goal);
                }
                cursor.close();
            }

        }catch (Exception ee){
            ee.printStackTrace();
        }
    }

    @Override// tıklandıgında click
    public void onGoalClick(int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("do you want to delete this goal?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myGoals.remove(position);
                try {
                    db = requireActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
                    db.delete("goals","uid = ?",new String[] {user.goals_listuid.get(position)});
                }catch (Exception e){
                    e.printStackTrace();
                }
                user.goals_listuid.remove(position);
                adapter.notifyDataSetChanged();
                if(user.goals_listuid.size() <= 0){
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        alert.show();
    }
}