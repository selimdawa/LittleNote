package com.flatcode.littlenote.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlenote.Unit.CLASS;
import com.flatcode.littlenote.Unit.DATA;
import com.flatcode.littlenote.Unit.THEME;
import com.flatcode.littlenote.Unit.VOID;
import com.flatcode.littlenote.databinding.ActivityNoteDetailsBinding;

public class NoteDetails extends AppCompatActivity {

    private ActivityNoteDetailsBinding binding;

    Intent data;

    private Context context = NoteDetails.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        data = getIntent();

        binding.toolbar.nameSpace.setText(data.getStringExtra(DATA.TITLE));
        binding.description.setMovementMethod(new ScrollingMovementMethod());
        binding.description.setText(data.getStringExtra(DATA.CONTENT));
        binding.description.setBackgroundColor(getResources().getColor(data.getIntExtra(DATA.COLOR, DATA.DEFAULT_COLOR), null));

        binding.toolbar.edit.setOnClickListener(view1 -> VOID.IntentExtraEditFormDetails(context, CLASS.EDIT,
                DATA.TITLE, data, DATA.CONTENT, data, DATA.ID_PATH, data));
    }
}