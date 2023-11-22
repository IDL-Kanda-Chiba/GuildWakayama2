package com.example.GuildWakayama2.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.GuildWakayama2.databinding.FragmentHomeBinding;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        switchrequestshow();
        return root;
    }

    private void switchrequestshow(){
        Button request_cancel_button = binding.RequestCancelButton;
        Button request_solve_button = binding.RequestSolveButton;
        request_cancel_button.setOnClickListener(v -> CancelRequest());
        request_solve_button.setOnClickListener(v -> SolveRequest());

        ImageView request_imageview = binding.RequestImageView;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Requester", Context.MODE_PRIVATE);
        boolean currentRequestState = sharedPreferences.getBoolean("Request", false);
        if(currentRequestState){
            request_solve_button.setVisibility(View.VISIBLE);
            request_cancel_button.setVisibility(View.VISIBLE);
            request_imageview.setVisibility(View.GONE);
        }else{
            request_solve_button.setVisibility(View.GONE);
            request_cancel_button.setVisibility(View.GONE);
            request_imageview.setVisibility(View.VISIBLE);
        }displayData();
    }

    private void CancelRequest(){
        DeleteRequest();
        switchrequestshow();
    }

    private void SolveRequest() {
        DeleteRequest();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Requester", Context.MODE_PRIVATE);
        String level = sharedPreferences.getString("Level", "");
        sharedPreferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        int ticket = sharedPreferences.getInt("ticket", 0);
        ticket -= getTicketConsumptionByLevel(level);
        if (ticket >= 0) {
            sharedPreferences.edit().putInt("ticket", ticket).apply();
            switchrequestshow();
        } else {
            showTicketRechargeDialog();
        }
    }

    private int getTicketConsumptionByLevel(String level) {
        int baseTicketConsumption = 1; // ベースのチケット消費枚数
        // 難易度ごとの追加のチケット消費枚数
        if ("普通".equals(level)) {
            return baseTicketConsumption + 1; // 普通の場合、ベース+1
        } else if ("難しい".equals(level)) {
            return baseTicketConsumption + 2; // 難しいの場合、ベース+2
        } else {
            return baseTicketConsumption; // 簡単など、それ以外の場合はベースのみ
        }
    }

    private void showTicketRechargeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("チケットが足りません");
        builder.setMessage("チケットを増やしますか？");
        builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // チケットを増やす処理（例：+1）
                SharedPreferences sharedPreferences =
                        getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("ticket", 0).apply();
                switchrequestshow();
            }
        });
        builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // キャンセルの場合は何もしない
                DeleteRequest();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Requester", Context.MODE_PRIVATE);
        boolean isRequestTrue = sharedPreferences.getBoolean("Request", false);
        if (isRequestTrue) {
            String title = sharedPreferences.getString("Title", "");
            String category = sharedPreferences.getString("Category", "");
            String level = sharedPreferences.getString("Level", "");

            String displayText = "ジャンル: " + category + "\n難易度: " + level;
            binding.RequestTitle.setText(title);
            binding.RequestTextView.setText(displayText);
        } else {
            // Requestがfalseの場合のログ表示
            binding.RequestTitle.setText("あなたは依頼を出していません");
            binding.RequestTextView.setText("で依頼を出してください");
        }
    }

    private void DeleteRequest() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Requester", Context.MODE_PRIVATE);
        boolean currentRequestState = sharedPreferences.getBoolean("Request", false);
        boolean newRequestState = !currentRequestState;
        sharedPreferences.edit().putBoolean("Request", newRequestState).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
