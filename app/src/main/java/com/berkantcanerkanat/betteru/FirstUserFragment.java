package com.berkantcanerkanat.betteru;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.berkantcanerkanat.betteru.FirstUserFragmentDirections;
import com.berkantcanerkanat.betteru.R;
import com.berkantcanerkanat.betteru.FirstUserFragmentDirections;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FirstUserFragment extends Fragment {
    boolean nameOk = false,ageOk = false;
    SharedPreferences sharedPreferences;
    int yas = 0;
    Animation buttonAnimation;
    User user;
    private AdView mAdView;
    public FirstUserFragment() {
        // Required empty public constructor

    }

//class user classı ve uygulamaya her gırıldıgınde doldur

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.firstuseradView);//menu banner
        AdRequest adRequestformenuban = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestformenuban);
        user = User.getInstance();
        buttonAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.menu_button_move);
        TextView nameAlert = view.findViewById(R.id.nameAlertText);
        TextView ageAlert = view.findViewById(R.id.ageAlertText);
        EditText nameText = view.findViewById(R.id.nameText);
        EditText ageText = view.findViewById(R.id.yasText);
        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButton.startAnimation(buttonAnimation);
                String name = nameText.getText().toString().trim();
                String yasString = ageText.getText().toString().trim();
                if(!yasString.equals("")) yas = Integer.parseInt(yasString);
                System.out.println(yas);
                if(!name.equals("")){
                    nameOk = true;
                    nameAlert.setText("");
                }else{
                    nameAlert.setText("please input your name!");
                    nameOk = false;
                }
                if(yas > 0){
                    ageOk = true;
                    ageAlert.setText("");
                }else{
                    ageAlert.setText("please input your age!");
                    ageOk = false;
                }
                if(nameOk && ageOk){
                 sharedPreferences = getActivity().getSharedPreferences("com.berkantcanerkanat.betteru", Context.MODE_PRIVATE);
                 sharedPreferences.edit().putBoolean("firstUser",false).apply();
                 sharedPreferences.edit().putString("firstUserName",name).apply();
                 sharedPreferences.edit().putString("age",yasString).apply();
                 String firstUseDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                 sharedPreferences.edit().putString("firstuseDate",firstUseDate).apply();
                 user.name = name;
                 user.age = yasString;
                 user.firstUseDate = firstUseDate;
                    Animation buttonAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.menu_button_move);
                    startButton.startAnimation(buttonAnimation);
                    buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Toast.makeText(getActivity().getApplicationContext(),"Welcome "+name,Toast.LENGTH_LONG).show();
                            NavDirections action = FirstUserFragmentDirections.actionFirstUserFragmentToMenuFragment();
                            Navigation.findNavController(view).navigate(action);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            }
        });
    }

}