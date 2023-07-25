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
import com.example.firebase.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private ProgressBar progressBar;

    private FirebaseAuth autencicacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarLogin();
        initComp();
        progressBar.setVisibility(View.GONE);
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();
                if(!textEmail.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    validarLogin(usuario);
                }else {
                    Toast.makeText(LoginActivity.this, "O campo nome esta vazio.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verificarLogin(){
        autencicacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autencicacao.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        autencicacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autencicacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Erro ao logar : \n"+usuario.getEmail().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    void initComp(){
        campoEmail = findViewById(R.id.editTextLoginEmail);
        campoSenha = findViewById(R.id.editTextLoginPass);
        botaoEntrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressBarLogin);
    }

    public void abrirCadastro(View view){
        Intent intent = new Intent(this, CadastrarActivity.class);
        startActivity(intent);
        //setContentView(R.layout.activity_cadastrar);
    }

}