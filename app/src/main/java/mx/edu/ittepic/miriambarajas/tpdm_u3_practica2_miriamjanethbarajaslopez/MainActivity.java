package mx.edu.ittepic.miriambarajas.tpdm_u3_practica2_miriamjanethbarajaslopez;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    Button seleccion;
    RadioButton maquillajes, perros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seleccion = findViewById(R.id.aceptar);
        maquillajes = findViewById(R.id.maquillajes);
        perros = findViewById(R.id.perro);

        seleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maquillajes.isChecked()){
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    finish();
                    return;
                }if(perros.isChecked()){
                    startActivity(new Intent(MainActivity.this, Main3Activity.class));
                    finish();
                    return;
                }else{
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("Atención")
                            .setMessage("Favor de seleccionar alguna opción")
                            .setPositiveButton("Aeptar", null)
                            .show();
                    return;
                }
            }
        });
    }
}
