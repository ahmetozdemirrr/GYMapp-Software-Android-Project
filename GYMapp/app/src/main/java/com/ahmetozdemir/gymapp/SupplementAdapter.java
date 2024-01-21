package com.ahmetozdemir.gymapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetozdemir.gymapp.databinding.RecyclerRowBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.ArrayList;

public class SupplementAdapter extends RecyclerView.Adapter< SupplementAdapter.SupplementHolder> // Bu sınıf recyclerView içinde görülen supplementlerin düzenini yönetir.
{
    ArrayList <Supplement> supplementArrayList; // Supplement öğelerimizi burada depoluyoruz

    public  SupplementAdapter(ArrayList<Supplement> supplementArrayList) // constructor
    {
        this.supplementArrayList = supplementArrayList;
    }

    /* ViewHolder recyclerView gibi tasarımlarda çoklu nesnelere fazla hafıza kaplamadan erişebilmeyi sağlayan bir tasarım desenidir.*/
    @NonNull
    @Override
    public SupplementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) //viewHolder oluşturur.
    {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new SupplementHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplementHolder holder, int position) // Belirli bir pozisyondaki öğenin içeriğini atar.
    {
        holder.binding.recyclerViewTextView.setText(supplementArrayList.get(position).supplementName);

        Glide.with(holder.itemView.getContext())
                .load(supplementArrayList.get(position).supplementImage)
                .placeholder(R.drawable.user_icon) // Opsiyonel: Yükleme sırasında gösterilecek resim
                .error(R.drawable.user_icon) // Opsiyonel: Yükleme hatası durumunda gösterilecek resim
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imageView1);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(holder.itemView.getContext(), DetailsActivity.class);
                intent.putExtra("landmark",supplementArrayList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() // Veri kümesindeki eleman sayısını tutar.
    {
        return supplementArrayList.size();
    }

    public class SupplementHolder extends RecyclerView.ViewHolder // Her bir öğenin görünümünü tutar
    {
        private RecyclerRowBinding binding;
        public SupplementHolder(RecyclerRowBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
