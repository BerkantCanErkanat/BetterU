package com.berkantcanerkanat.betteru;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class AchFragment extends Fragment {
    ListView listView;
    ArrayAdapter arrayAdapter;
    User user;
    TextView noAchText;
    private AdView mAdView;
    public AchFragment() {
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
        return inflater.inflate(R.layout.fragment_ach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.achlistadView);//menu banner
        AdRequest adRequestformenuban = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestformenuban);
        user = User.getInstance();
        listView = view.findViewById(R.id.listView);
        noAchText = view.findViewById(R.id.noAchView);
        if(user.permanentAch.size() > 0){
            AchListAdapter achListAdapter = new AchListAdapter(getActivity().getApplicationContext(),user.permanentAch);
            listView.setAdapter(achListAdapter);
            noAchText.setVisibility(View.INVISIBLE);
        }else{
            noAchText.setVisibility(View.VISIBLE);
        }

    }
}