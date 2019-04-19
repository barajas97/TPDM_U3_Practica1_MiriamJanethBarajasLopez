package mx.edu.ittepic.miriambarajas.tpdm_u3_practica2_miriamjanethbarajaslopez;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {

    EditText nombreL, razaL, edadL, sexoL;
    RadioButton desSi, desNo;
    Button ins, eli, con, act;
    DatabaseReference servicioRealtime;
    ListView lista;
    List<Perros> datosPerros;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        nombreL = findViewById(R.id.nombre);
        razaL = findViewById(R.id.raza);
        edadL = findViewById(R.id.edad);
        sexoL = findViewById(R.id.sexo);
        desSi = findViewById(R.id.sides);
        desNo = findViewById(R.id.nodes);
        ins = findViewById(R.id.insertar);
        eli = findViewById(R.id.eliminar);
        con = findViewById(R.id.consultar);
        act = findViewById(R.id.actualizar);
        lista = findViewById(R.id.listaPerros);

        servicioRealtime = FirebaseDatabase.getInstance().getReference();

        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarPerro();
            }
        });

        eli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPerro();
            }
        });

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarDatos();
            }
        });
        
        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDato();
            }
        });
    }

    private void actualizarDato() {
        if(nombreL.getText().toString().isEmpty() || edadL.getText().toString().isEmpty() || razaL.getText().toString().isEmpty()
                || sexoL.getText().toString().isEmpty()){
            AlertDialog.Builder alerta = new AlertDialog.Builder(Main3Activity.this);
            alerta.setTitle("Alerta")
                    .setMessage("Por favor llene los campos para poder modificar")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;

        }
        final Map<String, Object> datoMap = new HashMap<>();
        datoMap.put("nombre", nombreL.getText().toString());
        datoMap.put("edad", Integer.parseInt(edadL.getText().toString()));
        datoMap.put("raza", razaL.getText().toString());
        datoMap.put("sexo", sexoL.getText().toString());
        datoMap.put("desparacitado", desSi.isChecked());

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText nombrep = new EditText(this);
        nombrep.setHint("Nombre de perro");
        alerta.setTitle("Actualizar")
                .setMessage("Nombre de perro a actualizar:")
                .setView(nombrep).setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(nombrep.getText().toString().isEmpty()){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(Main3Activity.this);
                    alerta.setTitle("Atención")
                            .setMessage("Ingrese un nombre de perro:")
                            .setPositiveButton("Aceptar",null)
                            .show();
                    return;
                }
                actualizar(nombrep.getText().toString(), datoMap);
            }
        }).setNegativeButton("Cancelar", null).show();

    }

    private void actualizar(String p, Map<String,Object> datoMap) {
        servicioRealtime.child("perros").child(p).updateChildren(datoMap);
    }

    private void eliminarPerro() {

    }

    private void eliminarDato(String s) {
        servicioRealtime.child("perros").child(s).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this, "Dato eliminado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this, "No hay concidencias", Toast.LENGTH_LONG).show();
                    }
                });
        return;
    }

    private void insertarPerro() {
        if(desSi.isChecked()){
            Perros nuevo = new Perros(nombreL.getText().toString(), razaL.getText().toString(), sexoL.getText().toString(),Integer.parseInt(edadL.getText().toString()), true);
            servicioRealtime.child("perros").child(nombreL.getText().toString()).push().setValue(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main3Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            nombreL.setText("");
                            razaL.setText("");
                            edadL.setText("");
                            sexoL.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main3Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }if(desNo.isChecked()){
            Perros nuevo = new Perros(nombreL.getText().toString(), razaL.getText().toString(), sexoL.getText().toString(),Integer.parseInt(edadL.getText().toString()), false);
            servicioRealtime.child("perros").child(nombreL.getText().toString()).push().setValue(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main3Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            nombreL.setText("");
                            razaL.setText("");
                            edadL.setText("");
                            sexoL.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main3Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }else{
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Atención")
                    .setMessage("Indique si el perro esta desparacitado")
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }

    private void consultarDatos() {
        servicioRealtime.child("perros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datosPerros = new ArrayList<>();

                servicioRealtime.child("perros").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap: dataSnapshot.getChildren()){
                            Perros perros = snap.getValue(Perros.class);
                            if(perros!=null){
                                datosPerros.add(perros);
                            }//if

                        }//for
                        crearListView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void crearListView() {
        if(datosPerros.size()<=0){
            return;
        }

        String[] nombres = new String[datosPerros.size()];
        for(int i =0; i<nombres.length;i++){
            Perros p = datosPerros.get(i);
            nombres[i] =p.nombre;
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adaptador);
    }

}
