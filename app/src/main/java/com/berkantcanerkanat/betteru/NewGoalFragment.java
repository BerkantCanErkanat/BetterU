package com.berkantcanerkanat.betteru;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;


public class NewGoalFragment extends Fragment {
   TextView title,bDate,fDate,alert;
   CalendarView calendarView;
   ImageView speechButton;
   Button goBack;
   User user;
   SQLiteDatabase db;
   private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final int RECOGNIZER_RESULT = 1;
    public NewGoalFragment() {
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
        return inflater.inflate(R.layout.fragment_new_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.newgoaladView);//menu banner
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

        bDate = view.findViewById(R.id.bDate);
        fDate = view.findViewById(R.id.fDate);
        title = view.findViewById(R.id.titleTextView);
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setVisibility(View.INVISIBLE);
        bDate.setOnClickListener(this::onClick);
        fDate.setOnClickListener(this::onClick);
        goBack = view.findViewById(R.id.goBackButton);
        goBack.setOnClickListener(this::goBackOnClick);
        alert = view.findViewById(R.id.alertView);
        user = User.getInstance();
        speechButton = view.findViewById(R.id.imageView);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speech to text");
                startActivityForResult(speechIntent,RECOGNIZER_RESULT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){//evet verildi mi ve hangi olay icin (mikrofonu kullanma tarzı)
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            title.setText(matches.get(0));
        }
    }

    public void onClick(View view){
        goBack.setVisibility(View.INVISIBLE);
        calendarView.setVisibility(View.VISIBLE);

        if(view.getId() == R.id.bDate){
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                    String date = "";
                    i1++;
                    if(i2 < 10){
                        date += "0"+i2+"-";
                    }else{
                        date += i2+"-";
                    }
                    if(i1 < 10){
                        date += "0"+i1+"-";
                    }else{
                        date += i1+"-";
                    }
                    date += i;
                    bDate.setText(date);
                    calendarView.setVisibility(View.INVISIBLE);
                    goBack.setVisibility(View.VISIBLE);
                }
            });
        }else if(view.getId() == R.id.fDate){
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                    String date = "";
                    i1++;
                    if(i2 < 10){
                        date += "0"+i2+"-";
                    }else{
                        date += i2+"-";
                    }
                    if(i1 < 10){
                        date += "0"+i1+"-";
                    }else{
                        date += i1+"-";
                    }
                    date += i;
                    fDate.setText(date);
                    calendarView.setVisibility(View.INVISIBLE);
                    goBack.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void goBackOnClick(View view){
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if(!bDate.getText().toString().equals("") && !fDate.getText().toString().equals("") && !title.getText().toString().trim().equals("")){
            if(getDaysBetweenDates(bDate.getText().toString(),fDate.getText().toString()) > 0 && getDaysBetweenDates(today,fDate.getText().toString()) > 0 && getDaysBetweenDates(today,bDate.getText().toString()) >= 0){
                alert.setText("");
                String uuid = UUID.randomUUID().toString();
                Goals newGoal = new Goals(title.getText().toString(),bDate.getText().toString(),"false",uuid,fDate.getText().toString());
                user.goals_listuid.add(uuid);
                putDataIntoTable(newGoal);
                Animation buttonAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.menu_button_move);
                goBack.startAnimation(buttonAnimation);
                buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        NavDirections action = NewGoalFragmentDirections.actionNewGoalFragmentToGoalsFragment();
                        Navigation.findNavController(view).navigate(action);
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }else{
                alert.setText("check Date must be further than today and beginning day, beginning date must be today or further!!!");
            }
        }else{
            alert.setText("please choose the dates and don't leave title blank");
        }

    }
    public long getDaysBetweenDates(String madeDate, String checkDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(madeDate);
            endDate = dateFormat.parse(checkDate);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }
    private long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }
    public void putDataIntoTable(Goals goal){
        try{
            db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS goals (title VARCHAR, madedate VARCHAR, checkdate VARCHAR, ict VARCHAR, uid VARCHAR, dbd VARCHAR)");

            String sqlStatement = "INSERT INTO goals (title, madedate, checkdate, ict, uid, dbd) VALUES (?,?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(sqlStatement);
            sqLiteStatement.bindString(1,goal.title);
            sqLiteStatement.bindString(2,goal.madeDate);
            sqLiteStatement.bindString(3,goal.checkDate);
            sqLiteStatement.bindString(4,goal.isCameTrue);
            sqLiteStatement.bindString(5,goal.uid);
            sqLiteStatement.bindString(6,String.valueOf(goal.daysBetweenDates));
            sqLiteStatement.execute();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}