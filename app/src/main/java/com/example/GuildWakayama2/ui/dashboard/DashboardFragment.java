// DashboardFragment.java
package com.example.GuildWakayama2.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.GuildWakayama2.ui.notifications.Post;
import com.example.GuildWakayama2.databinding.FragmentDashboardBinding;
import java.util.List;
import com.example.GuildWakayama2.R;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;
import android.content.SharedPreferences;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private LocationManager locationManager;
    private LocationListener locationListener;

    double latitude,longitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 位置情報の取得
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 位置情報が変化したときの処理
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // 初回位置情報の取得
        if (isAdded()) {
            getLocation();
        }

        // 更新ボタンの処理
        Button updateButton = binding.buttonUpdate;
        updateButton.setOnClickListener(v -> getLocation());

        // Firebaseからデータを取得
        FirebaseDataManager.getPosts(new FirebaseDataManager.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Post> posts) {
                int displayCount = 0; // 表示した回数をカウントする変数
                // データが正常に読み込まれた場合の処理
                for (Post post : posts) {
                    double postLatitude = post.latitude;
                    double postLongitude = post.longitude;

                    // 距離を計算
                    float distance = calculateDistance(postLatitude, postLongitude);

                    // 距離が1000未満の場合の処理
                    if (distance < 1000) {
                        displayCount++;

                        // 表示するテキストビューのインデックス
                        int textViewIndex = displayCount;

                        // テキストビューにデータを表示するメソッドの呼び出し
                        displayDataOnTextView(root, textViewIndex, post.title, post.difficulty, post.user_name,post.genre,post.body);

                        // 表示回数が6回に達した場合はループを抜ける
                        if (displayCount == 6) {
                            break;
                        }
                    }
                }
            }

            @Override
            public void onDataLoadError(String errorMessage) {
                // データ読み込みエラーの場合の処理
                Log.e("YourActivity", "Data load error: " + errorMessage);
            }
        });
        return root;
    }

    // 距離を計算するメソッド
    private float calculateDistance(double postLatitude, double postLongitude) {
        getLocation();
        float[] results = new float[3];
        Location.distanceBetween(latitude, longitude, postLatitude, postLongitude, results);
        return results[0];
    }

    // テキストビューにデータを表示するメソッド
    private void displayDataOnTextView(View root, int index, String title, String difficulty, String user_name,String genre,String body) {
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

        imageView.setOnClickListener(v -> showConfirmationDialog(title, difficulty, user_name, genre, body));
    }

    // 確認ダイアログを表示するメソッド
    private void showConfirmationDialog(String title, String difficulty, String user_name, String genre, String body) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("確認");
        builder.setMessage("この依頼を本当に受けますか？");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // OKが押されたときの処理
            onAcceptRequest(title, difficulty, user_name, genre, body);
        });
        builder.setNegativeButton("キャンセル", (dialog, which) -> {
            // キャンセルが押されたときの処理
            dialog.dismiss();
        });
        builder.show();
    }

    // 依頼を受けたときの処理
    private void onAcceptRequest(String title, String difficulty, String user_name, String genre, String body) {
        // ここにFirebaseに結果を書き込む処理を追加
        writeResultToFirebase(title, difficulty, user_name, genre, body);
        Log.d("DashBoard","Title: " + title + " Difficulty: " + difficulty
                + " Username: " + user_name + " Genre: " + genre + " Body: " + body);
        // ここにアプリ内で行う処理を追加
        // 例: トーストメッセージを表示
        Toast.makeText(requireContext(), "依頼を受けました", Toast.LENGTH_SHORT).show();
        // SharedPreferencesにデータを保存
        saveToSharedPreferences(title, difficulty, user_name, genre, body);
    }

    private void saveToSharedPreferences(String title, String difficulty, String user_name, String genre, String body) {
        // SharedPreferencesのインスタンスを取得
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Responder", Context.MODE_PRIVATE);

        // SharedPreferencesにデータを保存するためのEditorを取得
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // データを保存
        editor.putBoolean("Respond", true);
        editor.putString("Title", title);
        editor.putString("Difficulty", difficulty);
        editor.putString("Username", user_name);
        editor.putString("Genre", genre);

        // 変更を保存
        editor.apply();
    }

    // Firebaseに結果を書き込むメソッド
    private void writeResultToFirebase(String title, String difficulty, String user_name, String genre, String body) {
        // ここにFirebaseに書き込む処理を追加
        // FirebaseDataManager.writeResult(title, difficulty, user_name, genre, body);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            // 位置情報の更新をリクエスト
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 最新の位置情報を取得
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
            }
        } else {
            // パーミッションが許可されていない場合はリクエスト
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // 位置情報の更新を停止
        locationManager.removeUpdates(locationListener);
    }
}
