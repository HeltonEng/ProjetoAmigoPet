package com.example.firebase.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.helper.UsuarioFirebase;
import com.example.firebase.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class EditarPerfilActivity extends AppCompatActivity {

    private ImageView imagePerfil;
    private TextView alterarFoto;
    private TextInputEditText inputNome, inputEmail;
    private Button buttonEditar;
    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private String identificadorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //configurar toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        myToolbar.setTitle("Editar Perfil");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        //configurar usuario
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdUsuario();

        //configurar componentes
        initComp();

        //Recuperar dados do usuario
        FirebaseUser userPerfil = UsuarioFirebase.getUsuarioAtual();
        inputEmail.setText(userPerfil.getEmail());
        inputNome.setText(userPerfil.getDisplayName());

        Uri url = userPerfil.getPhotoUrl();
        if (url != null){
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imagePerfil);
            //imagePerfil.setRotation(90);
        }else{
            imagePerfil.setImageResource(R.drawable.avatar);
        }

        //Salvar alteracoes
        buttonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeAtualizado = inputNome.getText().toString();

                //Atualizar nome no perfil
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                //Atualizar nome na base de dados
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.atualizar();
                Toast.makeText(EditarPerfilActivity.this, "Dados atualizados com sucesso.", Toast.LENGTH_SHORT).show();
            }
        });

        //configurar foto
        alterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(intent);
            }
        });

    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        //doSomeOperations();
                        Bitmap imagem = null;
                        try {
                            //selecao apenas da galeria
                            Uri localImagemSelecionada = data.getData();
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                            //Caso tenha uma imagem
                            if(imagem != null){
                                imagePerfil.setImageBitmap(imagem);
                                //imagePerfil.setRotation(90);

                                //Recuperar imagem para firebase
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagem.compress(Bitmap.CompressFormat.JPEG, 100,baos);
                                byte[] dadosImagem = baos.toByteArray();

                                //Salvar imagem no firebase
                                StorageReference imgRef = storageRef
                                        .child("imagens")
                                        .child("perfil")
                                        .child(identificadorUsuario + ".jpg");
                                UploadTask uploadTask = imgRef.putBytes(dadosImagem);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditarPerfilActivity.this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //recuperar local da foto
                                        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();
                                                atualizarFotoUsuario(url);
                                            }
                                        });

                                        Toast.makeText(EditarPerfilActivity.this, "Imagem enviada.", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void atualizarFotoUsuario(Uri url){
        //Atualizar foto perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        //Atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(this, "Sua foto foi atualizada", Toast.LENGTH_SHORT).show();

    }

    void initComp(){
        imagePerfil = findViewById(R.id.imageEditar);
        alterarFoto = findViewById(R.id.textAlterarFotoEditar);
        inputNome = findViewById(R.id.nomeEditar);
        inputEmail = findViewById(R.id.emailEditar);
        buttonEditar = findViewById(R.id.buttonSalvarEditar);
        inputEmail.setFocusable(false);
    }

    //altera funcao padrao do botao voltar para finalizar a activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}