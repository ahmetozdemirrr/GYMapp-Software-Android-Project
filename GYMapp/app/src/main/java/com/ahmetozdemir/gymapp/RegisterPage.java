package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ahmetozdemir.gymapp.databinding.RegisterPageBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class RegisterPage extends AppCompatActivity
{
    private RegisterPageBinding binding; // xml bağlama için
    private RequestQueue requestQueue; // RequestQueue'yu sınıf düzeyinde tanımla

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = RegisterPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // RequestQueue'yu oluştur
        requestQueue = Volley.newRequestQueue(this);

        ImageView passwordToggle = binding.imageButtonShowPassword;
        EditText passwordEditText = binding.editTextNumberPassword2;

        //Şifreyi göster/gizle işlevselliini yöneten onClick
        passwordToggle.setOnClickListener(new View.OnClickListener()
        {
            boolean passwordVisible = true;

            @Override
            public void onClick(View v)
            {
                passwordVisible = !passwordVisible;

                if (passwordVisible)
                {
                    // Şifreyi göster
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                else
                {
                    // Şifreyi gizle
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Göz ikonuna tıklandığında imleç sağ tarafta kalsın diye aşağıdaki satır eklendi
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        //"SIGN UP" butonuna basıldığında buraya giriyoruz
        binding.button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Kullanıcı adı, şifre ve mail adresini al
                String userName = binding.editText.getText().toString();
                String password = binding.editTextNumberPassword2.getText().toString();
                String email = binding.editTextTextPassword2.getText().toString();

                // 1- Kullanıcı adı 3 ile 20 karakter arasında olmalı
                if (userName.length() < 3 || userName.length() > 20)
                {
                    // Hata durumu, kullanıcıya uyarı mesajı gösterilebilir
                    Toast toast = Toast.makeText(getApplicationContext(), "User name must be between 3 and 20 characters", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0); // toast mesajları yukarıya alındı
                    toast.show();
                    return;
                }

                // 2- Şifre kontrolü (en az bir büyük harf, bir digit, en az 5 karakter uzunluğu)
                if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{5,}$"))
                {
                    // Hata durumu, kullanıcıya uyarı mesajı gösterilebilir
                    Toast toast = Toast.makeText(getApplicationContext(), "The password must contain at least one capital letter, one digit and be at least 5 characters long", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0); // toast mesajları yukarıya alındı
                    toast.show();
                    return;
                }

                // 3- Mail uzantısı kontrolü (gmail)
                if (!email.endsWith("@gmail.com"))
                {
                    // Hata durumu, kullanıcıya uyarı mesajı gösterilebilir
                    Toast toast = Toast.makeText(getApplicationContext(), "A valid Gmail address must be entered", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0); // toast mesajları yukarıya alındı
                    toast.show();
                    return;
                }

                // Bilgileri API'ye yollamak için bir RegisterModel objesi oluşturuyoruz
                RegisterModel registerModel = new RegisterModel(userName, email, password);
                // API'ye kayıt bilgilerini gönderiyoruz
                sendPostRequest(registerModel);
            }
        });
    }

    private void sendPostRequest(RegisterModel registerModel)
    {
        Log.e("RegisterPage", "Mail : " + registerModel.getEmail());
        String url = "http://212.20.147.47:7096/api/Authentication/Register"; // API endpoint'i

        // JSON request body'sini oluştur
        JSONObject jsonBody = new JSONObject();
        try
        {
            // Kullanıcı adı, şifre ve email'i JSON objesine ekle
            jsonBody.put("username", registerModel.getUserName());
            jsonBody.put("password", registerModel.getPassword());
            jsonBody.put("email", registerModel.getEmail());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        // JSON objesi yerine JSON string'i kullanacağız
        JsonRequest<JSONObject> jsonObjectRequest = new JsonRequest<JSONObject>
        (
            Request.Method.POST,
            url,
            jsonBody.toString(),  // JSON string'i burada kullanıyoruz

            response ->
            {
                Log.e("RegisterPage","JSON : " + jsonBody);
                // Başarılı yanıt
                Toast.makeText(this, "Email sended", Toast.LENGTH_SHORT).show();
                Log.d("RegisterPage", "API call successful: " + response.toString());
                goMain();
            },

            error ->
            {
                // Hata durumu
                Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show();
                Log.e("RegisterPage", "API call failed: " + error.toString());

                // Hata detayı
                if (error.networkResponse != null)
                {
                    Log.e("RegisterPage", "Error Response Code: " + error.networkResponse.statusCode);

                    try
                    {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("RegisterPage", "Error Response Body: " + responseBody);
                    }

                    catch (Exception e)
                    {
                        Log.e("RegisterPage", "Error parsing response body: " + e.getMessage());
                    }
                }
            }
        )

        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
            {
                try
                {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                }

                catch (UnsupportedEncodingException | JSONException e)
                {
                    return Response.error(new ParseError(e));
                }
            }
        };
        // Retry policy'yi ayarla
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Request'ı kuyruğa ekle
        requestQueue.add(jsonObjectRequest);
    }

    public void goMain() // Register başarılıysa ana sayfaya geri dön
    {
        Intent intent = new Intent(RegisterPage.this, MainActivityGYMAPP.class);
        startActivity(intent);
    }
}