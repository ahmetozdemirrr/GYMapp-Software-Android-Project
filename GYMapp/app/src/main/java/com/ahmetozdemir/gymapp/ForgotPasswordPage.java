package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ahmetozdemir.gymapp.databinding.ForgotPasswordPageBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class ForgotPasswordPage extends AppCompatActivity
{
    private ForgotPasswordPageBinding binding;
    private RequestQueue requestQueue;
    private boolean passwordVisible = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ForgotPasswordPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // RequestQueue'yu oluştur
        requestQueue = Volley.newRequestQueue(this);

        binding.button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Butona tıklanıldığında çağrılacak işlemleri buraya yazın
                changePassword(v);
            }
        });
    }

    public void changePassword(View view)
    {
        // Burada şifre değiştirme işlemleri gerçekleştirilecek
        String email = binding.editTextNumberPassword2.getText().toString();
        // Şifre değiştirme isteğini API'ye gönder
        sendPasswordChangeRequest(email);
    }

    private void sendPasswordChangeRequest(String email)
    {
        String url = "http://212.20.147.47:7096/api/Authentication/reset"; // API endpoint'i

        // JSON request body'si oluştur
        JSONObject jsonBody = new JSONObject();
        try
        {
            jsonBody.put("email", email);
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
                    // Başarılı yanıt
                    Log.d("ForgotPasswordPage", "Response: " + response.toString());
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    goMain();
                },

                error ->
                {
                    // Hata durumu
                    Toast.makeText(this, "Error sending reset email", Toast.LENGTH_SHORT).show();
                    Log.e("ForgotPasswordPage", "Volley Error: " + error.toString());

                    if (error.networkResponse != null)
                    {
                        Log.e("ForgotPasswordPage", "Error Response Code: " + error.networkResponse.statusCode);

                        try
                        {
                            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e("ForgotPasswordPage", "Error Response Body: " + responseBody);
                        }

                        catch (Exception e)
                        {
                            Log.e("ForgotPasswordPage", "Error parsing response body: " + e.getMessage());
                        }
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy
        (
                3000,  // timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(jsonObjectRequest);
    }

    public void goMain() // Şifre değiştirme başarılıysa ana sayfaya geri dön
    {
        Intent intent = new Intent(ForgotPasswordPage.this, MainActivityGYMAPP.class);
        startActivity(intent);
    }
}
