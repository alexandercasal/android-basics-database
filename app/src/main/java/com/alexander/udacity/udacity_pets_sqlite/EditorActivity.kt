package com.alexander.udacity.udacity_pets_sqlite

import android.app.AlertDialog
import android.app.LoaderManager
import android.content.ContentValues
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract.PetEntry
import kotlinx.android.synthetic.main.activity_editor.edit_pet_breed
import kotlinx.android.synthetic.main.activity_editor.edit_pet_name
import kotlinx.android.synthetic.main.activity_editor.edit_pet_weight
import kotlinx.android.synthetic.main.activity_editor.spinner_gender

class EditorActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val TAG = EditorActivity::class.java.simpleName
    private val ID_LOADER_PET = 0
    private var mGender = 0
    private var mUri: Uri? = null
    private var mPetHasChanges = false

    private inner class mTouchListener : View.OnTouchListener {
        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            mPetHasChanges = true
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        setupSpinner()
        setEditMode()

        loaderManager.initLoader(ID_LOADER_PET, null, this)
        edit_pet_name.setOnTouchListener(mTouchListener())
        edit_pet_breed.setOnTouchListener(mTouchListener())
        edit_pet_weight.setOnTouchListener(mTouchListener())
        spinner_gender.setOnTouchListener(mTouchListener())
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

    private fun setEditMode() {
        mUri = intent.data

        mUri?.let {
            setTitle(getString(R.string.editor_activity_title_edit_pet))
        } ?: setTitle(getString(R.string.editor_activity_title_new_pet))
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (mUri == null) {
                    insertPet()
                } else {
                    updatePet()
                }
                finish()
                return true
            }
            R.id.action_delete -> return true
            android.R.id.home -> {
                if (!mPetHasChanges) {
                    NavUtils.navigateUpFromSameTask(this)
                    return true
                }

                showUnsavedChangesDialog(object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        NavUtils.navigateUpFromSameTask(this@EditorActivity)
                    }
                })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!mPetHasChanges) {
            super.onBackPressed()
            return
        }

        showUnsavedChangesDialog(object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                finish()
            }
        })
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

        var newPetUri: Uri?
        if (isValidPet(name, breed, gender, weight)) {
            newPetUri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, contentValues)
        } else {
            return
        }

        if (newPetUri != null) {
            Toast.makeText(this, getString(R.string.pet_saved), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.unable_save_pet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePet() {
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

        var updateCount = 0
        if (isValidPet(name, breed, gender, weight)) {
            updateCount = contentResolver.update(
                    mUri,
                    contentValues,
                    null,
                    null
            )
        }

        if (updateCount > 0) {
            //Toast.makeText(this, getString(R.string.pet_updated), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidPet(name: String?, breed: String?, gender: Int?, weight: Int?): Boolean {
        if (!PetEntry.isValidName(name)) {
            Toast.makeText(this, getString(R.string.invalid_pet_name), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!PetEntry.isValidBreed(breed)) {
            Toast.makeText(this, getString(R.string.invalid_pet_breed), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!PetEntry.isValidWeight(weight)) {
            Toast.makeText(this, getString(R.string.invalid_pet_weight), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!PetEntry.isValidGender(gender)) {
            Toast.makeText(this, getString(R.string.invalid_pet_gender), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor>? {
        if (id == ID_LOADER_PET && mUri != null) {
            return CursorLoader(this, mUri, null, null, null, null)
        }

        return null
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        if (loader.id == ID_LOADER_PET) {
            cursor.moveToFirst()
            val petName = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME))
            val petBreed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED))
            val petWeight = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT))
            val petGender = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER))

            edit_pet_name.setText(petName)
            edit_pet_breed.setText(petBreed)
            edit_pet_weight.setText(petWeight.toString())
            spinner_gender.setSelection(petGender)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        edit_pet_name.text.clear()
        edit_pet_breed.text.clear()
        edit_pet_weight.text.clear()
        spinner_gender.setSelection(0)
    }

    private fun showUnsavedChangesDialog(discardButtonClickListener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard, discardButtonClickListener)
        builder.setNegativeButton(R.string.keep_editing, { dialogInterface: DialogInterface, i: Int ->
            if (dialogInterface != null) {
                dialogInterface.dismiss()
            }
        })

        builder.create().show()
    }
}
