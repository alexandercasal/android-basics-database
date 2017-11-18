package com.alexander.udacity.udacity_pets_sqlite

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract

class CatalogActivity : AppCompatActivity() {

    private val TAG = CatalogActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }
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
        val contentValues = ContentValues()
        contentValues.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto")
        contentValues.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier")
        contentValues.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE)
        contentValues.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7)

        val newPetUri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, contentValues)

        if (newPetUri != null) {
            Toast.makeText(this, getString(R.string.pet_saved), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.unable_save_pet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayDatabaseInfo() {
        val projection = arrayOf(
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT
        )

        val cursor = contentResolver.query(
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        )

        try {
            val idIndex = cursor.getColumnIndex(PetContract.PetEntry._ID)
            val nameIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)
            val breedIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)
            val weightIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT)

            val displayView = findViewById<TextView>(R.id.text_view_pet)
            val displayBuilder = StringBuilder()
            displayBuilder.append("Num rows in pets database table: ${cursor.count}\n")
            while (cursor.moveToNext()) {
                displayBuilder.append(
                        "${cursor.getLong(idIndex)} - ${cursor.getString(nameIndex)} - " +
                                "${cursor.getString(breedIndex)} - ${cursor.getInt(weightIndex)}\n"
                )
            }
            displayView.text = displayBuilder.toString()
        } finally {
            cursor.close()
        }
    }
}
