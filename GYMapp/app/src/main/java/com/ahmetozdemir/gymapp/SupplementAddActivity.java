package com.ahmetozdemir.gymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetozdemir.gymapp.databinding.ActivitySupplementAddBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SupplementAddActivity extends AppCompatActivity
{
    private ActivitySupplementAddBinding binding;

    Button showPopupButton;
    TextView desc;
    TextView link;
    TextView name;
    private RequestQueue requestQueue;
    int catID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySupplementAddBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_supplement_add);
        showPopupButton = findViewById(R.id.bodyPart);
        desc = findViewById(R.id.exerciseName);
        name = findViewById(R.id.ExerciseLink);
        link = findViewById(R.id.photoLink);

        showPopupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        requestQueue = Volley.newRequestQueue(this); // RequestQueue'yu oluştur
    }
    public void saveChangesSupp(View view)
    {
        String description = desc.getText().toString();
        String supplementName= name.getText().toString();
        String photoLink = link.getText().toString();

        if("".equals(description) || "".equals(supplementName) || "".equals(photoLink))
        {
            Toast.makeText(this, "Fill all the space " + supplementName, Toast.LENGTH_SHORT).show();

        }

        else if(catID == 0)
        {
            Toast.makeText(this, "Select a supplement type", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Intent resultIntent = new Intent(SupplementAddActivity.this, BasePage.class);

            SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
            String storedEmail = preferences.getString("email", "");

            String url = "http://212.20.147.47:7238/api/Supplement/addsupplement"; // API endpoint'i

            // JSON request body'si oluştur
            JSONObject jsonBody = new JSONObject();
            Log.e("ExAd", "catıd" + catID);

            try
            {
                jsonBody.put("title", supplementName);
                jsonBody.put("description", description);
                jsonBody.put("image", photoLink);
                jsonBody.put("categoryID", catID);
            }

            catch (JSONException e)
            {
                Log.d("SignInPage", "asdas" ); // Yanıtı logla
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (
                Request.Method.POST, url, jsonBody,

                response ->
                {
                    Log.d("SignInPage", "Response: " + response.toString()); // Yanıtı logla
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                },

                error ->
                {
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
        }
    }

    private void showPopup(View anchorView)
    {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout_supp, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Popup içindeki butonları tanımla
        Button btnPopup1 = popupView.findViewById(R.id.suppPopup1);
        Button btnPopup2 = popupView.findViewById(R.id.suppPopup2);
        Button btnPopup3 = popupView.findViewById(R.id.suppPopup3);
        Button btnPopup4 = popupView.findViewById(R.id.suppPopup4);
        Button btnPopup5 = popupView.findViewById(R.id.suppPopup5);
        Button btnPopup6 = popupView.findViewById(R.id.suppPopup6);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Popup dışına tıklandığında dismiss olacak
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                // Toast.makeText(ExerciseAddActivity.this, "Selected: null", Toast.LENGTH_SHORT).show();
            }
        });

        // Her butona tıklandığında
        View.OnClickListener popupClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int bp1 = 0;
                TextView selectedTextView = (TextView) view;
                String buttonText = selectedTextView.getText().toString();
                if("Protein".equals(buttonText)) catID = 1;
                if("BCA".equals(buttonText)) catID = 2;
                if("Preworkout".equals(buttonText)) catID = 3;
                if("Kreatin".equals(buttonText)) catID = 4;
                if("Postworkout".equals(buttonText)) catID = 5;
                if("Vitaminler".equals(buttonText)) catID = 6;
                Log.e("ExAd", "ID: "+ String.valueOf(catID) );
                Log.e("ExAd", "textbutton:" + buttonText );
                showPopupButton.setText(buttonText);
                Toast.makeText(SupplementAddActivity.this, "Selected: " + buttonText, Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        };

        btnPopup1.setOnClickListener(popupClickListener);
        btnPopup2.setOnClickListener(popupClickListener);
        btnPopup3.setOnClickListener(popupClickListener);
        btnPopup4.setOnClickListener(popupClickListener);
        btnPopup5.setOnClickListener(popupClickListener);
        btnPopup6.setOnClickListener(popupClickListener);
        // Popup penceresini aç
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, -300); // Eğer tasarımsal bir boyut sıkıntısı çıkarsa -300 ü 0 yap
    }
}



