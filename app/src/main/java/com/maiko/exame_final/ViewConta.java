package com.maiko.exame_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.maiko.exame_final.database.Connection;
import com.maiko.exame_final.database.ContaDAO;
import com.maiko.exame_final.model.Conta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewConta extends AppCompatActivity {

    Connection bd;
    ContaDAO contasDAO;
    Conta conta;

    TextView tVwDescricao;
    TextView tVwVencimento;
    TextView tVwValor;
    TextView tVwPagamento;
    Button btnEditar;
    Button btnVoltar;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_conta);

        tVwDescricao = findViewById(R.id.tVwDescricao);
        tVwVencimento = findViewById(R.id.tVwVencimento);
        tVwValor = findViewById(R.id.tVwValor);
        tVwPagamento = findViewById(R.id.tVwPagamento);
        btnEditar = findViewById(R.id.btnEditar);
        btnVoltar = findViewById(R.id.btnVoltar);

        bd = Room.databaseBuilder(getApplicationContext(), Connection.class, "lista_contas")
                .allowMainThreadQueries().build();
        contasDAO = bd.getContaDAO();

        long idConta = (long) getIntent().getSerializableExtra("conta");
        conta = contasDAO.findById(idConta);

        String pgto = "À pagar";
        if (conta.isPago()) {
            pgto = "Pagamento confirmado!";
        }

        String vencimento = "Vencimento: " + sdf.format(conta.getVencimento());
        String valor = "Valor: R$" + df.format(conta.getValor());

        tVwDescricao.setText(conta.getDescricao());
        tVwVencimento.setText(vencimento);
        tVwValor.setText(valor);
        tVwPagamento.setText(pgto);
        btnEditar.setOnClickListener(v -> editar());
        btnVoltar.setOnClickListener(v -> voltar());
    }

    public void editar() {
        Intent it = new Intent(this, EditConta.class);
        it.putExtra("conta", conta.getId());
        startActivity(it);
    }

    public void voltar() {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

}