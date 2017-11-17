package com.alexander.udacity.udacity_pets_sqlite.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Alexander on 11/16/2017.
 */
class PetDbHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION: Int = 1
        const val DATABASE_NAME = "pets.db"
    }

    private val CREATE_TABLE = """CREATE TABLE ${PetContract.PetEntry.TABLE_NAME} (
        |${PetContract.PetEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        |${PetContract.PetEntry.COLUMN_PET_NAME} TEXT NOT NULL,
        |${PetContract.PetEntry.COLUMN_PET_GENDER} INTEGER NOT NULL,
        |${PetContract.PetEntry.COLUMN_PET_WEIGHT} INTEGER NOT NULL DEFAULT 0);""".trimMargin()
    private val DROP_TABLE = "DROP TABLE ${PetContract.PetEntry.TABLE_NAME});"

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL(DROP_TABLE)
        onCreate(database)
    }
}