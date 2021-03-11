package com.berkantcanerkanat.betteru.singleton;

import com.berkantcanerkanat.betteru.goals.Goals;
import com.berkantcanerkanat.betteru.todoandnotes.Todo;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    public String name;
    public String age;
    public int bookNum = 0;
    public int seriesNum = 0;
    public int movieNum = 0;
    public ArrayList<String> goals_listuid = new ArrayList<>();
    public ArrayList<String> movies_listuid = new ArrayList<>();
    public ArrayList<String> todo_listuid = new ArrayList<>();
    public HashMap<String, Goals> updatedDatas = new HashMap<>();
    public ArrayList<String> permanentAch = new ArrayList<>();
    public ArrayList<String> reminders_listuid = new ArrayList<>();
    public boolean firstTimeOpen = true;//ilk ekranın bir kere calısması ıcın ??
    public boolean yeniBirTodo = true;// eger yeni bir todo eklencek ise true degılse false
    public Todo instant_todo;// anlık eklenen to do burada tutuluyor
    public String firstUseDate;
    private static User user;
    private User(){

    }
    public static User getInstance(){
        if(user == null){
               user = new User();
               return user;
        }
        return user;
    }
}
