package com.alexander.udacity.udacity_pets_sqlite

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract
import com.alexander.udacity.udacity_pets_sqlite.data.PetDbHelper

class CatalogActivity : AppCompatActivity() {

    private val TAG = CatalogActivity::class.java.simpleName
    private lateinit var mDBHelper: PetDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        mDBHelper = PetDbHelper(this)
    }

    override fun onStart() {
        super.onStart()
        displayDatabaseInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()
                displayDatabaseInfo()
                return true
            }
            R.id.action_delete_all_entries -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun insertPet() {
        val db = mDBHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto")
        contentValues.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier")
        contentValues.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE)
        contentValues.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7)

        val newRowID = db.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues)
        Log.v(TAG, "New row ID = $newRowID")
    }

    private fun displayDatabaseInfo() {
        val db = mDBHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${PetContract.PetEntry.TABLE_NAME}", null)
        try {
            val displayView = findViewById<TextView>(R.id.text_view_pet)
            displayView.text = "Num rows in pets database table: ${cursor.count}"
        } finally {
            cursor.close()
        }
    }
}
