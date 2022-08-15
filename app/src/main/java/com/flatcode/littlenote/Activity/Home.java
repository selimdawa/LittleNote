package com.flatcode.littlenote.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flatcode.littlenote.Model.Note;
import com.flatcode.littlenote.R;
import com.flatcode.littlenote.Unit.CLASS;
import com.flatcode.littlenote.Unit.DATA;
import com.flatcode.littlenote.Unit.THEME;
import com.flatcode.littlenote.Unit.VOID;
import com.flatcode.littlenote.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.text.MessageFormat;
import java.util.Objects;

public class Home extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityHomeBinding binding;

    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;

    private final Activity activity;
    private final Context context = activity = Home.this;

    private static final int SETTINGS_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .registerOnSharedPreferenceChangeListener(this);
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Color Mode ----------------------------- Start
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingFragment())
                .commit();
        // Color Mode -------------------------------- End

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        if (sharedPreferences.getString(DATA.COLOR_OPTION, "ONE").equals("ONE")) {
            binding.toolbar.mode.setBackgroundResource(R.drawable.sun);
        } else if (sharedPreferences.getString(DATA.COLOR_OPTION, "NIGHT_ONE").equals("NIGHT_ONE")) {
            binding.toolbar.mode.setBackgroundResource(R.drawable.moon);
        }

        if (Objects.requireNonNull(DATA.FIREBASE_USER).isAnonymous()) {
            binding.toolbar.info.setVisibility(View.GONE);
            binding.toolbar.sync.setVisibility(View.VISIBLE);
        } else {
            binding.toolbar.info.setVisibility(View.VISIBLE);
            binding.toolbar.sync.setVisibility(View.GONE);
        }

        Query query = DATA.FIREBASE_STORE.collection(DATA.PARENT_PATH).document(DATA.FirebaseUserUid).collection(DATA.CHILD_PATH).orderBy(DATA.TITLE, Query.Direction.DESCENDING);
        // query notes > uid > myNotes

        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i,
                                            @NonNull final Note note) {
                noteViewHolder.title.setText(note.getTitle());
                noteViewHolder.description.setText(note.getContent());
                final int code = DATA.getRandomColor();
                noteViewHolder.card.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(v -> VOID.IntentExtraDetails(context, CLASS.DETAILS,
                        DATA.TITLE, note, DATA.CONTENT, note, DATA.COLOR, code, DATA.ID_PATH, docId));

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(v -> {
                    final String docId1 = noteAdapter.getSnapshots().getSnapshot(i).getId();
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.setGravity(Gravity.END);
                    menu.getMenu().add(DATA.EDIT).setOnMenuItemClickListener(item -> {
                        VOID.IntentExtraDetails(context, CLASS.EDIT,
                                DATA.TITLE, note, DATA.CONTENT, note, null, 0, DATA.ID_PATH, docId);
                        return false;
                    });

                    menu.getMenu().add(DATA.DELETE).setOnMenuItemClickListener(item -> {
                        DocumentReference docRef = DATA.FIREBASE_STORE.collection(DATA.PARENT_PATH).document(DATA.FirebaseUserUid).collection(DATA.CHILD_PATH).document(docId1);
                        docRef.delete().addOnSuccessListener(aVoid -> {
                            // note deleted
                            int n = noteAdapter.getItemCount();
                            binding.toolbar.number.setText(MessageFormat.format(" ({0})", n));
                        }).addOnFailureListener(e -> Toast.makeText(context, R.string.error_delete, Toast.LENGTH_SHORT).show());
                        return false;
                    });

                    menu.show();
                });
                int n = noteAdapter.getItemCount();
                binding.toolbar.number.setText(MessageFormat.format(" ({0})", n));
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
                return new NoteViewHolder(view);
            }
        };

        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(noteAdapter);

        binding.toolbar.sync.setOnClickListener(v -> {
            if (DATA.FIREBASE_USER.isAnonymous()) {
                VOID.Intent(context, CLASS.LOGIN);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            } else {
                Toast.makeText(context, R.string.temporary_connect, Toast.LENGTH_SHORT).show();
            }
        });
        binding.toolbar.add.setOnClickListener(v -> {
            VOID.Intent(context, CLASS.ADD);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        });
        binding.toolbar.logout.setOnClickListener(v -> checkUser());
        binding.toolbar.info.setOnClickListener(v -> VOID.aboutAccount(context,
                DATA.FIREBASE_USER.getDisplayName(), DATA.FIREBASE_USER.getEmail()));
    }

    private void checkUser() {
        // if user is real or not
        if (Objects.requireNonNull(DATA.FIREBASE_USER).isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            VOID.Intent(context, CLASS.SPLASH);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(context)
                .setTitle(R.string.alert_delete_title)
                .setMessage(R.string.alert_delete_message)
                .setPositiveButton(R.string.alert_delete_positive, (dialog, which) -> {
                    VOID.Intent(context, CLASS.REGISTER);
                    finish();
                }).setNegativeButton(R.string.alert_delete_negative, (dialog, which) -> {
                    // ToDO: delete all the notes created by the Anon user
                    // TODO: delete the anon user
                    Objects.requireNonNull(DATA.FIREBASE_USER).delete().addOnSuccessListener(aVoid -> {
                        VOID.Intent(context, CLASS.SPLASH);
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        finish();
                    });
                });

        warning.show();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        View view;
        CardView card;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            card = itemView.findViewById(R.id.card);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        VOID.closeApp(context, activity);
    }

    // Color Mode ----------------------------- Start
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(DATA.COLOR_OPTION)) {
            this.recreate();
        }
    }

    public static class SettingFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_CODE) {
            this.recreate();
        }
    }
    // Color Mode -------------------------------- End
}