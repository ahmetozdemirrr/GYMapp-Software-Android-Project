package com.ahmetozdemir.gymapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.BasePageBinding;

public class BasePage extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        BasePageBinding binding;
        super.onCreate(savedInstanceState);

        // Ana sayfada geri tuşuna basıldığında logine gitmesini engelliyoruz
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(BasePage.this);

                alert.setTitle("Exit Program");
                alert.setMessage("Are you sure exit program?");

                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(BasePage.this, "Bye", Toast.LENGTH_SHORT).show();
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
        });

        // Data binding ile XML dosyasını bağlıyoruz
        binding = BasePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Log.d("BasePage", "onCreate: BasePage oluşturuldu.");
    }

    public void goProfile(View view)
    {
        // Log.d("BasePage", "goProfile: Profil sayfasına git butonuna basıldı.");
        Intent intent = new Intent(BasePage.this, ProfilePage.class);
        startActivity(intent);
    }

    public void goSupplements(View view)
    {
        // Log.d("BasePage", "goSupplements: Supplement sayfasına git butonuna basıldı.");

        if (isAdminUser())
        {
            // Log.d("BasePage", "goSupplements: Admin kullanıcısı, AdminSupplement sayfasına gidiliyor.");
            Intent intent = new Intent(BasePage.this, SupplementActivityAdmin.class);
            startActivity(intent);
        }

        else
        {
            // Log.d("BasePage", "goSupplements: Admin kullanıcısı değil, SupplementActivity sayfasına gidiliyor.");
            Intent intent = new Intent(BasePage.this, SupplementActivity.class);
            startActivity(intent);
        }
    }

    public void goExercises(View view)
    {
        // Log.d("BasePage", "goExercises: Egzersiz sayfasına git butonuna basıldı.");

        if (isAdminUser())
        {
            // Log.d("BasePage", "goExercises: Admin kullanıcısı, AdminExercise sayfasına gidiliyor.");
            Intent intent = new Intent(BasePage.this, ExerciseAdmin.class);
            startActivity(intent);
        }

        else
        {
            // Log.d("BasePage", "goExercises: Admin kullanıcısı değil, Exercise sayfasına gidiliyor.");
            Intent intent = new Intent(BasePage.this, Exercise.class);
            startActivity(intent);
        }
    }

    private boolean isAdminUser()
    {
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String userRole = preferences.getString("userRole", "");

        // Log.d("BasePage", "isAdminUser: Kullanıcının rolü: " + userRole);

        return "Admin".equals(userRole);
    }
}
