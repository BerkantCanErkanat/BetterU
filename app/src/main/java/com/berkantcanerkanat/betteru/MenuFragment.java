package com.berkantcanerkanat.betteru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.berkantcanerkanat.betteru.itemdecoration.ItemDecorrater;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// real menutorprofilinter ca-app-pub-2883276092494974/4412190987

// deneme test interstitial ca-app-pub-3940256099942544/1033173712

public class MenuFragment extends Fragment implements ReminderRecyclerViewAdapter.OnToDoListener {
    RecyclerView recyclerView;
    ArrayList<Reminder> reminderArrayList;
    FloatingActionButton floatingActionButton;
    TextView noReminderText,navAge,navDay;
    User user;
    SQLiteDatabase db;
    RecyclerView.Adapter adapter;
    //ama saga cekerekte acabılırız
    DrawerLayout drawerLayout;// action bar toggle ıcın gereklı
    NavigationView navigationView;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.menuadView);//menu banner
        AdRequest adRequestformenuban = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestformenuban);

        AdRequest adRequest = new AdRequest.Builder().build();//interstitial
        InterstitialAd.load(getActivity(),"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
        drawerLayout = view.findViewById(R.id.drawerLayout);
        navigationView = view.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.profileItem){
                    NavDirections action = MenuFragmentDirections.actionMenuFragmentToProfileFragment();
                    Navigation.findNavController(view).navigate(action);
                    closeDrawer();
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(getActivity());
                    }
                }else if(item.getItemId() == R.id.goalsItem){
                    NavDirections action = MenuFragmentDirections.actionMenuFragmentToGoalsFragment();
                    Navigation.findNavController(view).navigate(action);
                    closeDrawer();
                }else if(item.getItemId() == R.id.todolistItem){
                    NavDirections action = MenuFragmentDirections.actionMenuFragmentToToDoListFragment();
                    Navigation.findNavController(view).navigate(action);
                    closeDrawer();
                }else if(item.getItemId() == R.id.achievementsItem){
                    NavDirections action = MenuFragmentDirections.actionMenuFragmentToAchFragment();
                    Navigation.findNavController(view).navigate(action);
                    closeDrawer();
                }else if(item.getItemId() == R.id.SettingsItem){
                    NavDirections action = MenuFragmentDirections.actionMenuFragmentToFirstUserFragment();
                    Navigation.findNavController(view).navigate(action);
                    closeDrawer();
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(getActivity());
                    }
                }else if(item.getItemId() == R.id.SupportItem){
                    NavDirections action = MenuFragmentDirections.actionMenuFragmentToSupportFragment();
                    Navigation.findNavController(view).navigate(action);
                    closeDrawer();
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(getActivity());
                    }
                }
                return false;
            }
        });
        recyclerView = view.findViewById(R.id.reminderRecycleView);
        noReminderText = view.findViewById(R.id.reminderemptyView);
        reminderArrayList = new ArrayList<>();
        user = User.getInstance();
        getReminderDatas();
        ItemDecorrater itemDecorrater = new ItemDecorrater(2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.recyclerview_divider));

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(itemDecorrater);//item decorator her item arası bosluk bırakalım dıye
        recyclerView.addItemDecoration(dividerItemDecoration);

        //adapter
        adapter = new ReminderRecyclerViewAdapter(reminderArrayList,this);
        recyclerView.setAdapter(adapter);

        if(reminderArrayList.size() <= 0){
            noReminderText.setVisibility(View.VISIBLE);
        }else{
            noReminderText.setVisibility(View.INVISIBLE);
        }
        floatingActionButton = view.findViewById(R.id.reminderfloatingButtonView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = MenuFragmentDirections.actionMenuFragmentToNewReminderFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
        View headerLayout = navigationView.getHeaderView(0);
        navAge = headerLayout.findViewById(R.id.navheaderNameAge);
        navAge.setText(user.name+","+user.age);
        navDay = headerLayout.findViewById(R.id.navheaderDay);
        navDay.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

    }
    public void closeDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void getReminderDatas(){
        try {
            for(String uid: user.reminders_listuid){
                db = requireActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
                Cursor cursor = db.rawQuery("SELECT * FROM reminders WHERE uid = ?",new String[]{uid});
                int contentIx = cursor.getColumnIndex("content");
                int titleIx = cursor.getColumnIndex("title");
                int timeIx = cursor.getColumnIndex("time");
                int uidIx = cursor.getColumnIndex("uid");
                while (cursor.moveToNext()){
                   Reminder reminder = new Reminder(cursor.getString(contentIx),cursor.getString(timeIx),cursor.getString(uidIx),cursor.getString(titleIx));
                   reminderArrayList.add(reminder);
                }
                cursor.close();
            }
        }catch (Exception ee){
            ee.printStackTrace();
        }
    }

    @Override
    public void onLongtodoClick(int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("do you want to delete this watched movie?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //workmanager iptal edıyoruz
                    WorkManager.getInstance(getContext()).cancelAllWorkByTag(user.reminders_listuid.get(position));
                    db = requireActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
                    db.delete("reminders","uid = ?",new String[] {reminderArrayList.get(position).uid});
                    Toast.makeText(getContext(),"reminder cancelled",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                user.reminders_listuid.remove(reminderArrayList.get(position).uid);
                reminderArrayList.remove(position);
                adapter.notifyDataSetChanged();
                if(reminderArrayList.size() <= 0){
                    noReminderText.setVisibility(View.VISIBLE);
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