package com.alexander.udacity.udacity_pets_sqlite

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract.PetEntry
import com.alexander.udacity.udacity_pets_sqlite.data.PetDbHelper
import kotlinx.android.synthetic.main.activity_editor.edit_pet_breed
import kotlinx.android.synthetic.main.activity_editor.edit_pet_name
import kotlinx.android.synthetic.main.activity_editor.edit_pet_weight
import kotlinx.android.synthetic.main.activity_editor.spinner_gender

class EditorActivity : AppCompatActivity() {

    private val TAG = EditorActivity::class.java.simpleName
    private var mGender = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        setupSpinner()
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinner_gender.adapter = genderSpinnerAdapter
        spinner_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selection = parent.getItemAtPosition(position) as String
                if (selection.isNotEmpty()) {
                    if (selection == getString(R.string.gender_male)) {
                        mGender = PetEntry.GENDER_MALE
                    } else if (selection == getString(R.string.gender_female)) {
                        mGender = PetEntry.GENDER_FEMALE
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                mGender = 0
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                insertPet()
                finish()
                return true
            }
            R.id.action_delete -> return true
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun insertPet() {
        val name = edit_pet_name.text.toString().trim()
        val breed = edit_pet_breed.text.toString().trim()
        val gender = mGender
        var weight = 0

        try {
            weight = edit_pet_weight.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Log.e(TAG, e.message, e)
        }

        val contentValues = ContentValues()
        contentValues.put(PetContract.PetEntry.COLUMN_PET_NAME, name)
        contentValues.put(PetContract.PetEntry.COLUMN_PET_BREED, breed)
        contentValues.put(PetContract.PetEntry.COLUMN_PET_GENDER, gender)
        contentValues.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight)

        var newPetUri: Uri? = null
        if (isValidPet(name, breed, gender, weight)) {
            newPetUri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, contentValues)
        } else {
            Toast.makeText(this, getString(R.string.missing_pet_info), Toast.LENGTH_SHORT).show()
        }

        if (newPetUri != null) {
            Toast.makeText(this, getString(R.string.pet_saved), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.unable_save_pet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidPet(name: String?, breed: String?, gender: Int?, weight: Int?): Boolean {
        if (name?.isEmpty() ?: true) {
            return false
        }
        if (breed?.isEmpty() ?: true) {
            return false
        }
        if (weight == null) {
            return false
        }
        if (gender == null || (gender != null
                && (gender != PetContract.PetEntry.GENDER_FEMALE
                && gender != PetContract.PetEntry.GENDER_MALE
                && gender != PetContract.PetEntry.GENDER_UNKNOWN))) {
            return false
        }

        return true
    }
}
