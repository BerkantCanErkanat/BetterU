package com.berkantcanerkanat.betteru;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.berkantcanerkanat.betteru.R;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ProfileFragment extends Fragment {
    TextView nameAge,ach,goalsW,nickName,day;
    User user;
    SharedPreferences sharedPreferences;
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private AdView mAdView;
    public ProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.profileadView);//menu banner
        AdRequest adRequestformenuban = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestformenuban);
        initializeVar(view);
        setValues();
    }
    public void initializeVar(View view){
        day = view.findViewById(R.id.dateText);
        nameAge = view.findViewById(R.id.nameAgeText);
        ach = view.findViewById(R.id.achText);
        goalsW = view.findViewById(R.id.goalsWText);
        nickName = view.findViewById(R.id.nickNameText);
        sharedPreferences = getActivity().getSharedPreferences("com.berkantcanerkanat.betteru", Context.MODE_PRIVATE);
        user = User.getInstance();
    }
    public void setValues(){
        nameAge.setText(user.name+","+user.age);
        ach.setText(user.permanentAch.size()+" goals achieved");
        goalsW.setText(user.goals_listuid.size()+" goals waiting");
        nickName.setText(findLevel());
        day.setText("Day "+getDaysBetweenDates(user.firstUseDate,new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())));
    }
    public String findLevel(){
        if(user.permanentAch.size() < 3){
            return "Newbie";
        }else if(user.permanentAch.size() < 6){
            return "Novice";
        }else if(user.permanentAch.size() < 9){
            return "Rookie";
        }else if(user.permanentAch.size() < 12){
            return "Talented";
        }else if(user.permanentAch.size() < 15){
            return "Proficient";
        }else if(user.permanentAch.size() < 20){
            return "Advanced";
        }else if(user.permanentAch.size() < 30){
            return "Senior";
        }else{
            return "Expert";
        }
    }
    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }
    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }
}