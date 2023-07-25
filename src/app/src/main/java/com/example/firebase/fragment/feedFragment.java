package com.example.firebase.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.firebase.R;
import com.example.firebase.adapter.PostagemAdapter;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.helper.UsuarioFirebase;
import com.example.firebase.model.Postagem;
import com.example.firebase.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class feedFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Postagem> postagens = new ArrayList<>();
    private Postagem p;
    private ProgressBar progressBar;
    boolean isLoading = false;

    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference segRef, feedRef;
    private String  idUsuarioLogado;
    private Usuario usuario;
    PostagemAdapter adapter = new PostagemAdapter(postagens);
    List<String> seguindo = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recyclerViewFeed);

        //Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        segRef = ConfiguracaoFirebase.getFirebase()
                .child("seguidores")
                .child( idUsuarioLogado );
        feedRef = ConfiguracaoFirebase.getFirebase()
                .child("Postagens");
                //.child( idUsuarioLogado );

        //define layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //define adapter
        //carregarPostagens();
        //listarFeed();

        //PostagemAdapter adapter = new PostagemAdapter(postagens);
        recyclerView.setAdapter(adapter);

        //Atualiza feed
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.limpar();
                adapter.notifyDataSetChanged();
                listarFeed();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void listarFeed(){

        segRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot ds: snapshot.getChildren() ){
                    //Recupera dados
                    seguindo.add(ds.getKey());
                }
                System.out.println(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        feedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot ds: snapshot.getChildren() ){
                    //Recupera dados
                    //postagens.add( ds.getValue(Postagem.class) );
                    if(seguindo.contains(ds.getKey())){
                        System.out.println("Igual:");
                        String formata = "" + ds.getValue();
                        System.out.println(formata);
                        //formata = formata.replace(" ", "");
                        formata = formata.replace("{", "");
                        formata = formata.replace("}, ", "  ");
                        formata = formata.replace("}", "");
                        String[] lista = formata.split("  ");
                        //formata = formata.replace("{", " ");
                        //String[] bits = formata.split(" ");
                        System.out.println(formata);

                        for (int i =0; i < lista.length; i++) {
                            System.out.println(i + " : " + lista[i]);
                            String f = lista[i].replace(", imagem=", "  ");
                            f = f.replace(", nome=", "  ");
                            f = f.replace(", postagem=", "  ");
                            f = f.replace("data=", "  ");
                            String[] l = f.split("  ");
                            System.out.println(f);
                            //System.out.println(i + " : " + ds.getKey() + " | " + l[0] + " | " +  l[1] + " | " + l[2] + " | " + l[3]);
                            p = new Postagem(ds.getKey(), l[0].replaceFirst(".$", ""),l[3],l[2],l[4],l[1]);
                            postagens.add(p);
                        }

                     }
                    //System.out.println("Teste key: " + i + " : " + ds.getKey() +" : " + seguindo);
                }
                Collections.reverse( postagens );
                System.out.println("Postagens: " + postagens);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        listarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        //feedRef.removeEventListener( valueEventListenerFeed );
    }

}