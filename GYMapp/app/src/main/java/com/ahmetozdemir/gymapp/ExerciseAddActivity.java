package com.ahmetozdemir.gymapp;

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

import androidx.appcompat.app.AppCompatActivity;

import com.ahmetozdemir.gymapp.databinding.ActivityExerciseAddBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ExerciseAddActivity extends AppCompatActivity
{
    private RequestQueue requestQueue;
    private ActivityExerciseAddBinding binding;
    private int bodyPartID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Button showPopupButton = findViewById(R.id.bodyPart);
        showPopupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPopup(view);
            }
        });
        requestQueue = Volley.newRequestQueue(this); // RequestQueue'yu oluştur
    }

    private void showPopup(View anchorView)
    {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Popup içindeki butonları tanımla
        Button btnPopup1 = popupView.findViewById(R.id.btnPopup1);
        Button btnPopup2 = popupView.findViewById(R.id.btnPopup2);
        Button btnPopup3 = popupView.findViewById(R.id.btnPopup3);
        Button btnPopup4 = popupView.findViewById(R.id.btnPopup4);
        Button btnPopup5 = popupView.findViewById(R.id.btnPopup5);
        Button btnPopup6 = popupView.findViewById(R.id.btnPopup6);

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
                if("Chest".equals(buttonText)) bodyPartID = 1;
                if("Back".equals(buttonText)) bodyPartID = 2;
                if("Leg".equals(buttonText)) bodyPartID = 3;
                if("Six-pack".equals(buttonText)) bodyPartID = 4;
                if("Shoulder".equals(buttonText)) bodyPartID = 5;
                if("Arm".equals(buttonText)) bp1 = 6;
                Log.e("ExAd", "ID: "+ String.valueOf(bodyPartID) );
                Log.e("ExAd", "textbutton:" + buttonText );
                binding.bodyPart.setText(buttonText);
                Toast.makeText(ExerciseAddActivity.this, "Selected: " + buttonText, Toast.LENGTH_SHORT).show();
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
        // popupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, 50, 100);
    }

    public void saveChanges(View view)
    {
        String exerciseName = binding.exerciseName.getText().toString();
        String exerciseLink= binding.ExerciseLink.getText().toString();
        String photoLink = binding.photoLink.getText().toString();

        if("".equals(exerciseName) || "".equals(exerciseLink) || "".equals(photoLink))
        {
            Toast.makeText(this, "Fill all the space " + exerciseName, Toast.LENGTH_SHORT).show();
        }

        else if(bodyPartID == 0)
        {
            Toast.makeText(this, "Select a body part", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Intent resultIntent = new Intent(ExerciseAddActivity.this, BasePage.class);

            SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
            String storedEmail = preferences.getString("email", "");
            String url = "http://212.20.147.47:7238/api/exercise/addexercise"; // API endpoint'i

            // JSON request body'si oluştur
            JSONObject jsonBody = new JSONObject();
            try
            {
                jsonBody.put("exerciseName", exerciseName);
                jsonBody.put("videoLink", exerciseLink);
                jsonBody.put("exerciseImage", photoLink);
                jsonBody.put("bodyPartID", bodyPartID);
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
                    Toast.makeText(this, "Kayıtedildi", Toast.LENGTH_SHORT).show();
                },

                error ->
                {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                    Log.e("SignInPage", "Volley Error:Tarsuslu " + error.toString());

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

    public void goProfile(View view)
    {
        Intent intent = new Intent(ExerciseAddActivity.this, ProfilePage.class);
        startActivity(intent);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(ExerciseAddActivity.this, BasePage.class);
        startActivity(intent);
    }
}