package com.example.GuildWakayama2.ui.mypage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.GuildWakayama2.R;
import com.example.GuildWakayama2.databinding.FragmentMypageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MypageFragment extends Fragment {

    private FragmentMypageBinding binding;
    private MypageViewModel mypageViewModel;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMypageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mypageViewModel = new ViewModelProvider(requireActivity()).get(MypageViewModel.class);

        final ImageView userIconImageView = binding.userIconImageView;
        final TextView userNameTextView = binding.userNameTextView;
        final TextView pointTextView = binding.pointTextView;
        final TextView ticketTextView = binding.ticketTextView;
        final Button increaseTicketButton = binding.increaseTicketButton;
        final Button changeInfoButton = binding.changeInfoButton;
        final Button exchangeButton = binding.exchangeButton;

        mypageViewModel.getUserName().observe(getViewLifecycleOwner(), userName -> userNameTextView.setText(userName));
        mypageViewModel.getPoint().observe(getViewLifecycleOwner(), point -> pointTextView.setText(getString(R.string.point_format, point)));
        mypageViewModel.getTicket().observe(getViewLifecycleOwner(), ticket -> ticketTextView.setText(getString(R.string.ticket_format, ticket)));
        userIconImageView.setImageResource(R.drawable.ic_icon); // Assuming you have ic_profile.png in your resources.
        mypageViewModel.loadUserData();

        increaseTicketButton.setOnClickListener(v -> {
            mypageViewModel.increaseTicket();
            Snackbar.make(root, "チケットが一枚増えました", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // ボタンを押した時の処理
                        }
                    })
                    .show();
        });

        exchangeButton.setOnClickListener(v -> {
            Snackbar.make(root, "ポイントは使用できません", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // ボタンを押した時の処理
                        }
                    })
                    .show();
        });


        changeInfoButton.setOnClickListener(v -> {
            // Show a dialog to change user information (username, email, password).
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("変更したい情報を入力してください");

            // Remove the following line since 'inflater' is already defined in the outer scope
            // LayoutInflater inflater = requireActivity().getLayoutInflater();

            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_info, null);
            builder.setView(dialogView);

            EditText newUsernameEditText = dialogView.findViewById(R.id.editTextNewUsername);
            EditText newEmailEditText = dialogView.findViewById(R.id.editTextNewEmail);
            EditText newPasswordEditText = dialogView.findViewById(R.id.editTextNewPassword);

            newUsernameEditText.setText(mypageViewModel.getUserName().getValue());
            newEmailEditText.setText(mypageViewModel.getEmail().getValue());
            newPasswordEditText.setText(mypageViewModel.getPassword().getValue());

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newUsername = newUsernameEditText.getText().toString();
                String newEmail = newEmailEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                // Firebase Authのユーザ名を更新
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("sample", "User profile updated.");
                                }
                            }
                        });

                // realtime databaseの親から子をたどってデータベースを更新
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("requests");
                databaseReference.child("user-id").child(user.getUid()).child("user_name").setValue(newUsername);

                mypageViewModel.setUserName(newUsername);
                mypageViewModel.setEmail(newEmail);
                mypageViewModel.setPassword(newPassword);
                mypageViewModel.saveUserData();

                Snackbar.make(root, "プロフィールが変更されました", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ボタンを押した時の処理
                            }
                        })
                        .show();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // User canceled the dialog
            });

            AlertDialog dialog = builder.create();
            dialog.getWindow().setLayout(250, 250);
            dialog.show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
