package com.ahmetozdemir.gymapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetozdemir.gymapp.databinding.ActivitySupplementAdminBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SupplementActivityAdmin extends AppCompatActivity
{
    int spantCount = 2;
    int oriantation = RecyclerView.VERTICAL;
    boolean reverseLayout = false;
    SupplementAdapterAdmin supplementAdapter;

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this,spantCount,oriantation,reverseLayout);
    ArrayList<Supplement> supplementArrayList;
    private static final String API_URL = "http://212.20.147.47:7238/api/Supplement/getallsupplements";
    private ActivitySupplementAdminBinding binding;
    private RecyclerView recyclerView1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivitySupplementAdminBinding.inflate(getLayoutInflater());
        Log.d("SignInPage", "Tarsuslu supp "); // Yanıtı logla
        View view = binding.getRoot();
        setContentView(view);

        supplementArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        supplementAdapter = new SupplementAdapterAdmin(supplementArrayList,this);

        binding.recyclerView.setAdapter(supplementAdapter);
        Log.d("SignInPage", "Tarsuslu supp1 "); // Yanıtı logla
        fetchDataFromApi();
        Log.d("SignInPage", "Tarsuslu supp2 "); // Yanıtı logla
    }

    public void goProfile(View view)
    {
        Intent intent = new Intent(SupplementActivityAdmin.this, ProfilePage.class);
        startActivity(intent);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(SupplementActivityAdmin.this, BasePage.class);
        startActivity(intent);
    }
    public void addSupp(View view)
    {
        Intent intent = new Intent(SupplementActivityAdmin.this, SupplementAddActivity.class);
        startActivity(intent);
    }
    private void fetchDataFromApi()
    {
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
        response ->
        {
            try
            {
                JSONArray jsonArray = new JSONArray(response);
                Toast.makeText(this, "Fetch data supp"+ jsonArray.length() , Toast.LENGTH_SHORT).show();

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("supacad", "Key: " + ", Value:sup " + jsonObject);
                    String supplementName = jsonObject.getString("title");
                    String supplementDescription = jsonObject.getString("description");
                    String photoLink = jsonObject.getString("image");
                    String supplementIDString = jsonObject.getString("id");
                    int supplementID = Integer.parseInt(supplementIDString);

                    Supplement supplement = new Supplement(supplementName, supplementDescription,photoLink,supplementID);
                    supplementArrayList.add(supplement);
                }
                supplementAdapter.notifyDataSetChanged();
            }

            catch (JSONException e)
            {
                e.printStackTrace();
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
        });
        // RequestQueue işlemi
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
