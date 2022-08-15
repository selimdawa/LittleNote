package com.flatcode.littlenote.Activity;

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
import com.flatcode.littlenote.databinding.ActivityAddEditNoteBinding;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    private ActivityAddEditNoteBinding binding;

    private Context context = AddNote.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.add_note);

        binding.toolbar.image.setVisibility(View.VISIBLE);
        binding.toolbar.image.setImageResource(R.drawable.ic_true);

        binding.toolbar.image.setOnClickListener(v -> {
            String nTitle = binding.noteTitle.getText().toString();
            String nContent = binding.noteContent.getText().toString();

            if (nTitle.isEmpty() || nContent.isEmpty()) {
                Toast.makeText(context, R.string.error_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            // save note
            DocumentReference document = DATA.FIREBASE_STORE.collection(DATA.PARENT_PATH).document(DATA.FirebaseUserUid).collection(DATA.CHILD_PATH).document();
            Map<String, Object> note = new HashMap<>();
            note.put(DATA.TITLE, nTitle);
            note.put(DATA.CONTENT, nContent);

            document.set(note).addOnSuccessListener(aVoid -> {
                Toast.makeText(context, R.string.success_note_add, Toast.LENGTH_SHORT).show();
                VOID.Intent(context, CLASS.HOME);
                onBackPressed();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, R.string.error_empty, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.VISIBLE);
            });
        });
    }
}