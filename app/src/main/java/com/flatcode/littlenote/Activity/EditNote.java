package com.flatcode.littlenote.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlenote.R;
import com.flatcode.littlenote.Unit.CLASS;
import com.flatcode.littlenote.Unit.DATA;
import com.flatcode.littlenote.Unit.THEME;
import com.flatcode.littlenote.Unit.VOID;
import com.flatcode.littlenote.databinding.ActivityAddEditNoteBinding;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {

    private ActivityAddEditNoteBinding binding;

    Intent data;

    private Context context = EditNote.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.edit_note);

        binding.toolbar.image.setVisibility(View.VISIBLE);
        binding.toolbar.image.setImageResource(R.drawable.ic_true);

        data = getIntent();

        String noteTitle = data.getStringExtra(DATA.TITLE);
        String noteContent = data.getStringExtra(DATA.CONTENT);

        binding.noteTitle.setText(noteTitle);
        binding.noteContent.setText(noteContent);

        binding.toolbar.image.setOnClickListener(view1 -> {
            String nTitle = binding.noteTitle.getText().toString();
            String nContent = binding.noteContent.getText().toString();

            if (nTitle.isEmpty() || nContent.isEmpty()) {
                Toast.makeText(context, R.string.error_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            // save note
            DocumentReference document = DATA.FIREBASE_STORE.collection(DATA.PARENT_PATH).document(DATA.FirebaseUserUid).collection(DATA.CHILD_PATH).document(data.getStringExtra(DATA.ID_PATH));
            Map<String, Object> note = new HashMap<>();
            note.put(DATA.TITLE, nTitle);
            note.put(DATA.CONTENT, nContent);

            document.update(note).addOnSuccessListener(aVoid -> {
                Toast.makeText(context, R.string.note_saved, Toast.LENGTH_SHORT).show();
                VOID.Intent(context, CLASS.HOME);
                onBackPressed();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, R.string.error_saved, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.VISIBLE);
            });
        });
    }
}