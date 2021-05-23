package com.maiko.exame_final.database;

import androidx.room.*;

import com.maiko.exame_final.utils.DateConverter;
import com.maiko.exame_final.model.Conta;

@Database(entities = {Conta.class},
        version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class Connection extends RoomDatabase {

    public abstract ContaDAO getContaDAO();

}
