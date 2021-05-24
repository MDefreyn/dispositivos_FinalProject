package com.maiko.exame_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.maiko.exame_final.database.Connection;
import com.maiko.exame_final.database.ContaDAO;
import com.maiko.exame_final.model.Conta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemLongClickListener {

    Connection bd;
    ContaDAO contasDAO;

    EditText eTxFiltro;
    Button btnAdicionar;
    ListView lVwContas;

    ArrayAdapter<Conta> adapter;
    ArrayList<Conta> listaContas;

    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dPDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTxFiltro = findViewById(R.id.eTxFiltro);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        lVwContas = findViewById(R.id.lVwContas);

        bd = Room.databaseBuilder(getApplicationContext(), Connection.class, "lista_contas").allowMainThreadQueries().build();
        contasDAO = bd.getContaDAO();

        listarContas();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaContas);
        lVwContas.setAdapter(adapter);
        lVwContas.setOnItemLongClickListener(this);
        dPDialog = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            atualizaCampo();
        };
        eTxFiltro.setOnClickListener(v -> new DatePickerDialog(MainActivity.this, dPDialog,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
        eTxFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnAdicionar.setOnClickListener(v -> adicionar());
    }

    private void filtrar() {
        Date filtrado = calendar.getTime();
        Date conta;
        ArrayList<Conta> lstFiltrados = new ArrayList<>();
        listarContas();
        for (int i = 0; i < listaContas.size(); i++) {
            conta = listaContas.get(i).getVencimento();
            if (filtrado.compareTo(conta) >= 0) {
                lstFiltrados.add(listaContas.get(i));
            }
        }
        listaContas.clear();
        listaContas.addAll(lstFiltrados);
    }

    private void atualizaCampo() {
        eTxFiltro.setText(sdf.format(calendar.getTime()));
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