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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;




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
        switchrespondshow();
        setupEventListener(); // イベントリスナーの設定
        return root;
    }

    private void setupEventListener() {
        // イベントリスナーを設定
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference("events").child("user-id").child(userId);
        eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventId = eventSnapshot.getKey();
                    boolean judge = eventSnapshot.getValue(boolean.class);
                    Log.d("RespondUser", "judge: " + judge);

                    // ここでイベントを処理する（例: 依頼達成処理）
                    handleCompletionEvent(judge);

                    // イベントノードから削除
                    eventReference.child(eventId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // エラー処理
            }
        });
    }


    private void handleCompletionEvent(boolean judge) {
        if(judge){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Responder", Context.MODE_PRIVATE);
            String level = sharedPreferences.getString("Difficulty", "");
            sharedPreferences =
                    getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
            int point = sharedPreferences.getInt("point",0);
            if ("普通".equals(level)) {
                point += 200; // 普通の場合、ベース+1
            } else if ("難しい".equals(level)) {
                point += 400; // 難しいの場合、ベース+2
            } else {
                point += 100; // 簡単など、それ以外の場合はベースのみ
            }sharedPreferences.edit().putInt("point", point).apply();
            Toast.makeText(requireContext(), "依頼を達成しました", Toast.LENGTH_SHORT).show();
        }CancelRespond();
    }

    private void switchrespondshow(){
        Button respond_cancel_button = binding.RespondCancelButton;
        Button respond_solve_button = binding.RespondReviewButton;
        respond_cancel_button.setOnClickListener(v -> CancelRespond());
        respond_solve_button.setOnClickListener(v -> SolveRespond());

        ImageView respond_imageview = binding.RespondImageview;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Responder", Context.MODE_PRIVATE);
        boolean currentRequestState = sharedPreferences.getBoolean("Respond", false);
        if(currentRequestState){
            respond_solve_button.setVisibility(View.VISIBLE);
            respond_cancel_button.setVisibility(View.VISIBLE);
            respond_imageview.setVisibility(View.GONE);
        }else{
            respond_solve_button.setVisibility(View.GONE);
            respond_cancel_button.setVisibility(View.GONE);
            respond_imageview.setVisibility(View.VISIBLE);
        }displayRespondData();
    }

    private void CancelRespond(){
        DeleteRespond();
        switchrespondshow();
    }

    private void SolveRespond() {
        //DeleteRespond();
    }

    private void displayRespondData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Responder", Context.MODE_PRIVATE);
        boolean isRequestTrue = sharedPreferences.getBoolean("Respond", false);
        if (isRequestTrue) {
            String title = sharedPreferences.getString("Title", "");
            String category = sharedPreferences.getString("Genre", "");
            String level = sharedPreferences.getString("Difficulty", "");

            String displayText = "ジャンル: " + category + "\n難易度: " + level;
            binding.RespondTitle.setText(title);
            binding.RespondTextview.setText(displayText);
        } else {
            // Requestがfalseの場合のログ表示
            binding.RespondTitle.setText("あなたは依頼を受けていません");
            binding.RespondTextview.setText("で依頼を受けてください");
        }
    }

    private void DeleteRespond() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Responder", Context.MODE_PRIVATE);
        boolean currentRespondState = sharedPreferences.getBoolean("Respond", false);
        boolean newRequestState = !currentRespondState;
        sharedPreferences.edit().putBoolean("Respond", newRequestState).apply();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("requests");
        String respondKey = sharedPreferences.getString("RequestKey", "");
        reference.child("user-id").child(respondKey).child("respond_user_id").removeValue();
    }

    private void switchrequestshow(){
        Button request_cancel_button = binding.RequestCancelButton; // 自分の投稿を取り消すボタン
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
        }displayrequestData();
    }

    // 投稿を取り消すボタンを押したときの処理
    private void CancelRequest(){
        DeleteRequest(2); // 投稿を取り消す処理
        switchrequestshow();
    }

    private void SolveRequest() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Requester", Context.MODE_PRIVATE);
        String level = sharedPreferences.getString("Level", "");
        sharedPreferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        int ticket = sharedPreferences.getInt("ticket", 0);
        ticket -= getTicketConsumptionByLevel(level);
        if (ticket >= 0) {
            sharedPreferences.edit().putInt("ticket", ticket).apply();
            DeleteRequest(1);
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
                DeleteRequest(1);
                switchrequestshow();
            }
        });
        builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // キャンセルの場合は何もしない
                DeleteRequest(3);
                DeleteRequest(2);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayrequestData() {
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

    private void DeleteRequest(int judge) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Requester", Context.MODE_PRIVATE);
        boolean currentRequestState = sharedPreferences.getBoolean("Request", false);
        boolean newRequestState = !currentRequestState;
        sharedPreferences.edit().putBoolean("Request", newRequestState).apply();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("requests");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();
        Log.d("RespondUser", "RespondUser" + user_id);

        reference.child("user-id").child(user_id).child("respond_user_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String respondUserId = dataSnapshot.getValue(String.class);
                if (respondUserId != null) {
                    // 依頼が解決されたかキャンセルされたかに応じてイベントを送信
                    if (judge == 1) {
                        sendRequestEvent(true, respondUserId);
                        Log.d("RespondUser", "Respond" + respondUserId);
                    } else if (judge == 2) {
                        sendRequestEvent(false, respondUserId);
                    }
                }//依頼データを削除
                reference.child("user-id").child(user_id).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendRequestEvent(boolean isResolved,String respondUserId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventReference = database.getReference("events");

        String eventId = eventReference.child("user-id").child(respondUserId).push().getKey();
        eventReference.child("user-id").child(respondUserId).child(eventId).setValue(isResolved);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
