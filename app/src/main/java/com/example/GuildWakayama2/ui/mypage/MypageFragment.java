package com.example.GuildWakayama2.ui.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.GuildWakayama2.R;
import com.example.GuildWakayama2.databinding.FragmentMypageBinding;

public class MypageFragment extends Fragment {

    private FragmentMypageBinding binding;
    private MypageViewModel mypageViewModel;

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

        mypageViewModel.getUserName().observe(getViewLifecycleOwner(), userName -> userNameTextView.setText(userName));
        mypageViewModel.getPoint().observe(getViewLifecycleOwner(), point -> pointTextView.setText(getString(R.string.point_format, point)));
        mypageViewModel.getTicket().observe(getViewLifecycleOwner(), ticket -> ticketTextView.setText(getString(R.string.ticket_format, ticket)));
        userIconImageView.setImageResource(R.drawable.ic_profile); // Assuming you have ic_profile.png in your resources.
        mypageViewModel.loadUserData();

        increaseTicketButton.setOnClickListener(v -> {
            mypageViewModel.increaseTicket();
            logUserData();
        });

        changeInfoButton.setOnClickListener(v -> {
            // Show a dialog to change user information (username, email, password).
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change User Information");

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

                mypageViewModel.setUserName(newUsername);
                mypageViewModel.setEmail(newEmail);
                mypageViewModel.setPassword(newPassword);
                mypageViewModel.saveUserData();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // User canceled the dialog
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
        logUserData(); // Log user data initially.

        return root;
    }

    private void logUserData() {
        // Log user data (password, point, ticket) to console.
        String userData = "Password: " + mypageViewModel.getPassword() +
                ", Point: " + mypageViewModel.getPoint().getValue() +
                ", Ticket: " + mypageViewModel.getTicket().getValue();
        System.out.println(userData);
        Log.d("MyPage",userData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
