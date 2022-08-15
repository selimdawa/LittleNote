package com.flatcode.littlenote.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlenote.R;
import com.flatcode.littlenote.Unit.CLASS;
import com.flatcode.littlenote.Unit.DATA;
import com.flatcode.littlenote.Unit.THEME;
import com.flatcode.littlenote.Unit.VOID;
import com.flatcode.littlenote.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore store;
    FirebaseUser user;

    private ProgressDialog dialog;

    private Context context = Login.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        VOID.Logo(getBaseContext(), binding.logo);
        VOID.Intro(getBaseContext(), binding.background, binding.backWhite, binding.backBlack);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        showWarning();

        binding.loginBtn.setOnClickListener(v -> {
            dialog.setMessage("Logging In...");

            String Email = binding.emailEt.getText().toString();
            String Password = binding.passwordEt.getText().toString();

            if (Email.isEmpty() || Password.isEmpty()) {
                Toast.makeText(context, R.string.empty_required, Toast.LENGTH_SHORT).show();
                return;
            } else {
                // delete notes first
                dialog.show();

                auth.signInWithEmailAndPassword(Email, Password).addOnSuccessListener(authResult -> {
                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
                    if (Objects.requireNonNull(auth.getCurrentUser()).isAnonymous()) {
                        FirebaseUser user = auth.getCurrentUser();

                        store.collection(DATA.PARENT_PATH).document(user.getUid()).delete().addOnSuccessListener(aVoid ->
                                Toast.makeText(context, R.string.delete_message_notes, Toast.LENGTH_SHORT).show());

                        // delete Temp user
                        user.delete().addOnSuccessListener(aVoid -> Toast.makeText(context, R.string.delete_message_user, Toast.LENGTH_SHORT).show());
                    }
                    VOID.IntentClear(context, CLASS.HOME);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, R.string.login_failure + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }
        });

        binding.forget.setOnClickListener(v -> VOID.Intent(context, CLASS.FORGET_PASSWORD));
        binding.noAccount.setOnClickListener(v -> VOID.Intent(context, CLASS.REGISTER));
    }

    private void showWarning() {
        final AlertDialog.Builder warning = new AlertDialog.Builder(context)
                .setTitle(R.string.alert_delete_title)
                .setMessage(R.string.alert_login_message)
                .setPositiveButton(R.string.alert_login_positive, (dialog, which) -> {
                    VOID.Intent(context, CLASS.REGISTER);
                    finish();
                }).setNegativeButton(R.string.alert_login_negative, (dialog, which) -> {
                    // do nothing
                });

        warning.show();
    }
}