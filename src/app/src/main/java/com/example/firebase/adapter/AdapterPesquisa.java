package com.example.firebase.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.model.Usuario;

import java.util.List;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.myViewHolder> {

    private List<Usuario> listaUsuario;
    private Context context;

    public AdapterPesquisa(List<Usuario> l, Context c) {
        this.listaUsuario = l;
        this.context = c;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);

        return new myViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Usuario usuario = listaUsuario.get(position);

        holder.nome.setText(usuario.getNome());

        if(usuario.getCaminhoFoto() != null){
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(context).load(uri).into(holder.foto);

        }else{
            holder.foto.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        ImageView foto;
        TextView nome;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoPesquisa);
            nome = itemView.findViewById(R.id.textNomePesquisa);

        }
    }

}
