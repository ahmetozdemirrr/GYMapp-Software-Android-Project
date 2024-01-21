package com.ahmetozdemir.gymapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetozdemir.gymapp.databinding.SupplementAdminRowBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class SupplementAdapterAdmin extends RecyclerView.Adapter< SupplementAdapterAdmin.SupplementAdminHolder> // Bu sınıf recyclerView içinde görülen supplementlerin düzenini yönetir.
{
    private Context context;
    ArrayList <Supplement> supplementArrayList; // Supplement öğelerimizi burada depoluyoruz

    public SupplementAdapterAdmin(ArrayList<Supplement> supplementArrayList,Context context) // constructor
    {
        this.supplementArrayList = supplementArrayList;
        this.context = context;
    }

    /* ViewHolder recyclerView gibi tasarımlarda çoklu nesnelere fazla hafıza kaplamadan erişebilmeyi sağlayan bir tasarım desenidir.*/
    @NonNull
    @Override
    public SupplementAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) //viewHolder oluşturur.
    {
        SupplementAdminRowBinding recyclerRowBinding = SupplementAdminRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new SupplementAdminHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplementAdminHolder holder, int position) // Belirli bir pozisyondaki öğenin içeriğini atar.
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

    public class SupplementAdminHolder extends RecyclerView.ViewHolder // Her bir öğenin görünümünü tutar
    {
        private SupplementAdminRowBinding binding;
        public SupplementAdminHolder(SupplementAdminRowBinding binding)
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
                    String url = "http://212.20.147.47:7238/api/Supplement/deletesupplement";
                    String deleteUrl = url + "?blogId=" + supplementArrayList.get(position).getSupplementID();

                    Log.d("ExerciseDeleteActivity", "Response:sup " + supplementArrayList.get(position).getSupplementID());
                    Log.d("ExerciseDeleteActivity", "Response:sup " + deleteUrl);
                    // Volley RequestQueue oluşturun.
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    // DELETE isteği oluşturun.
                    StringRequest deleteRequest = new StringRequest(Request.Method.POST, deleteUrl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            // Başarılı bir şekilde silindiğinde buraya gelecek.

                            Log.d("ExerciseDeleteActivity", "Response: " + response);
                            Intent intent = new Intent(context, SupplementActivityAdmin.class);
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
