package com.example.gestordecontraseas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class UserActivity extends AppCompatActivity {

    private EditText et_newPass, et_confirmPass;
    private TextView txt_status;
    private boolean mostrarPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_newPass = (EditText)findViewById(R.id.et_newPass);
        et_confirmPass = (EditText)findViewById(R.id.et_confirmPass);
        txt_status = (TextView)findViewById(R.id.txtStatus);
        checkStatus();
    }

    // Checar estado de usuario
    public void checkStatus(){
        AdminSqliteOpenHelper manager = ManagerActivity.database(this);
        SQLiteDatabase db = manager.getWritableDatabase();

        Cursor pass = db.rawQuery("SELECT password FROM user", null);

        if (pass.moveToFirst()){
            String pass_DB = pass.getString(0);
            db.close();

            if ( pass_DB.isEmpty() ){
                txt_status.setText("DESPROTEGIDO");
            }else{
                txt_status.setText("PROTEGIDO");
            }
        }
    }

    // Salvando cambios de clave
    public void SavePassword(View view) {
        String newPass = et_newPass.getText().toString();
        String confirmPass = et_confirmPass.getText().toString();

        if (newPass.equals(confirmPass)) {
            AdminSqliteOpenHelper manager = ManagerActivity.database(this);
            SQLiteDatabase db = manager.getWritableDatabase();

            ContentValues registro = new ContentValues();
            registro.put("password", newPass);

            int count = db.update("user", registro, "id=1", null);
            db.close();

            if (count == 1) {
                Toast.makeText(this, "Datos de la clave actualizados correctamente. ", Toast.LENGTH_SHORT).show();
                et_newPass.setText("");
                et_confirmPass.setText("");
                finish();
            } else {
                Toast.makeText(this, "Ha ocurrido un error al actualizar la clave. ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Las claves no coinciden. ", Toast.LENGTH_SHORT).show();
        }
    }

    public void showPassword (View view){
        if (mostrarPassword) {
            et_newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_confirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mostrarPassword = false;
        } else {
            // Mostrar la contraseña
            et_newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            et_confirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mostrarPassword = true;
        }

        et_newPass.setSelection(et_confirmPass.getText().length());
    }

    public void back(View view){
        finish();
    }

}