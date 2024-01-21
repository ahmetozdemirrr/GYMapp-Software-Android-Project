package com.ahmetozdemir.gymapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ahmetozdemir.gymapp.databinding.ExerciseAdminRowBinding;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ExerciseAdapterAdmin extends RecyclerView.Adapter< ExerciseAdapterAdmin.ExerciseHolder> // Bu sınıf recyclerView içinde görülen supplementlerin düzenini yönetir.
{
    private Context context;
    ArrayList <ExerciseModel> exerciseArrayList; // Supplement öğelerimizi burada depoluyoruz

    public ExerciseAdapterAdmin(ArrayList<ExerciseModel> exerciseModelArrayList, Context context) // constructor
    {
        this.context = context;
        this.exerciseArrayList = exerciseModelArrayList;
    }

    /* ViewHolder recyclerView gibi tasarımlarda çoklu nesnelere fazla hafıza kaplamadan erişebilmeyi sağlayan bir tasarım desenidir.*/
    @NonNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) //viewHolder oluşturur.
    {
        ExerciseAdminRowBinding recyclerRowBinding = ExerciseAdminRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ExerciseHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseHolder holder, int position)
    {
        holder.binding.exerciseName.setText(exerciseArrayList.get(position).getExerciseName());
        holder.binding.exerciseDescription.setText(exerciseArrayList.get(position).getExerciseDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String youtubeLink = exerciseArrayList.get(position).getExerciseLink();
                Log.d("JSONObject", "Keyadapter " + ", Value: " + youtubeLink + "deneme " + exerciseArrayList.get(position).getExerciseDescription()) ;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));

                // YouTube uygulamasını kontrol et ve varsa kullan, yoksa tarayıcıda aç
                // intent.putExtra("force_fullscreen", true);
                intent.setPackage("com.google.android.youtube");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() // Veri kümesindeki eleman sayısını tutar.
    {
        return exerciseArrayList.size();
    }

    public class ExerciseHolder extends RecyclerView.ViewHolder // Her bir öğenin görünümünü tutar
    {
        private ExerciseAdminRowBinding binding;
        public ExerciseHolder(ExerciseAdminRowBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;

            Button yourButton = itemView.findViewById(R.id.closeTextView);

            // Butona tıklandığında yapılacak işlemleri tanımla
            yourButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    int position = getAdapterPosition();
                    String url = "http://212.20.147.47:7238/api/Exercise/deleteexercise";

                    String deleteUrl = url + "?exerciseId=" + exerciseArrayList.get(position).getExerciseID();
                    Log.d("ExerciseDeleteActivity", "Response: " + deleteUrl);
                    // Volley RequestQueue oluşturun.
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    // DELETE isteği oluşturun.
                    StringRequest deleteRequest = new StringRequest(Request.Method.POST, deleteUrl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            Log.d("ExerciseDeleteActivity", "Response: " + response);
                            Intent intent = new Intent(context, ExerciseAdmin.class);
                            context.startActivity(intent);
                        }
                    },

                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // Silme sırasında bir hata oluştuğunda buraya gelecek.

                            Log.e("ExerciseDeleteActivity", "Volley Error: " + error.toString());

                            if (error.networkResponse != null)
                            {
                                Log.e("ExerciseDeleteActivity", "Error Response Code: " + error.networkResponse.statusCode);
                            }
                        }
                    });
                    // İsteği RequestQueue'ya ekleyin.
                    requestQueue.add(deleteRequest);
                }
            });
        }
    }
}