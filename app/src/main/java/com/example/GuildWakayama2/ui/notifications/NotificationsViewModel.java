package com.example.GuildWakayama2.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import android.util.Log;
import java.util.HashSet;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

public class NotificationsViewModel extends ViewModel {

    // ユーザーが選択する項目のリスト
    private final List<String> options = Arrays.asList("ジャンル", "おつかい", "交換","その他");
    private final List<String> options2 = Arrays.asList("難易度", "簡単", "普通", "難しい");
    private final List<String> options3 = Arrays.asList("若者募集", "男性募集", "女性募集", "学生可", "同年代募集");

    // ユーザーが選択した項目を保持するLiveData
    public final MutableLiveData<String> selectedOption = new MutableLiveData<>();
    public final MutableLiveData<String> selectedOption2 = new MutableLiveData<>();
    // 選択された項目を保持するSet
    private final Set<String> selectedOptions = new HashSet<>();
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

    // Getterメソッド
    public Set<String> getSelectedOptions() {
        return selectedOptions;
    }
    // ダイアログが閉じられるときに呼ばれるメソッド
    public void onDialogClosed() {
        // 選択された項目をログに表示（実際のアプリではここで保存などの処理を行う）
        for (String selectedOption : selectedOptions) {
            Log.d("NotificationsViewModel", "Selected Option: " + selectedOption);
        }for (String removedOption : removedOptions) {
            Log.d("NotificationsViewModel", "Removed Option: " + removedOption);
        }
    }// SharedPreferencesを使用してダイアログが閉じられたときの情報を保存するメソッド
    private final List<String> removedOptions = new ArrayList<>();

    // チェックボックスがチェックされたときの処理
    public void onCheckboxChecked(String option) {
        if (!selectedOptions.contains(option)) {
            selectedOptions.add(option);
        }
        if(removedOptions.contains(option)){
            removedOptions.remove(option);
        }
    }

    // チェックボックスが非チェックになったときの処理
    public void onCheckboxUnchecked(String option) {
        if (selectedOptions.contains(option)) {
            selectedOptions.remove(option);
        }
        if (!removedOptions.contains(option)) {
            removedOptions.add(option);
        }
    }

    // SharedPreferencesを使用してダイアログが閉じられたときの情報を保存するメソッド
    public void onDialogClosedSaveInfo(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // チェックがついた項目をtrueで保存
        for (String option : selectedOptions) {
            editor.putBoolean(option, true);
        }

        // チェックが外れた項目をfalseで保存
        for (String option : removedOptions) {
            editor.putBoolean(option, false);
        }

        editor.apply();
    }

    public NotificationsViewModel() {
    }

    // 選択肢のリストを返すメソッド
    public List<String> getOptions() {
        return options;
    }
    public List<String> getOptions2() {
        return options2;
    }public List<String> getOptions3() {
        return options3;
    }
    public void setSelectedOption(String option) {
        selectedOption.setValue(option);
    }
    public void setSelectedOption2(String option) {
        selectedOption2.setValue(option);
    }
}