package com.example.firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.firebase.R;
import com.example.firebase.fragment.feedFragment;
import com.example.firebase.fragment.perfilFragment;
import com.example.firebase.fragment.pesquisarFragment;
import com.example.firebase.fragment.postagemFragment;
import com.example.firebase.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //configurar Firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //configurar toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        myToolbar.setTitle("Amigo Pet");
        setSupportActionBar(myToolbar);

        bottomNavigationView = findViewById(R.id.btnv);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.feed:
                        selectedFragment = new feedFragment();
                        break;
                    case R.id.pesquisar:
                        selectedFragment = new pesquisarFragment();
                        break;
                    case R.id.postagem:
                        selectedFragment = new postagemFragment();
                        break;
                    case R.id.perfil:
                        selectedFragment = new perfilFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toollbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}