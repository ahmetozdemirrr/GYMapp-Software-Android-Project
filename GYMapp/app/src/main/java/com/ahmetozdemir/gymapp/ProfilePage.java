package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.ProfilePageBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class ProfilePage extends AppCompatActivity
{
    private ProfilePageBinding binding;
    private RequestQueue requestQueue;
    String email; // Profil bilgilerini getirmek için kaydettiğimiz kullanıcı adıyla apiye istek atacağız

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ProfilePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        email = getEmailFromSharedPreferences();
        requestQueue = Volley.newRequestQueue(this); // RequestQueue'yu oluştur
        fetchDataFromApi(); // Verileri çek ve TextView'lara yerleştir
    }

    private String getEmailFromSharedPreferences()
    {
        // SharedPreferences kullanarak kullanıcı adını alma
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        return preferences.getString("email", "");
    }

    private void fetchDataFromApi()
    {
        String url = "http://212.20.147.47:7096/api/Authentication/getprofile"; // API endpoint'i

        url += "?email=" + email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (
            Request.Method.GET, url, null,

                response ->
                {
                    Log.e("ProfilePage", "Content : " + response.toString());

                    String userName = response.optString("userName", ""); // UserName değeri null ise boş string olarak alır
                    String height = response.optString("height", ""); // Height değeri null ise boş string olarak alır
                    String weight = response.optString("weight", ""); // Weight değeri null ise boş string olarak alır
                    String gender = response.optString("gender", ""); // gender değeri null ise boş string olarak alır
                    String email = response.optString("email", ""); // email değeri null ise boş string olarak alır
                    String phoneNumber = response.optString("telephone", ""); // Telephone değeri null ise boş string olarak alır

                    runOnUiThread(() ->
                    {
                        binding.textViewName.setText(userName);
                        binding.textViewWeight.setText(weight);
                        binding.textViewHeight.setText(height);
                        binding.textViewGender.setText(gender);
                        binding.textViewMail.setText(email);
                        binding.textViewTelephone.setText(phoneNumber);

                        // Kiloyu ve boyu double türüne çevirme (deneme amaçlı kendi değerlerimi koydum yoksa null olduğu için uygulama çöküyor)
                        double weightValue = 65/* Double.parseDouble(weight) */;
                        double heightValue = 1.73/* Double.parseDouble(height) */;

                        // BMI hesaplaması
                        double bmi = weightValue / (heightValue * heightValue);
                        String formattedBMI = String.format("%.2f", bmi);
                        binding.textViewMassIndex.setText(formattedBMI);
                    });
                },

                error ->
                {
                    Toast.makeText(ProfilePage.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    Log.e("ProfilePage", "Volley Error: " + error.toString());

                    if (error.networkResponse != null)
                    {
                        Log.e("ProfilePage", "Error Response Code: " + error.networkResponse.statusCode);

                        try
                        {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e("ProfilePage", "Error Response Body: " + responseBody);
                        }

                        catch (Exception e)
                        {
                            Log.e("ProfilePage", "Error parsing response body: " + e.getMessage());
                        }
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(ProfilePage.this, BasePage.class);
        startActivity(intent);
    }

    public void goSettings(View view)
    {
        Intent intent = new Intent(ProfilePage.this, SetProfilePage.class);
        startActivity(intent);
    }
}
