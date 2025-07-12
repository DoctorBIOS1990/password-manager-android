package com.example.gestordecontraseas;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ManagerActivity extends AppCompatActivity {

    private EditText et_id, et_descripcion, et_password, et_buscar, et_fuerza;
    private ListView lv_listado;
    private ArrayAdapter<String> arrayAdapter;
    private boolean mostrarPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_id = (EditText)findViewById(R.id.editId);
        et_descripcion = (EditText)findViewById(R.id.editDescripcion);
        et_password = (EditText)findViewById(R.id.editPassword);
        et_buscar = (EditText)findViewById(R.id.editBuscar);
        et_fuerza = (EditText)findViewById(R.id.editFuerza);
        lv_listado = findViewById(R.id.listView);
        Reload_List();
    }

    public static AdminSqliteOpenHelper database(Context context){
        String dbName = "database.sqlite";
        return new AdminSqliteOpenHelper(context, dbName, null, 1);
    }

    public void Ajustes(View view){
        Intent settings = new Intent (this, UserActivity.class);
        startActivity(settings);
    }

    // Post Method, for save password data.
    public void Guardar(View view){
        AdminSqliteOpenHelper manager = database(this);
        SQLiteDatabase db = manager.getWritableDatabase();
        String descripcion = et_descripcion.getText().toString();
        String password = et_password.getText().toString();

        if (!descripcion.isEmpty() && !password.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("descripcion", descripcion);
            registro.put("password", password);

            db.insert("passwords_list", null, registro);
            db.close();
            clearFields();
            Reload_List();

            Toast.makeText(this, "Clave guardada correctamente. ", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Debe llenar los campos. ", Toast.LENGTH_SHORT).show();
        }
    }

    // Method Search, find data for description
    public void searchByDescription (String descripcion){
        AdminSqliteOpenHelper manager = database(this);
        SQLiteDatabase db = manager.getWritableDatabase();

        if (!descripcion.isEmpty()){
            Cursor fila = db.rawQuery
                    ("SELECT * FROM passwords_list WHERE descripcion LIKE '%" + descripcion + "%'", null);

            if (fila.moveToFirst()){
                et_id.setText(fila.getString(0));
                et_descripcion.setText(fila.getString(1));
                et_password.setText(fila.getString(2));
                db.close();
            } else {
                Toast.makeText(this, "No existen registros. ", Toast.LENGTH_SHORT).show();
                db.close();
            }

        } else{
            Toast.makeText(this, "Debe introducir parte de la descripción asociada a la clave. ", Toast.LENGTH_SHORT).show();
        }
    }

    // Buscar desde la View, Reutilizando metodo.
    public void Buscar (View view){
        searchByDescription( et_buscar.getText().toString() );
    }

    // Method for modified data for any account.
    public void Modificar(View view){
        AdminSqliteOpenHelper manager = database(this);
        SQLiteDatabase db = manager.getWritableDatabase();

        String id = et_id.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String password = et_password.getText().toString();

        if (!id.isEmpty() && !descripcion.isEmpty() && !password.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("id", id);
            registro.put("descripcion", descripcion);
            registro.put("password", password);

            int count = db.update( "passwords_list", registro,  "id=" + id, null);

            db.close();
            clearFields ();

            if (count == 1){
                Reload_List();
                Toast.makeText(this, "Datos de la clave actualizados correctamente. ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe la clave registrada. ", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(this, "Debe llenar los campos. ", Toast.LENGTH_SHORT).show();
        }
    }

    // Method for Delete data registry.
    public void Eliminar(View view){
        AdminSqliteOpenHelper manager = database(this);
        SQLiteDatabase db = manager.getWritableDatabase();

        String id = et_id.getText().toString();

        if (!id.isEmpty()){
            int count = db.delete( "passwords_list", "id=" + id, null);

            db.close();
            clearFields();

            if (count == 1){
                Reload_List();
                Toast.makeText(this, "Clave eliminada correctamente. ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe la clave registrada. ", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "Debes introducir el Número de la clave. ", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to Build Random Password
    public void crearContrasena(View view){
        String password = et_password.getText().toString();
        int fuerza = Integer.parseInt( et_fuerza.getText().toString() );

        if (fuerza >= 8){
            String generated_pass = buildPass(fuerza);
            if (password.isEmpty()){
                et_password.setText(generated_pass);
            } else {
                Toast.makeText(this, "¿Estas seguro de cambiar la clave?. Blanquea la clave si lo estas.", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Debes introducir el Número de fuerza mayor a 8.", Toast.LENGTH_SHORT).show();
        }
    }

    // Generate Pass
    public String buildPass(int longitud) {
        String caracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%{}_-[]";
        String resultado = "";

        Random random = new Random();
        int caracteresLength = caracteres.length();

        for (int i = 0; i < longitud; i++) {
            int posicionAleatoria = random.nextInt(caracteresLength);
            resultado += caracteres.charAt(posicionAleatoria);
        }
        return resultado;
    }

    // Actions Buttons
    public void clearDescription (View view){
        et_descripcion.setText("");
    }

    // Clear All
    private void clearFields (){
        et_id.setText("");
        et_descripcion.setText("");
        et_password.setText("");
    }
    public void clearAll (View view){
        clearFields();
    }

    public void copyDescription (View view){
        String texto = et_descripcion.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto copiado", texto);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Descripción Copiada!", Toast.LENGTH_SHORT).show();
    }

    // Buttons
    public void showPassword (View view){
        if (mostrarPassword) {
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mostrarPassword = false;
        } else {
            // Mostrar la contraseña
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mostrarPassword = true;
        }

        et_password.setSelection(et_password.getText().length());
    }

    public void copyPassword (View view){
        String texto = et_password.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto copiado", texto);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Clave Copiada!", Toast.LENGTH_SHORT).show();
    }

    // Recorrer los campos de la DB
    public List<String> Listar(){
        List<String> registros = new ArrayList<>();
        Map<Integer, String> idDescripcionMap = new HashMap<>();

        AdminSqliteOpenHelper manager = database(this);
        SQLiteDatabase db = manager.getWritableDatabase();

        Cursor fila = db.rawQuery("SELECT id, descripcion FROM passwords_list", null);
        if (fila.moveToFirst()) {
            do {
                String descripcion = fila.getString(1);
                db.close();
                String registro = descripcion;
                registros.add(registro);
            } while (fila.moveToNext());
        }
        fila.close();
        db.close();
        return registros;
    }

    // Recargar la lista
    public void Reload_List(){
        AdminSqliteOpenHelper manager = database(this);
        SQLiteDatabase db = manager.getWritableDatabase();

        List<String> registros = Listar();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros);
        lv_listado.setAdapter(arrayAdapter);

        lv_listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String descripcion = lv_listado.getItemAtPosition(position).toString();
                searchByDescription(descripcion);
            }
        });
    }
}