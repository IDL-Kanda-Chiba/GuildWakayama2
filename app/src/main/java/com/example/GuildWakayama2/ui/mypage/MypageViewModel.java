package com.example.GuildWakayama2.ui.mypage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.GuildWakayama2.GuildWakayama2Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MypageViewModel extends ViewModel {

    private final MutableLiveData<String> userName;
    private final MutableLiveData<Integer> point;
    private final MutableLiveData<Integer> ticket;
    private final MutableLiveData<String> password;
    private final MutableLiveData<String> email;

    private final SharedPreferences sharedPreferences;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public MypageViewModel() {
        userName = new MutableLiveData<>("jack");
        point = new MutableLiveData<>(0);
        ticket = new MutableLiveData<>(0);
        password = new MutableLiveData<>("1234");
        email = new MutableLiveData<>("a@gmail.com");

        // アプリケーションコンテキストを利用して SharedPreferences を初期化
        Context appContext = GuildWakayama2Application.getInstance().getApplicationContext();
        sharedPreferences = appContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        loadUserData();
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<Integer> getPoint() {
        return point;
    }

    public LiveData<Integer> getTicket() {
        return ticket;
    }

    public LiveData<String> getPassword() {
        return password;
    }
    public LiveData<String> getEmail(){return email;}

    public void increaseTicket() {
        int currentTicket = ticket.getValue() != null ? ticket.getValue() : 0;
        ticket.setValue(currentTicket + 1);
        saveUserData();
    }

    public void saveUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("username", userName.getValue());
        editor.putString("username", user.getDisplayName());
        editor.putInt("point", point.getValue() != null ? point.getValue() : 0);
        editor.putInt("ticket", ticket.getValue() != null ? ticket.getValue() : 0);
        editor.putString("password", password.getValue());
        //editor.putString("email",email.getValue());
        editor.putString("email", user.getEmail());
        editor.apply();
    }

    public void loadUserData() {
        //userName.setValue(sharedPreferences.getString("username", userName.getValue()));
        userName.setValue(sharedPreferences.getString("username", user.getDisplayName()));
        point.setValue(sharedPreferences.getInt("point", point.getValue() != null ? point.getValue() : 0));
        ticket.setValue(sharedPreferences.getInt("ticket", ticket.getValue() != null ? ticket.getValue() : 0));
        password.setValue(sharedPreferences.getString("password", password.getValue()));
        //email.setValue(sharedPreferences.getString("email", email.getValue()));
        email.setValue(sharedPreferences.getString("email", user.getEmail()));
    }
    public void setUserName(String newUserName) {
        userName.setValue(newUserName);
    }
    public void setEmail(String newEmail) {
        email.setValue(newEmail);
    }
    public void setPassword(String newPassword) {
        password.setValue(newPassword);
    }
}
