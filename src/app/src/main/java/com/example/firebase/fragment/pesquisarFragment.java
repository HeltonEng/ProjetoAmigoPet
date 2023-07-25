package com.example.firebase.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.firebase.R;
import com.example.firebase.activity.PerfilAmigoActivity;
import com.example.firebase.adapter.AdapterPesquisa;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.helper.RecyclerItemClickListener;
import com.example.firebase.helper.UsuarioFirebase;
import com.example.firebase.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class pesquisarFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;

    private AdapterPesquisa adapterPesquisa;

    private String idUsuarioLogado;

    public pesquisarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pesquisar, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerViewPesquisa);

        //configuracoes inicias
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //configura o recyclerview
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        //evento de clique
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        //configura searchview
        searchViewPesquisa.setQueryHint("Pesquisar");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String textoDigitado = s.toUpperCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });

        return view;
    }

    private void pesquisarUsuarios(String texto){
        //limpar lista
        listaUsuarios.clear();

        //caso tenha usuario na pesquisa
        if(texto.length() >= 2){
            Query query = usuariosRef.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //limpar lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        Usuario usuario = ds.getValue(Usuario.class);
                        //exclui usuario logado da lista
                        if(idUsuarioLogado.equals(usuario.getId()))
                            continue;
                        //adiciona usuario a lista
                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();
                    int total = listaUsuarios.size();
                    Log.i("totalUsuarios", "total:" + total);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}