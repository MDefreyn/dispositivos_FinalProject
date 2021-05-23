package com.maiko.exame_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maiko.exame_final.database.Connection;
import com.maiko.exame_final.database.ContaDAO;
import com.maiko.exame_final.model.Conta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CadastrarContas extends AppCompatActivity {

    Connection bd;
    ContaDAO contasDAO;
    Conta conta;

    EditText eTxDescricao;
    EditText eTxValor;
    EditText eTxVencimento;
    SwitchCompat sWtPago;
    Button btnCadastrar;
    Button btnVoltar;

    boolean pago;
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dPDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_contas);

        eTxDescricao = findViewById(R.id.eTxDescricao);
        eTxValor = findViewById(R.id.eTxValor);
        eTxVencimento = findViewById(R.id.eTxVencimento);
        sWtPago = findViewById(R.id.sWtPago);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltar = findViewById(R.id.btnVoltar);

        bd = Room.databaseBuilder(getApplicationContext(), Connection.class, "lista_contas")
                .allowMainThreadQueries().build();
        contasDAO = bd.getContaDAO();

        dPDialog = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            atualizaCampo();
        };
        eTxVencimento.setOnClickListener(v -> new DatePickerDialog(CadastrarContas.this, dPDialog,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
        sWtPago.setOnCheckedChangeListener((compoundButton, check) -> pago = check);
        btnCadastrar.setOnClickListener(v -> cadastrarConta());
        btnVoltar.setOnClickListener(v -> voltarMain(0));
    }

    private void atualizaCampo() {
        eTxVencimento.setText(sdf.format(calendar.getTime()));
    }

    public void cadastrarConta() {
        if (!eTxDescricao.getText().toString().isEmpty() && !eTxValor.getText().toString().isEmpty() &&
                !eTxVencimento.getText().toString().isEmpty()) {
            Date vencimento = calendar.getTime();
            String descricao = eTxDescricao.getText().toString();
            double valor = Double.parseDouble(eTxValor.getText().toString());
            conta = new Conta(vencimento, descricao, valor, pago);
            contasDAO.save(conta);
            new AlertDialog.Builder(CadastrarContas.this)
                    .setTitle("Cadastro completo")
                    .setMessage("Conta cadastrada com sucesso!")
                    .show();
            voltarMain(2000);
        } else {
            Toast.makeText(CadastrarContas.this, "Informe todos os campos necessários",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void voltarMain(long espera) {
        Intent it = new Intent(this, MainActivity.class);
        handler.postDelayed(() -> startActivity(it), espera);
    }

}