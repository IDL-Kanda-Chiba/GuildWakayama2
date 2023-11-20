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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.GuildWakayama2.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private LocationManager locationManager;
    private LocationListener locationListener;
    // 通天閣の位置情報（仮のデータ）
    private static final double TENNOJI_TOWER_LATITUDE = 34.540813;
    private static final double TENNOJI_TOWER_LONGITUDE = 135.592972;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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
        getLocation();

        // 更新ボタンの処理
        Button updateButton = binding.buttonUpdate;
        updateButton.setOnClickListener(v -> getLocation());

        return root;
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
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                Log.d("Location", "Last Known Latitude: " + latitude + ", Longitude: " + longitude);

                // 通天閣の位置情報
                Location tennojiTowerLocation = new Location("Tennoji Tower");
                tennojiTowerLocation.setLatitude(TENNOJI_TOWER_LATITUDE);
                tennojiTowerLocation.setLongitude(TENNOJI_TOWER_LONGITUDE);

                // 距離を計算
                float distance = lastKnownLocation.distanceTo(tennojiTowerLocation);
                Log.d("Location", "Distance to Tennoji Tower: " + distance + " meters");
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
