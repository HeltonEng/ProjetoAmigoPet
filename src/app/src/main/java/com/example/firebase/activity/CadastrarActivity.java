package com.example.firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.helper.UsuarioFirebase;
import com.example.firebase.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastrarActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressbar;

    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        initComp();
        progressbar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNome = campoNome.getText().toString();
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();
                if(!textNome.isEmpty()){
                    usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    cadastrar(usuario);
                }else {
                    Toast.makeText(CadastrarActivity.this, "O campo nome esta vazio.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void  cadastrar(Usuario usuario){
        progressbar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful()){
                            try{
                                progressbar.setVisibility(View.GONE);

                                //Salvar dados no Firebase
                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId(idUsuario);
                                usuario.salvar();

                                //Salvar dados no perfil do firebase
                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                Toast.makeText(CadastrarActivity.this, "Guardado com sucesso.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }else{
                            progressbar.setVisibility(View.GONE);
                            String erro = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erro = "Digite uma senha mais forte.";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "Digite um email valido.";
                            }catch (FirebaseAuthUserCollisionException e){
                                erro = "Conta ja cadastrada.";
                            }catch (Exception e){
                                erro = "Ao cadastrar usuario: "+ e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastrarActivity.this, erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }



    void initComp(){
        campoNome = findViewById(R.id.editTextCadastrarNome);
        campoEmail = findViewById(R.id.editTextLoginEmail);
        campoSenha = findViewById(R.id.editTextLoginPass);
        botaoCadastrar = findViewById(R.id.buttonEntrar);
        progressbar = findViewById(R.id.progressBarLogin);
    }

}