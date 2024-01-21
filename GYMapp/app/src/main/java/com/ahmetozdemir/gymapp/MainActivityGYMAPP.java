package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.MainActivityGymappBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivityGYMAPP extends AppCompatActivity
{
    private MainActivityGymappBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (isTokenValid()) // Token kontrolü yap
        {
            Intent intent = new Intent(this, BasePage.class); // Token geçerliyse (süre olarak), BasePage'e yönlendir
            startActivity(intent);
            finish(); // MainActivity'yi kapat
        }

        else // Token geçerli değilse, normal akışa devam et
        {
            // Data binding ile XML dosyasını bağlıyoruz
            binding = MainActivityGymappBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
        }
    }

    public void signIn(View view)
    {
        Intent intent = new Intent(MainActivityGYMAPP.this, SignInPage.class);
        startActivity(intent);
    }

    public void signUp(View view)
    {
        Intent intent = new Intent(MainActivityGYMAPP.this, RegisterPage.class);
        startActivity(intent);
    }

    private boolean isTokenValid()
    {
        // Token'ın geçerliliğini kontrol etmek için gerekli kod
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String token = preferences.getString("token", null);

        if (token != null) {
            Log.d("TokenDebug", "Token found: " + token);
            return isTokenNotExpired(token);
        } else {
            Log.d("TokenDebug", "No token found");
            return false;
        }
    }

    public static boolean isTokenNotExpired(String token)
    {
        try
        {
            String[] tokenParts = token.split("\\."); // Token'ı decode et
            String payload = tokenParts[1]; // Token'ın payload kısmını al

            // Base64 decode işlemi
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);

            JSONObject jsonPayload = new JSONObject(decodedPayload); // JSON olarak parse et

            // Expiration (exp) tarihini al
            if (jsonPayload.has("exp"))
            {
                long expirationTime = jsonPayload.getLong("exp") * 1000; // saniye cinsinden olduğu için 1000 ile çarptık
                long currentTime = System.currentTimeMillis(); // Şuanki zamanı al
                return currentTime < expirationTime; // Token'ın geçerliliğini kontrol et
            }

            else
            {
                return false; // "exp" alanı yoksa, token geçerli değil kabul edilebilir
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return false; // Token parse edilemezse veya hata oluşursa false döndür
        }
    }
}
