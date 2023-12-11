package com.example.telecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.example.telecommunity.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.View;
public class BaseActivity extends AppCompatActivity {
    private TextView titleTextView;
    BottomNavigationView navView;
    private boolean enMisActividades = false;
    private String userRole;
    private ImageView iconSync;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        userRole = sharedPreferences.getString("tipoUsuario", "usuario");

        db = FirebaseFirestore.getInstance();
        iconSync = findViewById(R.id.iconSync);
        titleTextView = findViewById(R.id.titleTextView);
        navView = findViewById(R.id.bottomNavigationView);

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enMisActividades) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    titleTextView.setText("Inicio");
                    enMisActividades = false;
                } else {
                    Fragment fragment = "Usuario".equals(userRole) ? new MisEventosFragment() : new MisActividadesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    titleTextView.setText("Usuario".equals(userRole) ? "Mis Eventos" : "Mis Actividades");
                    enMisActividades = true;
                }
            }
        });

        iconSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enMisActividades) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    titleTextView.setText("Inicio");
                    enMisActividades = false;
                } else {
                    Fragment fragment = "Usuario".equals(userRole) ? new MisEventosFragment() : new MisActividadesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    titleTextView.setText("Usuario".equals(userRole) ? "Mis Eventos" : "Mis Actividades");
                    enMisActividades = true;
                }
            }
        });



        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_inicio) {
                    if (enMisActividades) {
                        selectedFragment = "Usuario".equals(userRole) ? new MisEventosFragment() : new MisActividadesFragment();
                        titleTextView.setText("Usuario".equals(userRole) ? "Mis Eventos" : "Mis Actividades");
                    } else {
                        selectedFragment = new HomeFragment();
                        titleTextView.setText("Inicio");
                    }
                } else if (id == R.id.navigation_buscar) {
                    selectedFragment = new SearchFragment();
                    titleTextView.setText("Buscar");
                } else if (id == R.id.navigation_notificacion) {
                    selectedFragment = new NotificationFragment();
                    titleTextView.setText("Notificaciones");
                } else if (id == R.id.navigation_donacion) {
                    selectedFragment = new DonationFragment();
                    titleTextView.setText("Donaciones");
                } else if (id == R.id.navigation_mi_perfil) {
                    selectedFragment = new ProfileFragment();
                    titleTextView.setText("Mi Perfil");
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    updateIconVisibility(selectedFragment); // Agregar esta línea para actualizar la visibilidad del ícono

                }

                return true;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navView.setSelectedItemId(R.id.navigation_inicio);
            titleTextView.setText("Inicio");
        }

        cometChatLogin();

    }

    @Override
    public void onBackPressed() {
        // Obtén el fragmento actual
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        // Actualiza el título de acuerdo con el fragmento
        if (currentFragment instanceof HomeFragment ||
                currentFragment instanceof MisEventosFragment ||
                currentFragment instanceof MisActividadesFragment ||
                currentFragment instanceof SearchFragment ||
                currentFragment instanceof NotificationFragment ||
                currentFragment instanceof DonationFragment ||
                currentFragment instanceof ProfileFragment) {


            moveTaskToBack(true);
            minimizeApp();
        } else if (currentFragment instanceof CometChatFragment) {
            titleTextView.setText("Mis Actividades");
            super.onBackPressed();
        } else {
            super.onBackPressed(); // No olvides llamar al super método

        }

    }

    public void minimizeApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void cometChatLogin() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String email = firebaseUser.getEmail();
            if (email != null) {
                db.collection("usuarios").whereEqualTo("correo", email).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                                Number codigo = userDocument.getLong("codigo"); // Obtiene el código como un Number
                                if (codigo != null) {
                                    String UID = String.valueOf(codigo.longValue()); // Convierte el código a String
                                    String authKey = "4db23a1794fbd8f631c7852fb5ac2c17c58b9bb1"; // Reemplaza con tu Auth Key de CometChat.

                                    CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {
                                        @Override
                                        public void onSuccess(User user) {
                                            Log.d("CometChat", "Login en CometChat exitoso: " + user.toString());

                                            // Guardar el UID para usarlo después en el adaptador
                                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("currentUserID", user.getUid());
                                            editor.apply();
                                        }

                                        @Override
                                        public void onError(CometChatException e) {
                                            Log.d("CometChat", "Login en CometChat falló: " + e.getMessage());
                                        }
                                    });
                                } else {
                                    Log.d("CometChat", "No se pudo obtener el código del usuario de Firestore");
                                }
                            } else {
                                Log.d("CometChat", "No se encontraron documentos de usuario con ese correo");
                            }
                        })
                        .addOnFailureListener(e -> Log.d("CometChat", "Error al obtener el documento del usuario", e));
            }
        } else {
            Log.d("CometChat", "El usuario de Firebase no está autenticado.");
        }
    }


    public void crearPublicacion(View view) {
        Intent intent = new Intent(this, CrearPublicacionActivity.class);
        startActivity(intent);
    }

    private void updateIconVisibility(Fragment activeFragment) {
        if (activeFragment instanceof HomeFragment || activeFragment instanceof MisActividadesFragment || activeFragment instanceof MisEventosFragment) {
            iconSync.setVisibility(View.VISIBLE);
        } else {
            iconSync.setVisibility(View.GONE);
        }
    }

}