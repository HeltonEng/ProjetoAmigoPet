package com.example.firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.adapter.AdapterComentario;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.model.Comentario;
import com.example.firebase.model.Postagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textComentarios;
    private Button buttonComentar;
    String idPost = null;

    private AdapterComentario adapterComentario;
    private DatabaseReference comRef;
    private List<Comentario> comentarios = new ArrayList<>();
    List<String> comentariosRecuperados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Configura RecyclerView
        recyclerView = findViewById(R.id.recyclerViewComentarios);
        //define layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //configura adapter
        adapterComentario = new AdapterComentario(comentarios);
        recyclerView.setAdapter(adapterComentario);

        //configurar toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        myToolbar.setTitle("Comentarios");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        //referencia firebase
        comRef = ConfiguracaoFirebase.getFirebase()
                .child("Postagens");
        //.child( idUsuarioLogado );
        textComentarios = findViewById(R.id.textComentarios);

        //Recuperar id
        if (getIntent().hasExtra("idPost")) {
            idPost = getIntent().getStringExtra("idPost");
        }

        buttonComentar = findViewById(R.id.buttonComentar);
        buttonComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textComentarios.setText("Teste");
                Comentario c = new Comentario("", "", "", "Teste");
                comentarios.add(c);
                adapterComentario.notifyDataSetChanged();
                salvarComentario();
            }
        });

    }
    public void salvarComentario() {
        String comentario = textComentarios.getText().toString();
        if (comentario != null && !comentario.equals("")) {
            Toast.makeText(this, "Salvo em " + idPost, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Escreva um comentario.", Toast.LENGTH_SHORT).show();
        }
        textComentarios.setText("");
    }

    //altera funcao padrao do botao voltar para finalizar a activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void listarComentarios() {
        //carrega comentarios
        comRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Recupera dados
                    comentariosRecuperados.add(ds.getKey());
                }
                System.out.println(comentariosRecuperados);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listarComentarios();
    }

}