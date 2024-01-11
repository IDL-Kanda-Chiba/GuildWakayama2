// DashboardFragment.java
package com.example.GuildWakayama2.ui.dashboard;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.GuildWakayama2.ui.notifications.Post;
import com.example.GuildWakayama2.databinding.FragmentDashboardBinding;
import java.util.List;
import com.example.GuildWakayama2.R;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import com.example.GuildWakayama2.ui.LocationManagerHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private LocationManagerHelper locationManagerHelper;

    double latitude,longitude;
    private View root;

    private int displayedCount;
    private boolean norequest = false;

    LinearLayout LL_Load,LL_Main;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // 新しいLocationManagerHelperのインスタンスを生成
        locationManagerHelper = new LocationManagerHelper(requireContext());

        // 初回位置情報の取得
        if (isAdded()) {
            locationManagerHelper.getLocation(this);
        }

        // 更新ボタンの処理
        Button updateButton = binding.buttonUpdate;
        updateButton.setOnClickListener(v -> loadDataFromFirebase());

        LL_Load = binding.LLLoad;
        LL_Main = binding.LLMain;

        // 初回のデータ読み込み
        loadDataFromFirebase();
        return root;
    }

    private void loadDataFromFirebase() {
        // 表示したデータをリセット
        resetDisplayedData();
        FirebaseDataManager.getPosts("user-id", new FirebaseDataManager.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Post> posts) {
                // データの表示処理をここに移動
                displayData(posts);
            }

            @Override
            public void onDataLoadError(String errorMessage) {
                // データ読み込みエラーの場合の処理
                Snackbar.make(root, "データの読み込みに失敗しました", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ボタンを押した時の処理
                            }
                        })
                        .show();
            }
        });
    }

    private void displayData(List<Post> posts) {

        // データが正常に読み込まれた場合の処理
        for (Post post : posts) {
            double postLatitude = post.latitude;
            double postLongitude = post.longitude;

            // 距離を計算
            float distance = calculateDistance(postLatitude, postLongitude);

            // 距離が1000未満の場合の処理
            if (distance < 1000) {
                displayedCount++;

                // 表示するテキストビューのインデックス
                int textViewIndex = displayedCount;

                // テキストビューにデータを表示するメソッドの呼び出し
                displayDataOnTextView(textViewIndex,
                        post.title, post.difficulty, post.user_name, post.genre, post.body,
                        post.user_id,postLatitude,postLongitude);

                // 表示回数が6回に達した場合はループを抜ける
                if (displayedCount == 6) {
                    break;
                }
            }
        }
        if(displayedCount == 0){
            displayedCount++;
            norequest = true;
        }else{
            norequest = false;
        }// 表示済みのデータがあれば表示する
        for (int i = 1; i <= displayedCount; i++) {
            showDataOnTextView(i);
        }// 非表示のデータがあれば非表示にする
        for (int i = displayedCount + 1; i <= 6; i++) {
            hideDataOnTextView(i);
        }
        LL_Load.setVisibility(View.GONE);
        LL_Main.setVisibility(View.VISIBLE);
    }

    private void resetDisplayedData() {
        LL_Load.setVisibility(View.VISIBLE);
        LL_Main.setVisibility(View.GONE);
        displayedCount = 0;
    }

    private void showDataOnTextView(int index) {
        int requestId = getResources().getIdentifier
                ("request" + index, "id", requireContext().getPackageName());
        LinearLayout linearLayout = root.findViewById(requestId);
        linearLayout.setVisibility(View.VISIBLE);
        if(norequest){
            int titleId = getResources().getIdentifier("title" + index, "id", requireContext().getPackageName());
            int levelId = getResources().getIdentifier("level" + index, "id", requireContext().getPackageName());
            int usernameId = getResources().getIdentifier("username" + index, "id", requireContext().getPackageName());
            int imageId = getResources().getIdentifier("image" + index, "id", requireContext().getPackageName());
            int bodyId = getResources().getIdentifier("body" + index, "id", requireContext().getPackageName());

            TextView titleTextView = root.findViewById(titleId);
            TextView levelTextView = root.findViewById(levelId);
            TextView usernameTextView = root.findViewById(usernameId);
            ImageView imageView = root.findViewById(imageId);
            TextView bodyTextView = root.findViewById(bodyId);

            titleTextView.setText("申し訳ありません");
            levelTextView.setText("現在依頼が出ておりません");
            usernameTextView.setText("");
            bodyTextView.setText("時間を空けてもう一度お試しください");
            imageView.setImageResource(R.drawable.image_syazai);
        }
    }


    // displayDataOnTextViewメソッドの後に以下のメソッドを追加
    private void hideDataOnTextView(int index) {
        int requestId = getResources().getIdentifier
                ("request" + index, "id", requireContext().getPackageName());
        LinearLayout linearLayout = root.findViewById(requestId);
        linearLayout.setVisibility(View.GONE);
    }

    // 距離を計算するメソッド
    private float calculateDistance(double postLatitude, double postLongitude) {
        locationManagerHelper.getLocation(this);
        float[] results = new float[3];
        latitude = locationManagerHelper.getLatitude();
        longitude = locationManagerHelper.getLongitude();
        Location.distanceBetween(latitude, longitude, postLatitude, postLongitude, results);
        return results[0];
    }

    // テキストビューにデータを表示するメソッド
    private void displayDataOnTextView(int index, String title, String difficulty, String user_name,
                                       String genre, String body,String user_id,
                                       double respond_latitude,double respond_longitude) {

        int titleId = getResources().getIdentifier("title" + index, "id", requireContext().getPackageName());
        int levelId = getResources().getIdentifier("level" + index, "id", requireContext().getPackageName());
        int usernameId = getResources().getIdentifier("username" + index, "id", requireContext().getPackageName());
        int imageId = getResources().getIdentifier("image" + index, "id", requireContext().getPackageName());
        int bodyId = getResources().getIdentifier("body" + index, "id", requireContext().getPackageName());

        TextView titleTextView = root.findViewById(titleId);
        TextView levelTextView = root.findViewById(levelId);
        TextView usernameTextView = root.findViewById(usernameId);
        ImageView imageView = root.findViewById(imageId);
        TextView bodyTextView = root.findViewById(bodyId);

        titleTextView.setText(title);
        levelTextView.setText(difficulty);
        usernameTextView.setText(user_name);
        bodyTextView.setText(body);

        if("おつかい".equals(genre)){
            imageView.setImageResource(R.drawable.image_shopping);
        }else if("交換".equals(genre)){
            imageView.setImageResource(R.drawable.image_change);
        }else{
            imageView.setImageResource(R.drawable.image_other);
        }

        imageView.setOnClickListener(v -> showConfirmationDialog(
                title, difficulty, user_name, genre,user_id,respond_latitude,respond_longitude));
    }

    // 確認ダイアログを表示するメソッド
    private void showConfirmationDialog(String title, String difficulty, String user_name, String genre,
                                        String user_id,double respond_latitude, double respond_longitude) {
        Log.d("dashboard","requestId" + user_id);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("確認");
        builder.setMessage("この依頼を本当に受けますか？");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // OKが押されたときの処理
            onAcceptRequest(title, difficulty, user_name, genre,user_id,respond_latitude,respond_longitude);
        });
        builder.setNegativeButton("キャンセル", (dialog, which) -> {
            // キャンセルが押されたときの処理
            dialog.dismiss();
        });
        builder.show();
    }

    // 依頼を受けたときの処理
    private void onAcceptRequest(String title, String difficulty, String user_name, String genre,
                                 String user_id,double respond_latitude, double respond_longitude) {
        // ここにFirebaseに結果を書き込む処理を追加
        writeResultToFirebase(user_id);
        // ここにアプリ内で行う処理を追加
        Snackbar.make(root, "依頼を受けました", Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ボタンを押した時の処理
                    }
                })
                .show();
        // SharedPreferencesにデータを保存
        saveToSharedPreferences(title, difficulty, user_name, genre, user_id,respond_latitude,respond_longitude);
    }

    private void saveToSharedPreferences(String title, String difficulty, String user_name,
                                         String genre, String user_id,
                                         double respond_latitude,double respond_longitude) {
        // SharedPreferencesのインスタンスを取得
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Responder", Context.MODE_PRIVATE);

        // SharedPreferencesにデータを保存するためのEditorを取得
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String latitude = String.valueOf(respond_latitude);
        String longitude = String.valueOf(respond_longitude);

        // データを保存
        editor.putBoolean("Respond", true);
        editor.putString("Title", title);
        editor.putString("Difficulty", difficulty);
        editor.putString("Username", user_name);
        editor.putString("Genre", genre);
        editor.putString("RequestKey", user_id);
        editor.putString("Latitude", latitude);
        editor.putString("Longitude", longitude);

        // 変更を保存
        editor.apply();
    }

    // Firebaseに結果を書き込むメソッド
    private void writeResultToFirebase(String requestId) {
        // Firebase Realtime Databaseの初期化
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // データベースの参照を取得（"requests"は適切なノード名に置き換えてください）
        DatabaseReference requestsRef = database.getReference("requests");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // 受けた依頼の子ノードにrespond_user_idを追加
        requestsRef.child("user-id").child(requestId).child("respond_user_id").setValue(userId);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
