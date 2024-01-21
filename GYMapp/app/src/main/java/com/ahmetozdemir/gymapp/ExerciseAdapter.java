package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ahmetozdemir.gymapp.databinding.ExerciseRecyclerBinding;
import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter< ExerciseAdapter.ExerciseHolder> // Bu sınıf recyclerView içinde görülen supplementlerin düzenini yönetir.
{
    ArrayList <ExerciseModel> exerciseArrayList; // Supplement öğelerimizi burada depoluyoruz

    public ExerciseAdapter(ArrayList<ExerciseModel> exerciseModelArrayList) // constructor
    {
        this.exerciseArrayList = exerciseModelArrayList;
    }

    /* ViewHolder recyclerView gibi tasarımlarda çoklu nesnelere fazla hafıza kaplamadan erişebilmeyi sağlayan bir tasarım desenidir.*/
    @NonNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) //viewHolder oluşturur.
    {
        ExerciseRecyclerBinding recyclerRowBinding = ExerciseRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
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
        private ExerciseRecyclerBinding binding;
        public ExerciseHolder(ExerciseRecyclerBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}