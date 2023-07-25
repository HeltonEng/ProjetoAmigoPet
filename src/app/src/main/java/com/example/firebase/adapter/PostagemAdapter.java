package com.example.firebase.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.activity.ComentariosActivity;
import com.example.firebase.model.Postagem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.MyViewHolder> {

    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth usuario = FirebaseAuth.getInstance();
    private List<Postagem> postagens;

    public PostagemAdapter(List<Postagem> p) {
        this.postagens = p;
    }

    public Void limpar(){
        postagens.clear();
        return null;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.postagem_detalhe,parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Postagem postagem = postagens.get(position);
        holder.nome.setText(postagem.getNome());
        holder.postagem.setText(postagem.getPostagem());
        holder.data.setText(postagem.getData());

        //Ajusta link da imagem e carrega
        String ur = postagem.getImagem();
        String[] bits = ur.split("/");
        String lastOne = bits[bits.length-1];
        //System.out.println(lastOne);
        ur = "https://firebasestorage.googleapis.com/v0/b/fir-8849a.appspot.com/o/imagens%2Fpostagem%2F"+ postagem.getId() +"%2F"+lastOne+"?alt=media";
        //System.out.println(ur);
        Uri url = Uri.parse( ur );
        if (url != null){
            Glide.with(holder.itemView)
                    .load(url)
                    .into(holder.imagem);
        }else{
            holder.imagem.setImageResource(R.drawable.avatar);
        }

        holder.botaoComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.itemView.getContext(), ComentariosActivity.class);
                i.putExtra("idPost", postagem.getIdPost());
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nome;
        private TextView postagem;
        private TextView data;
        private ImageView imagem;
        private Button botaoComentarios;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textNome);
            postagem = itemView.findViewById(R.id.textPostagem);
            imagem = itemView.findViewById(R.id.imagePostagem);
            data = itemView.findViewById(R.id.textData);
            botaoComentarios = itemView.findViewById(R.id.buttonComentarios);
        }
    }
}
