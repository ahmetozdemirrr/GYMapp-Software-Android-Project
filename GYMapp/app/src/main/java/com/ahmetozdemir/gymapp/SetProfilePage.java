package com.ahmetozdemir.gymapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.SetProfilePageBinding;

public class SetProfilePage extends AppCompatActivity
{
    private SetProfilePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = SetProfilePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    public void goEditProfile(View view)
    {
        Intent intent = new Intent(SetProfilePage.this, EditProfile.class);
        startActivity(intent);
    }

    public void exitProgram(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(SetProfilePage.this);

        alert.setTitle("Exit Program");
        alert.setMessage("Are you sure exit program?");

        alert.setNegativeButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(SetProfilePage.this, "Bye", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        });

        alert.setPositiveButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Toast.makeText(SetProfilePage.this, "Hala buradayız", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    public void exitAccount(View view)
    {
        goMain();
        clearToken();
        finishAffinity();
    }

    public void goMain() // Şifre değiştirme başarılıysa ana sayfaya geri dön
    {
        Intent intent = new Intent(SetProfilePage.this, MainActivityGYMAPP.class);
        startActivity(intent);
    }
    private void clearToken() // Eğer exit tuşuna basılırsa bir dahaki girişte yine login istenilsin
    {
        // SharedPreferences kullanarak token'ı silme
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.apply();
    }
}
