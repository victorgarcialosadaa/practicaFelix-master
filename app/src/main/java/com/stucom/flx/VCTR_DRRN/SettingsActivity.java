package com.stucom.flx.VCTR_DRRN;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stucom.flx.VCTR_DRRN.model.MyVolley;
import com.stucom.flx.VCTR_DRRN.model.Player;
import com.stucom.flx.VCTR_DRRN.model.Prefs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edName, edEmail;
    ImageView imImage;
    Uri photoURI;
    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Capture needed layout views
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        imImage = findViewById(R.id.imImage);
        player = Prefs.getInstance(SettingsActivity.this).getPlayer();

        // All buttons to this class (see implements in the class' declaration)
        findViewById(R.id.btnGallery).setOnClickListener(this);
        findViewById(R.id.btnCamera).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        findViewById(R.id.btn_unregister).setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        // Load player info from SharedPrefs
        edName.setText(player.getName());
    }

    @Override
    public void onPause() {
        changeName();
        // Save player info from SharedPrefs (save changes on name and email only)
        player.setName(edName.getText().toString());
        setImage(player.getImage(), false);

        super.onPause();
    }

    @Override
    public void onClick(View view) {
        // All buttons come here, so we decide based on their ids
        switch (view.getId()) {
            case R.id.btnDelete:
                deleteAvatar();
                break;
            case R.id.btnCamera:
                getAvatarFromCamera();
                break;
            case R.id.btnGallery:
                getAvatarFromGallery();
                break;
            case R.id.btn_unregister:
                unregisterUser();
                break;

        }
    }

    // Needed for onActivityResult()
    private static final int AVATAR_FROM_GALLERY = 1;
    private static final int AVATAR_FROM_CAMERA = 2;

    public void deleteAvatar() {
        // In this case simply clear the image by pasing null
        setImage(null, true);
    }

    public void getAvatarFromGallery() {
        // Call the Open Document intent searching for images
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, AVATAR_FROM_GALLERY);
    }

    public void getAvatarFromCamera() {
        // Prepare for storage (see FileProvider background documentation)
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Always this path
        File photo = new File(storageDir, "photo.jpg");
        try {
            boolean ok = photo.createNewFile();
            if (ok) Log.d("flx", "Overwriting image");
        } catch (IOException e) {
            Log.e("flx", "Error creating image file " + photo);
            return;
        }
        Log.d("flx", "Writing photo to " + photo);
        // Pass the photo path to the Intent and start it
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoURI = FileProvider.getUriForFile(this, "com.stucom.flx.fileProvider", photo);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, AVATAR_FROM_CAMERA);
        } catch (IllegalArgumentException e) {
            Log.e("flx", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Skip cancels & errors
        if (resultCode != RESULT_OK) return;

        if (requestCode == AVATAR_FROM_GALLERY) {
            // coming from gallery, the URI is in the intent's data
            photoURI = data.getData();
        }
        // if camera, no action needed, as we set the URI when the intent was created

        // now set the avatar
        String avatar = (photoURI == null) ? null : photoURI.toString();
        setImage(avatar, true);
    }

    public void setImage(String img, boolean saveToSharedPreferences) {
        Log.d("flx", "PlayerAvatar = " + img);
        if (img == null) {
            // if null, set the default "unknown" avatar picture
            imImage.setImageResource(R.drawable.unknown);
        } else {
            // the URI must be valid, so we set it to the ImageView
            Uri uri = Uri.parse(img);

            /*Pasamos la imgaen en la clase pushImage con el parametro uri para subirlo en el servidor*/
            try {
                pushImage(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imImage.setImageURI(uri);
        }
        if (!saveToSharedPreferences) return;
        // comply if a save to prefs was requested
        player.setImage(img);
        Prefs.getInstance(this).saveToPrefs();
    }

    /* Método para subir el nombre al servidor */
    public void changeName() {
        final String currentName = edName.getText().toString();
        /*Si el nombre del edName es diferente al de la instacia del jugador que cambie su nombre en el servidor.*/
        if (!currentName.equals(Prefs.getInstance(SettingsActivity.this).getPlayer().getName())) {
            String URL = "https://api.flx.cat/dam2game/user?token=" + Prefs.getInstance(SettingsActivity.this).getToken();
            StringRequest request = new StringRequest
                    (Request.Method.PUT, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Nombre modificado";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = error.toString();
                            NetworkResponse response = error.networkResponse;
                            if (response != null) {
                                Context context = getApplicationContext();
                                CharSequence text = response.statusCode + " " + message;
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", currentName);
                    return params;
                }
            };
            MyVolley.getInstance(SettingsActivity.this).add(request);
        }
    }
    /*Como hemos dicho anteriormente, este metodo es para subirlo al servidor*/
    public void pushImage(Uri uri) throws IOException {
        /*Convertimos la uri en un bitmap*/
        Bitmap bitmap = uriToBitmap(uri);
        /*Convertimos el Bitmap en un String que es de base 64*/
        final String currentImage = bitmapToBase64(bitmap);
        String URL = "https://api.flx.cat/dam2game/user?token=" + Prefs.getInstance(SettingsActivity.this).getToken();
        StringRequest request = new StringRequest
                (Request.Method.PUT, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Context context = getApplicationContext();
                                CharSequence text = "Imagen modificado";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.toString();
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            Context context = getApplicationContext();
                            CharSequence text = response.statusCode + " " + message;
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", currentImage);
                return params;
            }
        };
        MyVolley.getInstance(SettingsActivity.this).add(request);

    }

    /*Método para que pasamos a Uri a Bitmap*/


    public Bitmap uriToBitmap(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        return bitmap;
    }
    /*Método para que pasamos a Bitmap a Base64*/

    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgBaseTo64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imgBaseTo64;
    }
    /*Método para que pasamos a Base64 a Bitmap*/
    public Bitmap base64ToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    /*TODO: Falta Pasar Bitmap a Uri así finalmente coger la imagen de la base datos e introducirlo al dispositivo*/


    /*Desregistramos el usuario de la BBDD, Api de Felix y de la instancia de Prefs*/
    public void unregisterUser(){
        String URL = "https://api.flx.cat/dam2game/unregister?token=" + Prefs.getInstance(SettingsActivity.this).getToken();
        StringRequest request = new StringRequest
                (Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Context context = getApplicationContext();
                                Player player= new Player();
                                /*La instancia pref lo convertimos en vacío asi no tendremos nada guardado en cache*/
                                Prefs.getInstance(SettingsActivity.this).setPlayer(player);
                                Prefs.getInstance(SettingsActivity.this).setToken("");
                                CharSequence text = "Bye Bye";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                Intent intent = new Intent(SettingsActivity.this, RegisterActivity.class);
                                startActivity(intent);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.toString();
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            Context context = getApplicationContext();
                            CharSequence text = response.statusCode + " " + message;
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("must_delete", "true");
                return params;
            }

        };
        MyVolley.getInstance(SettingsActivity.this).add(request);

    }

}
