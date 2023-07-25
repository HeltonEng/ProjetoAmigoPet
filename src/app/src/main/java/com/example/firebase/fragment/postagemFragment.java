package com.example.firebase.fragment;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.activity.FiltroActivity;
import com.example.firebase.helper.Permissao;


import java.io.ByteArrayOutputStream;

public class postagemFragment extends Fragment {

    private Button botaoAbrirGaleria, botaoAbrirCamera;
    //private static final int SELECAO_CAMERA = 100;
    //private static final int SELECAO_GALERIA = 200;

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int CAMERA_REQUEST_CODE = 456;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        botaoAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        botaoAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);

        botaoAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //someActivityCamera.launch(intent);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                //}
            }
        });

        botaoAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //someActivityGaleria.launch(intent);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri imageUri = data.getData();
                loadImage(imageUri);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Uri imageUri = saveImageToGallery(imageBitmap);
                loadImage(imageUri);
            }
        }
    }
    private void loadImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            //imageView.setImageBitmap(bitmap);

            // Enviar a URI da imagem para outra activity
            Intent intent = new Intent(getActivity(), FiltroActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Uri saveImageToGallery(Bitmap imageBitmap) {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getActivity().getContentResolver(),
                imageBitmap,
                "Imagem Capturada",
                "Descrição da Imagem Capturada"
        );
        return Uri.parse(savedImageURL);
    }


    /*
    ActivityResultLauncher<Intent> someActivityCamera = registerForActivityResult(
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
                            imagem = (Bitmap) data.getExtras().get("data");

                            //Caso tenha uma imagem
                            if(imagem != null){
                                //Converter imagem em byte array
                                Toast.makeText(getContext(), "Imagem enviada.", Toast.LENGTH_SHORT).show();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagem.compress(Bitmap.CompressFormat.JPEG, 100,baos);
                                byte[] dadosImagem = baos.toByteArray();

                                //enviar imagem para filtro
                                Intent i = new Intent(getActivity(), FiltroActivity.class);
                                i.putExtra("fotoEscolhida",dadosImagem);
                                startActivity(i);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityGaleria = registerForActivityResult(
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
                            Uri localImagemSelecionada = data.getData();
                            imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);

                            //Caso tenha uma imagem
                            if(imagem != null){
                                //Converter imagem em byte array
                                //Toast.makeText(getContext(), "Imagem enviada.", Toast.LENGTH_SHORT).show();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagem.compress(Bitmap.CompressFormat.JPEG, 50,baos);
                                byte[] dadosImagem = baos.toByteArray();

                                //enviar imagem para filtro
                                Intent i = new Intent(getActivity(), FiltroActivity.class);
                                i.putExtra("fotoEscolhida",dadosImagem);
                                startActivity(i);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            */

}