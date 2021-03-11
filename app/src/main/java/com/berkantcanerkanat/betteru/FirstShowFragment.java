package com.berkantcanerkanat.betteru;


import android.app.Dialog;

import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;


import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


//firstshow banner real ad id: ca-app-pub-2883276092494974/8905969763


// test banner ca-app-pub-3940256099942544/6300978111
// test interstitial ca-app-pub-3940256099942544/1033173712

public class FirstShowFragment extends Fragment {
    TextView welcome;
    Animation born,move,buttonAnim;
    SharedPreferences sharedPreferences;
    boolean firstUser;
    String firstUserName,firstUserAge;
    User user;
    SQLiteDatabase db;
    HashMap<String,String> checkDatekontrol = new HashMap<>();
    String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    ArrayList<String> achList = new ArrayList<>();// anlık olarak bunlar kaydedılır sonra sılınır.
    ArrayList<String> notDoneList = new ArrayList<>();
    ArrayList<String> doneList = new ArrayList<>();
    Dialog dialog;
    private AdView mAdView;
    public FirstShowFragment() {
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
        return inflater.inflate(R.layout.fragment_first_show, container, false);
    }
public void putOne(){// deneme icin
        try {
            db = getActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS reminders (title VARCHAR, content VARCHAR, time VARCHAR, uid VARCHAR)");
            db.execSQL("INSERT INTO reminders (title, content, time, uid) VALUES ('deneme', 'dogum gunu','5 saat','123asd')");
            //db.execSQL("INSERT INTO goals (title, madedate, checkdate, ict, uid, dbd) VALUES ('going to college', '22-12-2020','26-12-2020','true','asdas1312','5')");
        }catch (Exception e){
            e.printStackTrace();
        }

}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.adView);//firstschowbanner banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        user = User.getInstance();
        getSharedPref();
       if(user.firstTimeOpen){// bu fragment sadece ilk kez calıstıgında data cekıyoruz
           //putOne();
           getUidsFromDb();
           if(checkDatekontrol.size() > 0){// bugun kontrol edılecek goal var ise
               addToAchList();
               dbOperations();
               startTheConfettiAnim(view);
           }
           getAchs();
       }
                initilaizeVar(view);
                welcome.startAnimation(born);
                born.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
        welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstUser){
                    moveWithAnim(true,view);
                }else{
                    moveWithAnim(false,view);
                }
            }
        });

    }
    public void startTheConfettiAnim(View view){
        dialog = new Dialog(getActivity());
        buttonAnim = AnimationUtils.loadAnimation(getActivity(),R.anim.menu_button_move);
        showAlert();
    }
    public void showAlert(){
        if(achList.size() > 0){
             dialog.setContentView(R.layout.ach_layout_dialog);
             dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
             TextView dialogTitle = dialog.findViewById(R.id.dialogTitleView);
             dialogTitle.setText(achList.get(0));
             Button greatButton = dialog.findViewById(R.id.greatButtonView);
             greatButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     achList.remove(0);
                     dialog.cancel();
                     showAlert();
                 }
             });
            dialog.show();
        }
    }
    public void dbOperations(){//hedef kontrol bu
        try {
            db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
            if(achList.size()>0){
               for(String entry: achList){
                   db.execSQL("CREATE TABLE IF NOT EXISTS achievements (title VARCHAR,today VARCHAR)");
                   String sqlStatement = "INSERT INTO achievements (title,today) VALUES (?,?)";
                   SQLiteStatement sqLiteStatement = db.compileStatement(sqlStatement);
                   sqLiteStatement.bindString(1,entry);
                   sqLiteStatement.bindString(2,today);
                   sqLiteStatement.execute();
               }
            }
            if(doneList.size()>0){
                for(String entry: doneList){
                    db.delete("goals","uid = ?",new String[] {entry});
                }
            }
            if(notDoneList.size()>0){
                for(String entry: notDoneList){
                    db.delete("goals","uid = ?",new String[] {entry});
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void addToAchList(){
        db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
        try {
            for (Map.Entry<String,String> entry : checkDatekontrol.entrySet()){
                Cursor cursor = db.rawQuery("SELECT * FROM goals WHERE uid = ?",new String[]{entry.getKey()});
                int titleIx = cursor.getColumnIndex("title");
                while (cursor.moveToNext()){
                    if(entry.getValue().equals("true")){
                        achList.add(cursor.getString(titleIx));// anlık olarak basarı tablosuna kaydedılecek degerler
                        doneList.add(entry.getKey());
                    }else{
                        notDoneList.add(entry.getKey());
                    }
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void moveWithAnim(boolean firstUser,View view){
        welcome.startAnimation(move);
        move.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                if(firstUser){//first user
                    NavDirections action = FirstShowFragmentDirections.actionFirstShowFragmentToFirstUserFragment();
                    Navigation.findNavController(view).navigate(action);
                }else{// first user degil
                    Toast.makeText(getActivity().getApplicationContext(),"Welcome "+firstUserName,Toast.LENGTH_LONG).show();
                    NavDirections action = FirstShowFragmentDirections.actionFirstShowFragmentToMenuFragment2();
                    Navigation.findNavController(view).navigate(action);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    public void initilaizeVar(View view){
        welcome = view.findViewById(R.id.welcome);
        born = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.anim);
        move = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fragment_move);
        user.name = firstUserName;
        user.age = firstUserAge;
    }
    public void getSharedPref(){
        int age1;
        sharedPreferences = getActivity().getSharedPreferences("com.berkantcanerkanat.betteru", Context.MODE_PRIVATE);
        firstUser = sharedPreferences.getBoolean("firstUser",true);
        firstUserName = sharedPreferences.getString("firstUserName",null);
        firstUserAge = sharedPreferences.getString("age",null);
        user.firstUseDate = sharedPreferences.getString("firstuseDate",null);
        if(firstUserAge != null){
            age1 = Integer.parseInt(firstUserAge);
            firstUserAge = String.valueOf(age1).trim();
        }
        // BU MBS OLAAYLARINI SHARED ILE YAP
        user.bookNum = sharedPreferences.getInt("bookNum",0);
        user.movieNum = sharedPreferences.getInt("watchedMovieNum",0);
    }
    public void getUidsFromDb(){
        try {
            db = getActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
        }catch (Exception e){
            e.printStackTrace();
        }
       getUidsFromGoals();
       getUidsFromTodo();
       getUidsFromMovies();
       getUidsFromReminders();
    }
    public void getUidsFromGoals(){
        try {
            //Cursor cursor = db.rawQuery("SELECT * FROM goals",null);
            Cursor cursor = db.rawQuery( "select * from "+"goals"+" ORDER BY dbd ASC", null );
            //date icin artan azalan dıkkat et ve query bu sekılde mı ogren********************
            int uidIx = cursor.getColumnIndex("uid");
            int checkIx = cursor.getColumnIndex("checkdate");
            int ictIx = cursor.getColumnIndex("ict");
            //uid column ısmı koyarkende boyle koy
            while(cursor.moveToNext()){
                if(today.equals(cursor.getString(checkIx))){
                    checkDatekontrol.put(cursor.getString(uidIx),cursor.getString(ictIx));
                }else{
                    user.goals_listuid.add(cursor.getString(uidIx));
                }
            }
            cursor.close();
        }catch (Exception ee){
            ee.printStackTrace();
        }
        System.out.println(user.goals_listuid.size());
    }
    public void getUidsFromTodo(){
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM todo",null);
            int uidIx = cursor.getColumnIndex("uid");//uid column ısmı koyarkende boyle koy
            while(cursor.moveToNext()){
                user.todo_listuid.add(cursor.getString(uidIx));
            }
            cursor.close();
        }catch (Exception ee){
            ee.printStackTrace();
        }
    }
    public void getUidsFromMovies(){
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM movies",null);
            int uidIx = cursor.getColumnIndex("uid");//uid column ısmı koyarkende boyle koy
            while(cursor.moveToNext()){
                user.movies_listuid.add(cursor.getString(uidIx));
            }
            cursor.close();
        }catch (Exception ee){
            ee.printStackTrace();
        }
    }
    public void getUidsFromReminders(){
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM reminders",null);
            int uidIx = cursor.getColumnIndex("uid");//uid column ısmı koyarkende boyle koy
            while(cursor.moveToNext()){
                user.reminders_listuid.add(cursor.getString(uidIx));
            }
            cursor.close();
        }catch (Exception ee){
            ee.printStackTrace();
        }
    }
    public void getAchs(){
        try {
            db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
            Cursor cursor = db.rawQuery("SELECT * FROM achievements",null);
            int titleIx = cursor.getColumnIndex("title");
            int todayIx = cursor.getColumnIndex("today");
            while(cursor.moveToNext()){
                user.permanentAch.add(cursor.getString(titleIx)+"--"+cursor.getString(todayIx));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}