package com.example.GuildWakayama2.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.app.AlertDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.GuildWakayama2.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;

    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ViewModelの初期化
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Spinner1の処理
        Spinner spinner1 = binding.spinner1;
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, viewModel.getOptions());
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = viewModel.getOptions().get(position);
                viewModel.setSelectedOption(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 何も選択されていない場合の処理
            }
        });

        // Spinner2の処理
        Spinner spinner2 = binding.spinner2;
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, viewModel.getOptions2());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = viewModel.getOptions2().get(position);
                viewModel.setSelectedOption2(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 何も選択されていない場合の処理
            }
        });

        // ログに表示するボタンの処理
        binding.logButton.setOnClickListener(v -> {
            // テキスト入力の内容をログに表示
            String textInput = binding.editTextUserInput1.getText().toString();
            Log.d("NotificationsFragment", "Text Input: " + textInput);

            String textInput2 = binding.editTextUserInput2.getText().toString();
            Log.d("NotificationsFragment", "Text Input2: " + textInput2);

            // Spinner1の選択内容をログに表示
            String spinner1Selection = viewModel.selectedOption.getValue();
            Log.d("NotificationsFragment", "Spinner 1 Selection: " + spinner1Selection);

            // Spinner2の選択内容をログに表示
            String spinner2Selection = viewModel.selectedOption2.getValue();
            Log.d("NotificationsFragment", "Spinner 2 Selection: " + spinner2Selection);
            // 保存メソッドを呼び出して情報をSharedPreferencesに保存
            viewModel.saveQuestInfo(requireContext(), textInput, textInput2, spinner1Selection, spinner2Selection);

            // 投稿データをFirebase Realtime Databaseに書き込む
            writePostDataToDatabase(textInput, textInput2, spinner1Selection, spinner2Selection);

        });
// ボタンが押されたときの処理
        // ダイアログの表示ボタンのクリックリスナー
        binding.showDialogButton.setOnClickListener(v -> {
            // 選択状態を保持するSet
            Set<String> selectedOptions = viewModel.getSelectedOptions();

            // 現在の選択状態をダイアログに反映
            boolean[] checkedItems = new boolean[viewModel.getOptions3().size()];
            int index = 0;
            for (String option : viewModel.getOptions3()) {
                checkedItems[index++] = selectedOptions.contains(option);
            }

            // ダイアログの作成
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Options")
                    .setMultiChoiceItems(viewModel.getOptions3().toArray(new CharSequence[0]), checkedItems,
                            (dialog, which, isChecked) -> {
                                String option = viewModel.getOptions3().get(which);
                                if (isChecked) {
                                    viewModel.onCheckboxChecked(option);
                                } else {
                                    viewModel.onCheckboxUnchecked(option);
                                }
                            })
                    .setPositiveButton("OK", (dialog, which) -> {
                        // ダイアログが閉じられたときの処理
                        viewModel.onDialogClosed();
                        // ダイアログが閉じられたときの情報を保存
                        viewModel.onDialogClosedSaveInfo(requireContext());
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        return root;
    }

    private void writePostDataToDatabase(String title, String body, String genre, String difficulty){
        // Firebase Realtime Databaseにデータを書き込む
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("requests");
        //reference.push().setValue(requestData);

        // FirebaseAuthのユーザデータを読み込む
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_name = user.getDisplayName();
        String user_id = user.getUid();

        String key = reference.child("requests").push().getKey();
        Post request = new Post(user_name, user_id, title, body, genre, difficulty,latitude,longitude);
        Map<String, Object> requestValues = request.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/requests" + key, requestValues);

        reference.updateChildren(childUpdates);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
