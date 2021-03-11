package com.berkantcanerkanat.betteru;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Map;

import static com.berkantcanerkanat.betteru.R.layout.fragment_menu;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = User.getInstance();
    }

    @Override
    protected void onPause() {// orta tus veya son fragmentta geri tusa basma
        super.onPause();
        user.firstTimeOpen = false;
        if(user.updatedDatas.size()>0)//goal icin update olayı kontrol edıyor
        updateTheData();
    }

    @Override
    protected void onStop() {//kareye basılma
        super.onStop();
        if(user.updatedDatas.size()>0)
        updateTheData();
    }
    public void updateTheData(){
        db = this.openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
        try {
            for (Map.Entry<String, Goals> entry : user.updatedDatas.entrySet()){
                db = this.openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
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

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
         if(navHostFragment.getChildFragmentManager().getFragments().get(0).getClass().getSimpleName().equals("MenuFragment")){
             DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
             if(drawerLayout.isOpen()){
                 drawerLayout.close();
             }else{
                 super.onBackPressed();
             }
         }else{
             super.onBackPressed();
         }
        if(user.updatedDatas.size()>0){
            updateTheData();
        }
    }
}