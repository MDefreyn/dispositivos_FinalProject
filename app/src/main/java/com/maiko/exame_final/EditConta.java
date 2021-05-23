package com.maiko.exame_final;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import com.maiko.exame_final.database.Connection;
import com.maiko.exame_final.database.ContaDAO;
import com.maiko.exame_final.model.Conta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditConta extends AppCompatActivity {

    Connection bd;
    ContaDAO contasDAO;
    Conta conta;

    TextView tVwDescricao;
    EditText eTxValor;
    EditText eTxVencimento;
    SwitchCompat sWtPago;
    Button btnSalvar;
    Button btnVoltar;
    Button btnDeletar;

    boolean pago;
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dPDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
    DecimalFormat df = new DecimalFormat("0.00");
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_conta);

        tVwDescricao = findViewById(R.id.tVwDescricao);
        eTxVencimento = findViewById(R.id.eTxVencimento);
        eTxValor = findViewById(R.id.eTxValor);
        sWtPago = findViewById(R.id.sWtPago);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnDeletar = findViewById(R.id.btnDeletar);

        bd = Room.databaseBuilder(getApplicationContext(), Connection.class, "lista_contas")
                .allowMainThreadQueries().build();
        contasDAO = bd.getContaDAO();

        long idConta = (long) getIntent().getSerializableExtra("conta");
        conta = contasDAO.findById(idConta);

        tVwDescricao.setText(conta.getDescricao());
        eTxVencimento.setText(sdf.format(conta.getVencimento()));
        eTxValor.setText(df.format(conta.getValor()));
        sWtPago.setChecked(conta.isPago());

        calendar.setTime(conta.getVencimento());
        dPDialog = (view, year, month, dayOfMonth) -> atualizaCampo();
        eTxVencimento.setOnClickListener(v -> new DatePickerDialog(EditConta.this, dPDialog,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
        sWtPago.setOnCheckedChangeListener((compoundButton, check) -> pago = check);
        btnSalvar.setOnClickListener(v -> salvar());
        btnVoltar.setOnClickListener(v -> voltarMain(0));
        btnDeletar.setOnClickListener(v -> deletar());
    }

    private void atualizaCampo() {
        eTxVencimento.setText(sdf.format(calendar.getTime()));
    }

    public void deletar() {
        new AlertDialog.Builder(EditConta.this)
                .setTitle("Deletar conta")
                .setMessage("Deseja deletar a conta " + conta.getDescricao() + " ?" +
                        "\nEssa ação não pode ser desfeita!")
                .setPositiveButton("Sim", (dialog, which) -> {
                    contasDAO.remove(conta);
                    voltarMain(1000);
                })
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void salvar() {
        conta.setVencimento(calendar.getTime());
        conta.setValor(Double.parseDouble(eTxValor.getText().toString()));
        conta.setPago(pago);
        contasDAO.update(conta);
        new AlertDialog.Builder(EditConta.this)
                .setTitle("Atualização concluída")
                .setMessage("A conta " + conta.getDescricao() + " foi atualizada!")
                .show();
        voltarMain(2000);
    }

    private void voltarMain(long espera) {
        Intent it = new Intent(this, MainActivity.class);
        handler.postDelayed(() -> startActivity(it), espera);
    }

}
