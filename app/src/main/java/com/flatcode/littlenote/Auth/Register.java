package com.flatcode.littlenote.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlenote.R;
import com.flatcode.littlenote.Unit.CLASS;
import com.flatcode.littlenote.Unit.DATA;
import com.flatcode.littlenote.Unit.THEME;
import com.flatcode.littlenote.Unit.VOID;
import com.flatcode.littlenote.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    FirebaseAuth auth;

    private ProgressDialog dialog;

    private Context context = Register.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        VOID.Logo(getBaseContext(), binding.logo);
        VOID.Intro(getBaseContext(), binding.background, binding.backWhite, binding.backBlack);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

        binding.login.setOnClickListener(v -> VOID.Intent(context, CLASS.LOGIN));

        binding.go.setOnClickListener(v -> {
            dialog.setMessage("A new account is created...");

            final String Username = binding.nameEt.getText().toString();
            String UserEmail = binding.emailEt.getText().toString();
            String UserPass = binding.cPasswordEt.getText().toString();
            String ConfirmPass = binding.passwordEt.getText().toString();

            if (UserEmail.isEmpty() || Username.isEmpty() || UserPass.isEmpty() || ConfirmPass.isEmpty()) {
                Toast.makeText(context, R.string.empty_required_all, Toast.LENGTH_SHORT).show();
                return;
            } else if (!UserPass.equals(ConfirmPass)) {
                binding.passwordEt.setError(DATA.ERR_PASS);
            } else {
                dialog.show();

                AuthCredential credential = EmailAuthProvider.getCredential(UserEmail, UserPass);
                Objects.requireNonNull(auth.getCurrentUser()).linkWithCredential(credential).addOnSuccessListener(authResult -> {
                    Toast.makeText(context, R.string.notes_synced, Toast.LENGTH_SHORT).show();
                    VOID.Intent(context, CLASS.HOME);

                    FirebaseUser usr = auth.getCurrentUser();
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(Username).build();
                    usr.updateProfile(request);

                    VOID.Intent(context, CLASS.HOME);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(context, R.string.failed_connect, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }
        });
    }
}