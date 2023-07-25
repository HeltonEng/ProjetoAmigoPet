package com.example.firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.helper.UsuarioFirebase;
import com.example.firebase.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button botaoAcaoPerfil;
    private ImageView imagePerfil;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private  DatabaseReference seguidoresRef;

    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes, textSeguidores, textSeguindo;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Config inicial Firebase
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //inicializar componentes
        imagePerfil = findViewById(R.id.imagePerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguindo = findViewById(R.id.textSeguindo);
        textSeguidores = findViewById(R.id.textSeguidores);
        botaoAcaoPerfil = findViewById(R.id.buttonEditarPerfil);
        botaoAcaoPerfil.setText("Carregando");

        //configurar toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        myToolbar.setTitle("Perfil");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        //recuperar usuario passado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //recupera nome na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            //recupera foto
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if(caminhoFoto != null){
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(this)
                        .load(url)
                        .into(imagePerfil);
            }

        }

    }

    private void recuperaDadosUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //recupera dados
                usuarioLogado = snapshot.getValue(Usuario.class);
                //verifica se esta seguindo
                verificaSegueUsuarioAmigo();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child(idUsuarioLogado)
                .child(usuarioSelecionado.getId());

        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //seguindo
                    habilitarBotaoSeguir(true);
                }else {
                    //nao segue
                    habilitarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void habilitarBotaoSeguir(boolean segueUsuatio){
        if(segueUsuatio){
            botaoAcaoPerfil.setText("Seguindo");
        }else {
            botaoAcaoPerfil.setText("Seguir");

            //acao do botao seguir usuario
            botaoAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){
        HashMap<String,Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nome", uAmigo.getNome());
        dadosAmigo.put("caminhoFoto",uAmigo.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(uLogado.getId())
                .child(uAmigo.getId());
        seguidorRef.setValue(dadosAmigo);

        //muda acao do botao
        botaoAcaoPerfil.setText("Seguindo");
        botaoAcaoPerfil.setOnClickListener(null);

        //atualiza seguindo usuario
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String,Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        DatabaseReference usuarioSeguindo = usuariosRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);
        //atualiza seguidores
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String,Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        DatabaseReference usuarioSeguidores = usuariosRef
                .child(uAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosUsuario();
        //recuperar dados do usuario
        recuperaDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosUsuario(){
        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguidores = String.valueOf(usuario.getSeguidores());
                        String seguindo = String.valueOf(usuario.getSeguindo());

                        textPublicacoes.setText(postagens);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}