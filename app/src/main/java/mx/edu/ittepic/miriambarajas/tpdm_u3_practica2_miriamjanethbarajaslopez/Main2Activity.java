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

public class Main2Activity extends AppCompatActivity {

    EditText fabri, prod, cat;
    RadioButton conS, sinS;
    Button ins, con, eli, act;
    DatabaseReference servicioRealtime;
    ListView lista;
    List<Maquillajes> datosMaquillajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fabri = findViewById(R.id.fabricante);
        prod = findViewById(R.id.lote);
        cat = findViewById(R.id.categoria);
        conS = findViewById(R.id.sistock);
        sinS = findViewById(R.id.nostock);
        ins = findViewById(R.id.insertar);
        con = findViewById(R.id.consultar);
        eli = findViewById(R.id.eliminar);
        act = findViewById(R.id.actualizar);
        lista = findViewById(R.id.listaDatos);
        
        servicioRealtime = FirebaseDatabase.getInstance().getReference();
        
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fabri.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }if(prod.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }if(cat.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }
                insertarMaquillaje();
            }
        });
        
        eli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarMaquillaje();
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
        if(fabri.getText().toString().isEmpty() || prod.getText().toString().isEmpty() || cat.getText().toString().isEmpty()){
            AlertDialog.Builder alerta = new AlertDialog.Builder(Main2Activity.this);
            alerta.setTitle("Alerta")
                    .setMessage("Por favor llene los campos para poder modificar")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;

        }
        final Map<String, Object> datoMap = new HashMap<>();
        datoMap.put("noLote", Integer.parseInt(prod.getText().toString()));
        datoMap.put("nombreFab", fabri.getText().toString());
        datoMap.put("tipoCat", cat.getText().toString());
        datoMap.put("disponibilidad", conS.isChecked());

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText lote = new EditText(this);
        lote.setHint("Número de lote");
        lote.setInputType(InputType.TYPE_CLASS_NUMBER);
        alerta.setTitle("Actualizar")
                .setMessage("Número de lote a actualizar:")
                .setView(lote).setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(lote.getText().toString().isEmpty()){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(Main2Activity.this);
                    alerta.setTitle("Atención")
                            .setMessage("Ingrese un número de lote:")
                            .setPositiveButton("Aceptar",null)
                            .show();
                    return;
                }
                actualizar(lote.getText().toString(), datoMap);
            }
        }).setNegativeButton("Cancelar", null).show();

    }

    private void actualizar(String p, Map<String,Object> datoMap) {
        servicioRealtime.child("maquillajes").child(p).updateChildren(datoMap);
    }

    private void consultarDatos() {
        servicioRealtime.child("maquillajes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datosMaquillajes = new ArrayList<>();

                servicioRealtime.child("maquillajes").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap: dataSnapshot.getChildren()){
                            Maquillajes maquillajes = snap.getValue(Maquillajes.class);
                            if(maquillajes!=null){
                                datosMaquillajes.add(maquillajes);
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
        if(datosMaquillajes.size()<=0){
            return;
        }

        String[] nombres = new String[datosMaquillajes.size()];
        for(int i =0; i<nombres.length;i++){
            Maquillajes m = datosMaquillajes.get(i);
            nombres[i] =m.nombreFab;
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adaptador);
    }

    private void insertarMaquillaje() {
        if(conS.isChecked()){
            Maquillajes nuevo = new Maquillajes(Integer.parseInt(prod.getText().toString()),
                    fabri.getText().toString(), true, cat.getText().toString());
            servicioRealtime.child("maquillajes").child(prod.getText().toString()).push().setValue(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main2Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            prod.setText("");
                            cat.setText("");
                            fabri.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main2Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }if(sinS.isChecked()){
            Maquillajes nuevo = new Maquillajes(Integer.parseInt(prod.getText().toString()),
                    fabri.getText().toString(), true, cat.getText().toString());
            servicioRealtime.child("maquillajes").child(prod.getText().toString()).push().setValue(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main2Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            prod.setText("");
                            cat.setText("");
                            fabri.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main2Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }else{
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Atención")
                    .setMessage("Por favor seleccione la disponibilidad")
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }
    
    private void eliminarMaquillaje(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText lote = new EditText(this);
        lote.setHint("Número de lote");
        lote.setInputType(InputType.TYPE_CLASS_NUMBER);
        alerta.setTitle("Eliminar")
                .setMessage("Lote a eliminar:")
                .setView(lote).setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(lote.getText().toString().isEmpty()){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(Main2Activity.this);
                    alerta.setTitle("Atención")
                            .setMessage("Ingrese un número de lote")
                            .setPositiveButton("Aceptar",null)
                            .show();
                    return;
                }
                eliminarDato(lote.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show(); 
    }

    private void eliminarDato(String s) {
        servicioRealtime.child("maquillajes").child(s).removeValue()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Main2Activity.this, "Dato eliminado", Toast.LENGTH_LONG).show();
                }
            })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Toast.makeText(Main2Activity.this, "No hay concidencias", Toast.LENGTH_LONG).show();
                 }
             });
        return;
    }
}
