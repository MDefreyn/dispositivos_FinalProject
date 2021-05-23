package com.maiko.exame_final.model;

import androidx.room.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Entity
public class Conta {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private Date vencimento;
    private String descricao;
    private double valor;
    private boolean pago;

    @Ignore
    public Conta() {
    }

    public Conta(Date vencimento, String descricao, double valor, boolean pago) {
        this.vencimento = vencimento;
        this.descricao = descricao;
        this.valor = valor;
        this.pago = pago;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return id == conta.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
        DecimalFormat df = new DecimalFormat("0.00");
        String pgto = "À pagar!";
        if (pago) {
            pgto = "Pago!";
        }
        return "Descrição: " + descricao +
                " R$:" + df.format(valor) +
                "\n" + sdf.format(vencimento)
                + " - " + pgto;
    }

}
