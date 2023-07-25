package com.example.firebase.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.activity.EditarPerfilActivity;
import com.example.firebase.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseUser;

public class perfilFragment extends Fragment {

    private ProgressBar progressBar;
    private ImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonEditarPerfil;

    public perfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        //configurar componentes
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        textPublicacoes = view.findViewById(R.id.textP);
        textSeguindo = view.findViewById(R.id.textSe);
        textSeguidores = view.findViewById(R.id.textS);
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfil);

        //Recuperar dados do usuario
        FirebaseUser userPerfil = UsuarioFirebase.getUsuarioAtual();

        Uri url = userPerfil.getPhotoUrl();
        if (url != null){
            Glide.with(this)
                    .load(url)
                    .into(imagePerfil);
            //imagePerfil.setRotation(90);
        }else{
            imagePerfil.setImageResource(R.drawable.avatar);
        }

        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}