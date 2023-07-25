package com.example.firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.example.firebase.helper.UsuarioFirebase;
import com.example.firebase.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class FiltroActivity extends AppCompatActivity {

    private ImageView imagemFotoEscolhida;
    private Bitmap imagem;
    private int r = 0;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        //Recuperar dados do usuario
        FirebaseUser userPerfil = UsuarioFirebase.getUsuarioAtual();

        imagemFotoEscolhida = findViewById(R.id.imageFotoEscolhida);

        //Recuperar imagem
        //Bundle bundle = getIntent().getExtras();
        //if(bundle != null){
        //    byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
        //    imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
        //    imagemFotoEscolhida.setImageBitmap(imagem);
        //    //imagemFotoEscolhida.setRotation(90);
        //}

        // Receber a URI da imagem do Intent
        if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            Uri imageUri = Uri.parse(imageUriString);

            // Carregar a imagem na ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imagemFotoEscolhida.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imagemFotoEscolhida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r = (r < 360) ? (r + 90) : (r = 0);
                imagemFotoEscolhida.setRotation(r);
            }
        });
        Button postar = findViewById(R.id.buttonPostar);
        postar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Configura para imagem ser salva em memória
                imagemFotoEscolhida.setDrawingCacheEnabled(true);
                imagemFotoEscolhida.buildDrawingCache();

                //Recupera bitmap da imagem (image a ser carregada)
                Bitmap bitmap = imagemFotoEscolhida.getDrawingCache();

                //Comprimo bitmap para um formato png/jpeg
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos );

                //converte o baos para pixel brutos em uma matriz de bytes
                // (dados da imagem)
                byte[] dadosImagem = baos.toByteArray();

                //Define nós para o storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference imagens = storageReference.child("imagens").child("postagem").child(userPerfil.getUid());

                //Nome da imagem
                String nomeArquivo = UUID.randomUUID().toString();
                StorageReference imagemRef = imagens.child( nomeArquivo + ".jpeg");

                //Retorna objeto que irá controlar o upload
                UploadTask uploadTask = imagemRef.putBytes( dadosImagem );

                uploadTask.addOnFailureListener(FiltroActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(FiltroActivity.this,
                                "Upload da imagem falhou: " + e.getMessage().toString(),
                                Toast.LENGTH_LONG ).show();

                    }
                }).addOnSuccessListener(FiltroActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri url = Uri.parse(imagemRef.toString());
                        //Toast.makeText(FiltroActivity.this,"Sucesso ao fazer upload: " + url.toString(),Toast.LENGTH_LONG ).show();

                    }
                });

                String pattern = "MM/dd/yyyy HH:mm:ss";

                // Create an instance of SimpleDateFormat used for formatting
                // the string representation of date according to the chosen pattern
                DateFormat df = new SimpleDateFormat(pattern);

                // Get the today date using Calendar object.
                Date today = Calendar.getInstance().getTime();
                // Using DateFormat format method we can create a string
                // representation of a date with the defined format.
                String data = df.format(today);

                TextInputEditText textoPostagem = findViewById(R.id.textPostagem);
                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
                String id = UUID.randomUUID().toString();
                firebaseRef.child("Postagens").child(userPerfil.getUid()).child(id).child("nome").setValue(usuario.getNome());
                firebaseRef.child("Postagens").child(userPerfil.getUid()).child(id).child("imagem").setValue(imagemRef.toString());
                if(textoPostagem.getText().toString().isEmpty()) {
                    firebaseRef.child("Postagens").child(userPerfil.getUid()).child(id).child("postagem").setValue("Sem comentario.");
                }else{
                    firebaseRef.child("Postagens").child(userPerfil.getUid()).child(id).child("postagem").setValue(textoPostagem.getText().toString());
                }
                firebaseRef.child("Postagens").child(userPerfil.getUid()).child(id).child("data").setValue(data);
                Toast.makeText(FiltroActivity.this, "Postando", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }
}