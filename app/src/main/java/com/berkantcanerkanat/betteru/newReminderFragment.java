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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;


public class newReminderFragment extends Fragment {
    TextView alert;
    EditText title,content,time;
    ImageView titleI,contentI,timeI;
    Button save;
    User user;
    private static final int title_code = 1;//title
    private static final int content_code = 2;//title
    private static final int time_code = 3;//title
    private String [] zaman_array = {"min","mins","minute","minutes","sec","secs","second","seconds","hour","hours"};
    int sure;// 5
    int zaman_index = 0;// 2. index gibi
    String secMinHour = "";
    SQLiteDatabase db;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    public newReminderFragment() {
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
        return inflater.inflate(R.layout.fragment_new_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.newreminderadView);//menu banner
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


        alert = view.findViewById(R.id.alertTextView);
        title = view.findViewById(R.id.newTitleTextView);
        time = view.findViewById(R.id.newTimeTextView);//acmaları yap
        content = view.findViewById(R.id.contentTextView);
        titleI = view.findViewById(R.id.titleAudioView);
        titleI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speech to text");
                startActivityForResult(speechIntent,title_code);
            }
        });
        contentI = view.findViewById(R.id.contentAudioView);
        contentI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speech to text");
                startActivityForResult(speechIntent,content_code);
            }
        });
        timeI = view.findViewById(R.id.timeAudioView);
        timeI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speech to text");
                startActivityForResult(speechIntent,time_code);
            }
        });
        save = view.findViewById(R.id.saveButton);
        user = User.getInstance();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!content.getText().toString().trim().equals("") && !title.getText().toString().trim().equals("") && checkForTime(time.getText().toString())){
                    String uuid = UUID.randomUUID().toString();
                    String hesaplanan_zaman = zaman_hesapla();
                    Reminder reminder = new Reminder(content.getText().toString(),hesaplanan_zaman,uuid,title.getText().toString());
                    user.reminders_listuid.add(uuid);
                    putDataIntoTable(reminder);
                    startWorkManager(uuid,reminder.content,reminder.title);
                    Animation buttonAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.menu_button_move);
                    save.startAnimation(buttonAnimation);
                    buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Toast.makeText(getContext(),"saved",Toast.LENGTH_SHORT).show();
                            NavDirections action = newReminderFragmentDirections.actionNewReminderFragmentToMenuFragment();
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
                    alert.setText("Sample:\nBirthdayParty\nat Berkant's House\n3 hours");
                }
            }
        });

    }
    public void startWorkManager(String uuid,String content,String title){
        TimeUnit timeUnit;

        switch (secMinHour) {
            case "second":
                timeUnit = TimeUnit.SECONDS;
                break;
            case "minute":
                timeUnit = TimeUnit.MINUTES;
                break;
            case "hour":
                timeUnit = TimeUnit.HOURS;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + secMinHour);
        }
        Data data = new Data.Builder().putString(uuid,content+"-"+title).build();
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderWorkManager.class)// Worker sınıfını parametre olarak ver
                .setInputData(data)// datayı yukarda ayarlamıstık
                .setInitialDelay(sure,timeUnit)// 5 saat sonra yap dedık gorevi
                .addTag(uuid)// birden fazla work request varsa ayrıstırmak ıcın
                .build();

        WorkManager.getInstance(getContext()).enqueue(workRequest);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == title_code && resultCode == RESULT_OK){//evet verildi mi ve hangi olay icin (mikrofonu kullanma tarzı)
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            title.setText(matches.get(0));
        }else if(requestCode == content_code && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            content.setText(matches.get(0));
        }else if(requestCode == time_code && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            time.setText(matches.get(0));
        }
    }
    public boolean checkForTime(String speech){
        String[] icerik = speech.split(" ");
        boolean ilk_kelime_numerik = isNumeric(icerik[0]);
        if(!ilk_kelime_numerik){// ilk girdi numeric olmalı 5 hours gibi
            return false;
        }
        zaman_index = ikinci_kelime_kontrol(icerik[1]);
        if(zaman_index == -1){//hours minutes seconds gibi bir sey gırılmemıs ise
            return false;
        }else{
            sure = Integer.parseInt(icerik[0]);
        }
        return true;
    }
    //hem sayıyı hem de zamanı elınde tut yolla sonra onu workmanager ile kullancaz
    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public int ikinci_kelime_kontrol(String str){
        str = str.toLowerCase();
        int sira = hangisine_esit(str);
        if(sira > -1){
            return sira;
        }else{
            return -1;
        }
    }
    public int hangisine_esit(String str){
        for(int i = 0;i<zaman_array.length;i++){
            if(str.equals(zaman_array[i])){
                return i;
            }
        }
        return -1;
    }
    public String zaman_hesapla(){
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date d = null;
        try {
            d = df.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        if(zaman_index == 4 || zaman_index == 5 || zaman_index == 6 || zaman_index == 7){//seconds
            secMinHour = "second";
            cal.add(Calendar.SECOND,sure);
            String newTime = df.format(cal.getTime());
            System.out.println("New Time : " + newTime);
            return newTime;
        }else if(zaman_index == 0 || zaman_index == 1 || zaman_index == 2 || zaman_index == 3){//minutes
            secMinHour = "minute";
            cal.add(Calendar.MINUTE,sure);
            String newTime = df.format(cal.getTime());
            System.out.println("New Time : " + newTime);
            return newTime;
        }else if(zaman_index == 8 || zaman_index == 9){
            secMinHour = "hour";
            cal.add(Calendar.HOUR,sure);
            String newTime = df.format(cal.getTime());
            System.out.println("New Time : " + newTime);
            return newTime;
        }
        return null;
    }
    public void putDataIntoTable(Reminder reminder){
        try{
            db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS reminders (title VARCHAR, content VARCHAR, time VARCHAR, uid VARCHAR)");
            String sqlStatement = "INSERT INTO reminders (title, content, time, uid) VALUES (?,?,?,?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(sqlStatement);
            sqLiteStatement.bindString(1,reminder.title);
            sqLiteStatement.bindString(2,reminder.content);
            sqLiteStatement.bindString(3,reminder.time);
            sqLiteStatement.bindString(4,reminder.uid);
            sqLiteStatement.execute();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}