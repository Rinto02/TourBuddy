package com.tourbuddy.tourbuddy;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.tourbuddy.tourbuddy.databinding.ActivityUserHomeBinding;

import java.util.Locale;

public class UserHomeActivity extends AppCompatActivity {
    private ActivityUserHomeBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Load the language from Firestore and set the app locale
        loadLanguageData();
    }

    private void replaceFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, selectedFragment)
                .commit();
    }

    private void loadLanguageData() {
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            String userId = mUser.getUid();

            // Reference to the Firestore document
            DocumentReference userDocumentRef = db.collection("users").document(userId);

            // Retrieve data from Firestore
            userDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("language"))
                            setAppLocale(documentSnapshot.getString("language"));
                        fragmentManager();


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("DATABASE", "Error getting document", e);
                }
            });


        }
    }

    private void setAppLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    private void fragmentManager(){
        // Inflate the layout after setting the locale
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the default fragment when the activity is created
        replaceFragment(new ProfileFragment());

        // Set the listener for BottomNavigationView
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        // Set the default selected item to be "Profile"
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Check if the selected item is already the one corresponding to the current fragment
            if (item.getItemId() == R.id.search && !(getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof SearchFragment))
                replaceFragment(new SearchFragment());
            else if (item.getItemId() == R.id.chat && !(getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof ChatFragment))
                replaceFragment(new ChatFragment());
            else if (item.getItemId() == R.id.profile && !(getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof ProfileFragment))
                replaceFragment(new ProfileFragment());
            else if (item.getItemId() == R.id.settings && !(getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof SettingsFragment))
                replaceFragment(new SettingsFragment());

            return true;
        });

    }
}
