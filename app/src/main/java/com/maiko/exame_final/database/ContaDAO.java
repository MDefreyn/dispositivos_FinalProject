package com.maiko.exame_final.database;

import androidx.room.*;

import com.maiko.exame_final.model.Conta;

import java.util.List;

@Dao
public interface ContaDAO {

    @Insert
    void save(Conta conta);

    @Update
    void update(Conta conta);

    @Delete
    void remove(Conta conta);

    @Query("SELECT * FROM conta ORDER BY vencimento")
    List<Conta> findAll();

    @Query("SELECT * FROM conta WHERE id = :id")
    Conta findById(long id);

}
