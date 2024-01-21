package com.ahmetozdemir.gymapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetozdemir.gymapp.databinding.ExerciseBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Exercise extends AppCompatActivity
{
    private static final String API_URL = "http://212.20.147.47:7238/api/Exercise/getallexercises";
    private ExerciseAdapter exerciseAdapter;
    private ProgressBar progressBar;
    private ArrayList<ExerciseModel> exerciseArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ExerciseBinding binding;
        RecyclerView recyclerView;

        super.onCreate(savedInstanceState);

        binding = ExerciseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        progressBar = binding.progressBar;

        exerciseArrayList = new ArrayList<>();
        // RecyclerView ve Adapter'ı oluştur
        recyclerView = binding.recyclerView;
        exerciseAdapter = new ExerciseAdapter(exerciseArrayList);

        // LinearLayoutManager ayarla
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        // RecyclerView'e LayoutManager ve Adapter'ı ayarla
        recyclerView.setLayoutManager(linearLayoutManager);
        fetchDataFromApi();
        recyclerView.setAdapter(exerciseAdapter);
    }
    public void goProfile(View view)
    {
        Intent intent = new Intent(Exercise.this, ProfilePage.class);
        startActivity(intent);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(Exercise.this, BasePage.class);
        startActivity(intent);
    }

    private void fetchDataFromApi()
    {
        progressBar.setVisibility(View.VISIBLE); // API çağrısı başladığında ProgressBar'u görünür yap
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,

        response ->
        {
            try
            {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("JSONObject", "Key: " + ", Value:egzersiz" + jsonObject);

                    String exerciseName = jsonObject.getString("exerciseName");
                    String exerciseDescription = jsonObject.getString("exerciseDescription");
                    String exerciseLink = jsonObject.getString("videoLink");
                    String exerciseIDString = jsonObject.getString("id");
                    int exerciseID = Integer.parseInt(exerciseIDString);
                    Log.d("JSONObject", "Keyadapter 1 " + ", Value: " + exerciseLink + "deneme " + exerciseName) ;

                    ExerciseModel exercise = new ExerciseModel(exerciseName,exerciseDescription,exerciseLink,exerciseID);
                    exerciseArrayList.add(exercise);
                }
                exerciseAdapter.notifyDataSetChanged();
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

            finally
            {
                progressBar.setVisibility(View.INVISIBLE); // API çağrısı tamamlandığında ProgressBar'u gizle
            }
        },

        error ->
        {
            Toast.makeText(this, "Error fetching data from API", Toast.LENGTH_SHORT).show();
            Log.e("ExerciseActivity", "Volley Error: " + error.toString());

            if (error.networkResponse != null)
            {
                Log.e("ExerciseActivity", "Error Response Code: " + error.networkResponse.statusCode);

                try
                {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Log.e("ExerciseActivity", "Error Response Body: " + responseBody);
                }

                catch (Exception e)
                {
                    Log.e("ExerciseActivity", "Error parsing response body: " + e.getMessage());
                }
            }
            progressBar.setVisibility(View.INVISIBLE); // Hata durumunda ProgressBar'u gizle
        });
        // RequestQueue işlemi
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}