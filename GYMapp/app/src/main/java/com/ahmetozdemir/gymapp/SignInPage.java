package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ahmetozdemir.gymapp.databinding.SignInPageBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class SignInPage extends AppCompatActivity
{
    private SignInPageBinding binding;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private boolean passwordVisible = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = SignInPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        requestQueue = Volley.newRequestQueue(this); // RequestQueue'yu oluştur
        progressBar = binding.progressBar;

        ImageView passwordToggle = binding.imageButtonShowPassword;
        EditText passwordEditText = binding.editTextNumberPassword2;

        passwordToggle.setOnClickListener(v ->
        {
            passwordVisible = !passwordVisible;

            if (passwordVisible)
            {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            else
            {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }

    public void goHome(View view) // Burada LoginModel kullanılarak API'ye istek gönderilecek
    {
        String userName = binding.editTextTextPassword2.getText().toString();
        String password = binding.editTextNumberPassword2.getText().toString();

        saveUserNameToSharedPreferences(userName);
        LoginModel loginModel = new LoginModel(userName, password);
        sendPostRequest(loginModel);
    }

    public void goToHomePage()
    {
        Intent intent = new Intent(SignInPage.this, BasePage.class);
        startActivity(intent);
    }

    public void forgotPassword(View view)
    {
        Intent intent = new Intent(SignInPage.this, ForgotPasswordPage.class);
        startActivity(intent);
    }

    private void sendPostRequest(LoginModel loginModel)
    {
        progressBar.setVisibility(View.VISIBLE); // API çağrısı başladığında ProgressBar'u görünür yap
        String url = "http://212.20.147.47:7096/api/Authentication/login"; // API endpoint'i

        // JSON request body'si oluştur
        JSONObject jsonBody = new JSONObject();

        Log.d("SignInPage","Login Model : " + loginModel.getPassword() + loginModel.getUserName());

        try
        {
            jsonBody.put("userName", loginModel.getUserName());
            jsonBody.put("password", loginModel.getPassword());
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
                try
                {
                    Log.d("SignInPage", "Response: " + response.toString()); // Yanıtı logla

                    String accessToken = response.getString("token"); // tekrar tekrar giriş yapılmaması için token çekiyoruz.
                    String secretKey = response.getString("secretKey"); // API tarafından gönderilen secretKey'i de alıyoruz

                    Log.e("SignInPage", "Token : " + accessToken);
                    Log.e("SignInPage", "Key : " + secretKey);

                    parseJwtToken(accessToken, secretKey);
                    Toast.makeText(this, "Login Succesful", Toast.LENGTH_SHORT).show();
                    goToHomePage();
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("SignInPage", "Error parsing response body: " + e.getMessage());
                }

                finally
                {
                    progressBar.setVisibility(View.INVISIBLE); // API çağrısı tamamlandığında ProgressBar'u gizle
                }
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
                progressBar.setVisibility(View.INVISIBLE); // API çağrısı tamamlandığında ProgressBar'u gizle
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
    }

    private void parseJwtToken(String token, String secretKey)
    {
        try
        {
            byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);  // API tarafında kullanılan gizli anahtar

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                        .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes))
                                        .setAllowedClockSkewSeconds(5) // 5 dakika sapma ayarı
                                        .build()
                                        .parseClaimsJws(token);

            String userRole = claimsJws.getBody().get("Role", String.class);
            String email = claimsJws.getBody().get("email", String.class);

            saveUserRoleToSharedPreferences(userRole); // Kullanıcının rol bilgisini SharedPreferences'e kaydediyoruz
            saveTokenToSharedPreferences(token); // Token'ı kaydediyoruz
            saveUserEmail(email);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("SignInPage", "Error parsing JWT token: " + e.getMessage());
        }
    }

    private void saveUserRoleToSharedPreferences(String userRole) // SharedPreferences kullanarak rolü kaydetme
    {
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userRole", userRole);
        editor.apply();
    }

    private void saveTokenToSharedPreferences(String token) // SharedPreferences kullanarak token'ı kaydetme
    {
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void saveUserNameToSharedPreferences(String userName) // SharedPreferences kullanarak kullanıcı adını kaydetme
    {
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", userName);
        editor.apply();
    }

    private void saveUserEmail(String userEmail) // SharedPreferences kullanarak email'i kaydetme
    {
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", userEmail);
        editor.apply();
    }
}