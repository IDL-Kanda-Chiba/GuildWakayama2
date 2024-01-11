package com.example.GuildWakayama2.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import java.util.Arrays;
import android.content.Context;
import android.content.SharedPreferences;

public class NotificationsViewModel extends ViewModel {

    // ユーザーが選択する項目のリスト
    private final List<String> options = Arrays.asList("ジャンル", "おつかい", "交換","その他");
    private final List<String> options2 = Arrays.asList("難易度", "簡単", "普通", "難しい");
    // ユーザーが選択した項目を保持するLiveData
    public final MutableLiveData<String> selectedOption = new MutableLiveData<>();
    public final MutableLiveData<String> selectedOption2 = new MutableLiveData<>();
    // SharedPreferencesのキー
    private static final String SHARED_PREF_NAME = "Requester";
    // SharedPreferencesの取得
    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }
    // SharedPreferencesを使用して情報を保存するメソッド
    public void saveQuestInfo(Context context, String textInput1, String textInput2,
                              String spinner1Selection, String spinner2Selection) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);

        // テキスト入力の内容を保存
        sharedPreferences.edit().putString("Title", textInput1).apply();
        sharedPreferences.edit().putString("Report", textInput2).apply();

        // Spinner1の選択内容を保存
        sharedPreferences.edit().putString("Category", spinner1Selection).apply();

        // Spinner2の選択内容を保存
        sharedPreferences.edit().putString("Level", spinner2Selection).apply();
        sharedPreferences.edit().putBoolean("Request", true).apply();
    }


    public NotificationsViewModel() {
    }

    // 選択肢のリストを返すメソッド
    public List<String> getOptions() {
        return options;
    }
    public List<String> getOptions2() {
        return options2;
    }
    public void setSelectedOption(String option) {
        selectedOption.setValue(option);
    }
    public void setSelectedOption2(String option) {
        selectedOption2.setValue(option);
    }
}