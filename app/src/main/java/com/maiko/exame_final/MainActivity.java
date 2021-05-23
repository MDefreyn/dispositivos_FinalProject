package com.maiko.exame_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.maiko.exame_final.database.Connection;
import com.maiko.exame_final.database.ContaDAO;
import com.maiko.exame_final.model.Conta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemLongClickListener {

    Connection bd;
    ContaDAO contasDAO;

    ListView lVwContas;
    Button btnAdicionar;

    ArrayAdapter<Conta> adapter;
    List<Conta> listaContas;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lVwContas = findViewById(R.id.lVwContas);
        btnAdicionar = findViewById(R.id.btnAdicionar);

        bd = Room.databaseBuilder(getApplicationContext(), Connection.class, "lista_contas").allowMainThreadQueries().build();
        contasDAO = bd.getContaDAO();

        listarContas();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaContas);
        lVwContas.setAdapter(adapter);
        lVwContas.setOnItemLongClickListener(this);

        btnAdicionar.setOnClickListener(v -> adicionar());
    }

    public void adicionar() {
        Intent it = new Intent(this, CadastrarContas.class);
        startActivity(it);
    }

    private void listarContas() {
        if (listaContas == null) {
            listaContas = new ArrayList<>(200);
        } else {
            listaContas.clear();
        }
        List<Conta> listaBase = contasDAO.findAll();
        listaContas.addAll(listaBase);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Conta conta = listaContas.get(position);
        String pgto = "À pagar";
        if (conta.isPago()) {
            pgto = "Pagamento confirmado!";
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Conta: " + conta.getDescricao())
                .setMessage("Valor: R$" + df.format(conta.getValor()) + "\tVencimento: " + sdf.format(conta.getVencimento())
                        + "\n" + pgto)
                .setPositiveButton("Visualizar", (dialog, which) -> visualizarConta(conta))
                .setNegativeButton("Editar", (dialog, which) -> editarConta(conta))
                .setNeutralButton("Pago?", (dialog, which) -> {
                    if (conta.isPago()) {
                        Toast.makeText(MainActivity.this, "A conta já está paga!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Pagamento realizado!",
                                Toast.LENGTH_SHORT).show();
                        conta.setPago(true);
                        contasDAO.update(conta);
                        listarContas();
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();
        return true;
    }

    private void editarConta(Conta conta) {
        Intent it = new Intent(this, EditConta.class);
        it.putExtra("conta", conta.getId());
        startActivity(it);
    }

    private void visualizarConta(Conta conta) {
        Intent it = new Intent(this, ViewConta.class);
        it.putExtra("conta", conta.getId());
        startActivity(it);
    }

}