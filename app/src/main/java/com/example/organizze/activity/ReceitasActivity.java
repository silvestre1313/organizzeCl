package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText editData, editDescricao, editCategoria;
    private EditText editValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);
        editData = findViewById(R.id.editData);
        editDescricao = findViewById(R.id.editDescricao);
        editCategoria = findViewById(R.id.editCategoria);
        editValor = findViewById(R.id.editValor);

        editData.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();
    }

    public void salvarReceita(View view){

        movimentacao = new Movimentacao();
        if (validarCamposReceita()){
            String data = editData.getText().toString();
            double valorRecebido = Double.parseDouble(editValor.getText().toString());
            movimentacao.setValor(valorRecebido);
            movimentacao.setCategoria(editCategoria.getText().toString());
            movimentacao.setData(data);
            movimentacao.setDescricao(editDescricao.getText().toString());
            movimentacao.setTipo("receita");
            double receitaAtualizada = valorRecebido + receitaTotal;
            atualizarReceitaTotal(receitaAtualizada);
            movimentacao.salvar(data);
            finish();
        }
    }

    public boolean validarCamposReceita(){

        String valor = editValor.getText().toString();
        String data = editData.getText().toString();
        String categoria = editCategoria.getText().toString();
        String descricao = editDescricao.getText().toString();

        if(!valor.isEmpty()){
            if(!data.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!descricao.isEmpty()){
                        return true;
                    } else{
                        Toast.makeText(ReceitasActivity.this, "Digite a descrição", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else{
                    Toast.makeText(ReceitasActivity.this, "Digite a categoria", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else{
                Toast.makeText(ReceitasActivity.this, "Digite a data", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else{
            Toast.makeText(ReceitasActivity.this, "Digite o valor", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceitaTotal(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebase.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarReceitaTotal(double receita){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebase.child("usuarios").child(idUsuario);
        usuarioRef.child("receitaTotal").setValue(receita);
    }
}