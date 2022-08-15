package com.flatcode.littlenote.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlenote.R;
import com.flatcode.littlenote.Unit.CLASS;
import com.flatcode.littlenote.Unit.DATA;
import com.flatcode.littlenote.Unit.THEME;
import com.flatcode.littlenote.Unit.VOID;
import com.flatcode.littlenote.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    private ActivitySplashBinding binding;

    FirebaseAuth auth;

    private Context context = Splash.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        VOID.Logo(getBaseContext(), binding.logo);
        VOID.Intro(getBaseContext(), binding.background, binding.backWhite, binding.backBlack);

        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            // check if user is logged in
            if (auth.getCurrentUser() != null) {
                VOID.Intent(context, CLASS.HOME);
                finish();
            } else {
                // create new anonymous account
                auth.signInAnonymously().addOnSuccessListener(authResult -> {
                    Toast.makeText(context, R.string.temporary_log, Toast.LENGTH_LONG).show();
                    VOID.Intent(context, CLASS.HOME);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, R.string.error_log + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }, DATA.DELAY_LOG);
    }
}