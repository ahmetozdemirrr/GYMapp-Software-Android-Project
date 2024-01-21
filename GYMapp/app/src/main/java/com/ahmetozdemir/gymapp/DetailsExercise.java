package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.ActivityDetailsBinding;

public class DetailsExercise extends AppCompatActivity
{
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();

        ExerciseModel selectedExercise = (ExerciseModel) intent.getSerializableExtra("exercise");

        binding.nameText.setText(selectedExercise.getExerciseName());
        binding.countryText.setText(selectedExercise.getExerciseDescription());
        // Eğer resim kullanıyorsanız:
        // binding.imageView4.setImageResource(selectedExercise.getImageResource());

        //getSupportActionBar().setTitle("EXERCISE DETAILS"); // Burası action barda yazılacak yazıyı ayarlar
    }

    public void goProfile(View view)
    {
        Intent intent = new Intent(DetailsExercise.this, ProfilePage.class);
        startActivity(intent);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(DetailsExercise.this, BasePage.class);
        startActivity(intent);
    }
}
