package com.example.gestordecontraseas;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.gestordecontraseas.ManagerActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_password = (EditText)findViewById(R.id.et_password);
    }

    // Iniciar sesión
    public void Login(View view){
        String intro_pass = et_password.getText().toString();
        Intent managerView = new Intent (this, ManagerActivity.class);

        try {
            AdminSqliteOpenHelper manager = ManagerActivity.database(this);
            SQLiteDatabase db = manager.getWritableDatabase();
            Cursor pass = db.rawQuery("SELECT password FROM user", null);

            if (pass.moveToFirst()){
                String pass_DB = pass.getString(0);
                db.close();

                if(pass_DB.equals(intro_pass)){
                    et_password.setText("");
                    startActivity(managerView);
                    finish();
                }else {
                    Toast.makeText(this, "Clave Incorrecta, intente de nuevo. ", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar sesión. Intente de nuevo. ", Toast.LENGTH_LONG).show();
        }
    }
}