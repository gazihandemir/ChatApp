package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//recycler view kullanmak için kendi RecyclerViewAdapter'imizi yazıyoruz.Bunun için viewHolder'a ihtiyacımız var
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    // Textleri yani mesajları tutabilmememiz için bir list oluşturuyoruz
    private List<String> chatMessages;

    public RecyclerViewAdapter(List<String> chatMessages) {
        // Recycler view oluştururken arrayList ile bağlamak için bir constructor oluşturuyoruz .
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Her bir satırın nasıl bözükecegini belirlemek için bu methodu kullanıyoruz ve bağlama işlemini LayoutInflater ile yapıyoruz
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        // Geriye tutucumuzu yolluyoruz
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Mesaj yazılan editText'teki yazdığımız şeyi alıyoruz ve
       String chatMessage = chatMessages.get(position);
       //  teker teker kullanacağımız recycle_view_row 'un içine koyuyoyurz
       holder.chatMessage.setText(chatMessage);
    }

    @Override
    public int getItemCount() {
        // mesajların uzunluğu attığımız kadar olsun
        return chatMessages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // View holder oluşturup tekli mesaj tutacagımız textview'i tanımlıyoruz
        public TextView chatMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chatMessage = itemView.findViewById(R.id.tvRecViewAdapter);
        }
    }
}
