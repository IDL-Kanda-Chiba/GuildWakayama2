package com.example.GuildWakayama2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.app.AlertDialog;

import java.util.Map;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.GuildWakayama2.databinding.FragmentNotificationsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.GuildWakayama2.ui.LocationManagerHelper;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private LocationManagerHelper locationManagerHelper;

    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ViewModelの初期化
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        // 新しいLocationManagerHelperのインスタンスを生成
        locationManagerHelper = new LocationManagerHelper(requireContext());
        // 位置情報の取得
        locationManagerHelper.getLocation(this);

        // Spinner1の処理
        Spinner spinner1 = binding.spinner1;
        CustomArrayAdapter adapter1 = new CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.getOptions().toArray(new String[0]));
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
        CustomArrayAdapter adapter2 = new CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.getOptions2().toArray(new String[0]));
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

        // 新しいLocationManagerHelperのインスタンスを生成
        locationManagerHelper = new LocationManagerHelper(requireContext());

        // ログに表示するボタンの処理
        binding.logButton.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("依頼を提出します\nよろしいですか？");
            builder.setTitle("確認");
            builder.setPositiveButton("OK", (dialog, id) -> writerequest(root));
            builder.setNegativeButton("CANCEL", (dialog, id) -> cancel());
            builder.create();
            builder.show();
        });

        return root;
    }

    private void cancel(){}

    private void writerequest(View root){
        // テキスト入力の内容をログに表示
        String textInput = binding.editTextUserInput1.getText().toString();
        String textInput2 = binding.editTextUserInput2.getText().toString();
        String spinner1Selection = viewModel.selectedOption.getValue();
        String spinner2Selection = viewModel.selectedOption2.getValue();
        // 保存メソッドを呼び出して情報をSharedPreferencesに保存
        viewModel.saveQuestInfo(requireContext(), textInput, textInput2, spinner1Selection, spinner2Selection);
        Snackbar.make(root, "依頼が出されました", Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ボタンを押した時の処理
                    }
                })
                .show();
        // 位置情報の取得
        locationManagerHelper.getLocation(this);

        // 投稿データをFirebase Realtime Databaseに書き込む
        writePostDataToDatabase(textInput, textInput2, spinner1Selection, spinner2Selection);
    }

    private void writePostDataToDatabase(String title, String body, String genre, String difficulty){
        // Firebase Realtime Databaseにデータを書き込む
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("requests"); //親データ
        //reference.push().setValue(requestData);

        // FirebaseAuthのユーザデータを読み込む
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_name = user.getDisplayName();
        String user_id = user.getUid();
        latitude = locationManagerHelper.getLatitude();
        longitude = locationManagerHelper.getLongitude();

        String key = reference.child("requests").push().getKey();
        Post request = new Post(user_name, user_id, title, body, genre, difficulty,latitude,longitude);
        Map<String, Object> requestValues = request.toMap(); //リクエストデータを構造化（ツリー化）
        //reference.child(key).child(user_name).setValue(requestValues);
        reference.child("user-id").child(user_id).setValue(requestValues);

        //Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/requests" + key, requestValues); //request(親)にrequest(子)を作成しリクエストデータを格納

        //reference.updateChildren(childUpdates);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
