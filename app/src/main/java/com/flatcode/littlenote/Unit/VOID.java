package com.flatcode.littlenote.Unit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.flatcode.littlenote.Model.Note;
import com.flatcode.littlenote.R;

import java.util.Objects;

public class VOID {

    public static void IntentClear(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void Intent(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void IntentExtraDetails
            (Context context, Class c, String key, Note value, String key2, Note value2
                    , String key3, int value3, String key4, String value4) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value.getTitle());
        intent.putExtra(key2, value2.getContent());
        intent.putExtra(key3, value3);
        intent.putExtra(key4, value4);
        context.startActivity(intent);
    }

    public static void IntentExtraEditFormDetails
            (Context context, Class c, String key,
             Intent value, String key2, Intent value2, String key3, Intent value3) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value.getStringExtra(DATA.TITLE));
        intent.putExtra(key2, value2.getStringExtra(DATA.CONTENT));
        intent.putExtra(key3, value3.getStringExtra(DATA.ID_PATH));
        context.startActivity(intent);
    }

    public static void closeApp(Context context, Activity activity) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_close_app);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.yes).setOnClickListener(v -> activity.finish());
        dialog.findViewById(R.id.no).setOnClickListener(v -> dialog.cancel());

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
    }

    public static void aboutAccount(Context context, String username, String email) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_account);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView Username = dialog.findViewById(R.id.username);
        Username.setText(username);
        TextView Email = dialog.findViewById(R.id.email);
        Email.setText(email);

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
    }

    public static void Intro(Context context, ImageView background, ImageView backWhite, ImageView backDark) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("color_option", "ONE").equals("ONE")) {
            background.setImageResource(R.drawable.background_day);
            backWhite.setVisibility(View.VISIBLE);
            backDark.setVisibility(View.GONE);
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE").equals("NIGHT_ONE")) {
            background.setImageResource(R.drawable.background_night);
            backWhite.setVisibility(View.GONE);
            backDark.setVisibility(View.VISIBLE);
        }
    }

    public static void Logo(Context context, ImageView background) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("color_option", "ONE").equals("ONE")) {
            background.setImageResource(R.drawable.logo);
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE").equals("NIGHT_ONE")) {
            background.setImageResource(R.drawable.logo_night);
        }
    }
}