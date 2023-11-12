package com.example.GuildWakayama2.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import android.util.Log;
import java.util.HashSet;

public class NotificationsViewModel extends ViewModel {

    // ユーザーが選択する項目のリスト
    private final List<String> options = Arrays.asList("Option 1", "Option 2", "Option 3");
    private final List<String> options2 = Arrays.asList("Option 2.1", "Option 2.2", "Option 2.3");

    // ユーザーが選択した項目を保持するLiveData
    public final MutableLiveData<String> selectedOption = new MutableLiveData<>();
    public final MutableLiveData<String> selectedOption2 = new MutableLiveData<>();
    // 選択された項目を保持するSet
    private final Set<String> selectedOptions = new HashSet<>();

    // Getterメソッド
    public Set<String> getSelectedOptions() {
        return selectedOptions;
    }
    // ダイアログが閉じられるときに呼ばれるメソッド
    public void onDialogClosed() {
        // 選択された項目をログに表示（実際のアプリではここで保存などの処理を行う）
        for (String selectedOption : selectedOptions) {
            Log.d("NotificationsViewModel", "Selected Option: " + selectedOption);
        }
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