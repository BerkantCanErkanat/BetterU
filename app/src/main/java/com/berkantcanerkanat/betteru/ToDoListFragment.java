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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.itemdecoration.ItemDecorrater;
import com.berkantcanerkanat.betteru.singleton.User;
import com.berkantcanerkanat.betteru.todoandnotes.Todo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class ToDoListFragment extends Fragment implements TodListRecyclerViewAdapter.OnToDoListener{
    ArrayList<Todo> todoArrayList;
    User user;
    RecyclerView.Adapter adapter;
    TextView noToShowTextView;
    SQLiteDatabase db;
    RecyclerView recyclerView;
    private AdView mAdView;
    public ToDoListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_to_do_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mAdView = view.findViewById(R.id.todolistadView);//menu banner
        AdRequest adRequestformenuban = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestformenuban);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.toDoListfloatingButtonView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.yeniBirTodo = true;
                NavDirections action = ToDoListFragmentDirections.actionToDoListFragmentToNewTodo();
                Navigation.findNavController(view).navigate(action);
            }
        });
        todoArrayList = new ArrayList<>();
        user = User.getInstance();
        getDatasFromTabletodo();// dataları cektik

        ItemDecorrater itemDecorrater = new ItemDecorrater(2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.recyclerview_divider));

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.toDoListRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(itemDecorrater);//item decorator her item arası bosluk bırakalım dıye
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new TodListRecyclerViewAdapter(todoArrayList,this);
        recyclerView.setAdapter(adapter);

        noToShowTextView = view.findViewById(R.id.toDolistemptyView);
        if(user.todo_listuid.size() <= 0){
            noToShowTextView.setVisibility(View.VISIBLE);
        }else{
            noToShowTextView.setVisibility(View.INVISIBLE);
        }
    }
    public void getDatasFromTabletodo(){
        try {
            for(String uid: user.todo_listuid){
                db = requireActivity().openOrCreateDatabase("BetterU", Context.MODE_PRIVATE,null);
                Cursor cursor = db.rawQuery("SELECT * FROM todo WHERE uid = ?",new String[]{uid});
                int titleIx = cursor.getColumnIndex("title");
                int dateIx = cursor.getColumnIndex("date");
                int contentIx = cursor.getColumnIndex("content");
                int uidIx = cursor.getColumnIndex("uid");
                int tfcontentIx = cursor.getColumnIndex("tfcontent");
                while (cursor.moveToNext()){
                  Todo todo = new Todo(cursor.getString(titleIx),cursor.getString(contentIx),cursor.getString(dateIx),cursor.getString(uidIx),cursor.getString(tfcontentIx));
                    todoArrayList.add(todo);
                }
                cursor.close();
            }

        }catch (Exception ee){
            ee.printStackTrace();
        }
    }

    @Override
    public void ontodoClick(int position,View view) {
        user.yeniBirTodo = false;
        user.instant_todo = todoArrayList.get(position);// bu hangi todo ile calıscaz onu gosterecek gectigimiz fragmantta
        NavDirections action = ToDoListFragmentDirections.actionToDoListFragmentToNewTodo();
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onLongtodoClick(int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("do you want to delete this to do list?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    db = requireActivity().openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
                    db.delete("todo","uid = ?",new String[] {todoArrayList.get(position).uid});
                }catch (Exception e){
                    e.printStackTrace();
                }
                user.todo_listuid.remove(todoArrayList.get(position).uid);
                todoArrayList.remove(position);
                adapter.notifyDataSetChanged();
                if(user.todo_listuid.size() <= 0){
                    noToShowTextView.setVisibility(View.VISIBLE);
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