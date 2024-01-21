package com.ahmetozdemir.gymapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.EditProfileBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class EditProfile extends AppCompatActivity
{
    private RequestQueue requestQueue;
    private EditProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = EditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        requestQueue = Volley.newRequestQueue(this); // RequestQueue'yu oluştur
    }

    public void saveChanges(View view)
    {
        String height = binding.nameAndSurname.getText().toString();
        String gender = binding.age.getText().toString();
        String weight = binding.weight.getText().toString();
        String phoneNumber = binding.phoneNumber.getText().toString();

        Intent resultIntent = new Intent(EditProfile.this, BasePage.class);

        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String storedEmail = preferences.getString("email", "");

        String url = "http://212.20.147.47:7096/api/Authentication/setprofile"; // API endpoint'i

        // JSON request body'si oluştur
        JSONObject jsonBody = new JSONObject();
        try
        {
            jsonBody.put("Email", storedEmail);
            jsonBody.put("Telephone", phoneNumber);
            jsonBody.put("Weight", weight);
            jsonBody.put("Gender", gender);
            jsonBody.put("Height", height);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (
            Request.Method.POST, url, jsonBody,

            response ->
            {
                Log.d("SignInPage", "Response: " + response.toString()); // Yanıtı logla

                Toast.makeText(this, "Saved Changes", Toast.LENGTH_SHORT).show();
            },

            error ->
            {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                Log.e("SignInPage", "Volley Error: " + error.toString());

                if (error.networkResponse != null)
                {
                    Log.e("SignInPage", "Error Response Code: " + error.networkResponse.statusCode);

                    try
                    {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("SignInPage", "Error Response Body: " + responseBody);
                    }

                    catch (Exception e)
                    {
                        Log.e("SignInPage", "Error parsing response body: " + e.getMessage());
                    }
                }
            }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy
            (
                    3000,  // timeout in milliseconds
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        );
        requestQueue.add(jsonObjectRequest);

        setResult(Activity.RESULT_OK, resultIntent);
        startActivity(resultIntent);
    }

    public void goProfile(View view)
    {
        Intent intent = new Intent(EditProfile.this, ProfilePage.class);
        startActivity(intent);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(EditProfile.this, BasePage.class);
        startActivity(intent);
    }
}
