package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Base64;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button buttonCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        buttonCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if(!nome.isEmpty()){
                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setSenha(senha);
                        cadastrarUsuario();
                    } else{
                        Toast.makeText(CadastroActivity.this, "Preencha o campo senha", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(CadastroActivity.this, "Preencha o campo email", Toast.LENGTH_SHORT).show();
                }
            } else{
                Toast.makeText(CadastroActivity.this, "Preencha o campo nome", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                usuario.setIdUsuario(idUsuario);
                usuario.salvar();
                finish();
            } else{
                String excecao = "";
                try{
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte";
                } catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um email valido";
                } catch (FirebaseAuthUserCollisionException e){
                    excecao = "Ja existe uma conta criada com esse email";
                } catch (Exception e){
                    excecao = "Erro ao cadastrar usuario" + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
            }
        });
    }
}