package com.berkantcanerkanat.betteru.todoandnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.berkantcanerkanat.betteru.R;
import com.berkantcanerkanat.betteru.ToDoListFragmentDirections;
import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.singleton.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


public class newTodo extends Fragment {
    User user;
    SQLiteDatabase db;
    Button addNew,save;
    EditText title;
    LinearLayout ictekiLayout;
    ArrayList<EditText> items;
    Todo newTodo;
    String newContent = "";// yeni koyulan todo nun icerigi
    String tfNewContent = "";// true false contenti
    LinkedHashMap<ImageView,Boolean> true_or_not;
    boolean done = false;
    private InterstitialAd mInterstitialAd;
    public newTodo() {
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
        return inflater.inflate(R.layout.fragment_new_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

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

        user = User.getInstance();
        addNew = view.findViewById(R.id.addNewButton);
        save = view.findViewById(R.id.saveButton);
        title = view.findViewById(R.id.titleView);
        ictekiLayout = view.findViewById(R.id.ictekilayout);
        items = new ArrayList<>();
        true_or_not = new LinkedHashMap<>();
        if(!user.yeniBirTodo){
            // eger var olanı acıyor isek
          String [] content = user.instant_todo.content.split(",");
          String [] tf_content = user.instant_todo.tfContent.split(",");
          title.setText(user.instant_todo.title);
          for(int i = 0;i< content.length;i++){
              View myView = getLayoutInflater().inflate(R.layout.new_todo_item_field,null,false);
              EditText editText = myView.findViewById(R.id.itemView);
              editText.setText(content[i]);
              ImageView imageView = myView.findViewById(R.id.firstImage_field);
              if(tf_content[i].equals("true")){
                  imageView.setImageResource(R.drawable.true_star);
                  true_or_not.put(imageView,true);
              }else{
                  imageView.setImageResource(R.drawable.item_image);
                  true_or_not.put(imageView,false);
              }
              items.add(editText);
              imageView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if(!done){
                          imageView.setImageResource(R.drawable.true_star);
                          done = true;
                          true_or_not.put(imageView,true);
                      }else{
                          imageView.setImageResource(R.drawable.item_image);
                          done = false;
                          true_or_not.put(imageView,false);
                      }
                  }
              });
              ictekiLayout.addView(myView,ictekiLayout.getChildCount()-2);
          }
        }
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               View myView = getLayoutInflater().inflate(R.layout.new_todo_item_field,null,false);
                EditText editText = myView.findViewById(R.id.itemView);
                ImageView imageView = myView.findViewById(R.id.firstImage_field);
                true_or_not.put(imageView,false);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!done){
                            imageView.setImageResource(R.drawable.true_star);
                            done = true;
                            true_or_not.put(imageView,true);
                        }else{
                            imageView.setImageResource(R.drawable.item_image);
                            done = false;
                            true_or_not.put(imageView,false);
                        }
                    }
                });
                items.add(editText);// tum itemler bunda
                ictekiLayout.addView(myView,ictekiLayout.getChildCount()-2);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.yeniBirTodo){
                    if(checkForFault()){// edittextler bos mu dolu mu kontrol et
                        try{
                            String uniqueId = UUID.randomUUID().toString();
                            user.todo_listuid.add(uniqueId);
                            getTrueOrNotContent();//eger image a tıklanmıs ıse true yapılmıs gibi
                            System.out.println(tfNewContent);
                            newTodo = new Todo(title.getText().toString().trim(),newContent,"noDate",uniqueId,tfNewContent);
                            db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
                            db.execSQL("CREATE TABLE IF NOT EXISTS todo (title VARCHAR, date VARCHAR, content VARCHAR, uid VARCHAR, tfcontent VARCHAR)");
                            String sqlStatement = "INSERT INTO todo (title, date, content, uid,tfcontent) VALUES (?,?,?,?,?)";
                            SQLiteStatement sqLiteStatement = db.compileStatement(sqlStatement);
                            sqLiteStatement.bindString(1,newTodo.title);
                            sqLiteStatement.bindString(2,newTodo.date);
                            sqLiteStatement.bindString(3,newTodo.content);
                            sqLiteStatement.bindString(4,newTodo.uid);
                            sqLiteStatement.bindString(5,newTodo.tfContent);
                            sqLiteStatement.execute();
                            NavDirections action = newTodoDirections.actionNewTodoToToDoListFragment();
                            Navigation.findNavController(view).navigate(action);
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(getActivity());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getActivity(),"do not leave any input blank!",Toast.LENGTH_LONG).show();
                    }
                }else{//var olan bir to do ya guncelleme islemi
                        if(checkForFault()){
                            try {
                                getTrueOrNotContent();//eger image a tıklanmıs ıse true yapılmıs gibi
                                newTodo = new Todo(title.getText().toString().trim(),newContent,"noDate",user.instant_todo.uid,tfNewContent);
                                db = getActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
                                ContentValues cv = new ContentValues();
                                cv.put("title",newTodo.title);
                                cv.put("date",newTodo.date);
                                cv.put("content",newTodo.content);
                                cv.put("uid",newTodo.uid);
                                cv.put("tfcontent",newTodo.tfContent);
                                db.update("todo",cv,"uid = ?",new String[]{newTodo.uid});
                                NavDirections action = newTodoDirections.actionNewTodoToToDoListFragment();
                                Navigation.findNavController(view).navigate(action);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                }
            }
        });
    }
    public boolean checkForFault(){
        //title kontrolu
        if(!title.getText().toString().trim().equals("")){
              for(EditText text: items){
                  if(!text.getText().toString().trim().equals("")){
                      newContent += text.getText().toString().trim()+",";
                  }else{
                      return false;
                  }
              }
              return true;
        }else{
            return false;
        }
    }
    public void getTrueOrNotContent(){
        for (Map.Entry<ImageView,Boolean> entry : true_or_not.entrySet()){
          if(entry.getValue()){
              tfNewContent += "true"+",";
          }else{
              tfNewContent += "false"+",";
          }
        }
    }
}