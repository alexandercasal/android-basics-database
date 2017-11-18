package com.alexander.udacity.udacity_pets_sqlite.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class PetProvider : ContentProvider() {

    private val TAG = PetProvider::class.java.simpleName
    private lateinit var mDbHelper: PetDbHelper

    companion object {
        const val PETS = 100
        const val PET_ID = 1001
        val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS)
            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, "${PetContract.PATH_PETS}/#", PET_ID)
        }
    }

    override fun onCreate(): Boolean {
        mDbHelper = PetDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor {
        val db = mDbHelper.readableDatabase
        val cursor: Cursor

        val match = sUriMatcher.match(uri)
        when (match) {
            PETS -> {
                cursor = db.query(
                        PetContract.PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            PET_ID -> {
                val select = "${PetContract.PetEntry._ID} = ?"
                val args = arrayOf(ContentUris.parseId(uri).toString())
                cursor = db.query(
                        PetContract.PetEntry.TABLE_NAME,
                        projection,
                        select,
                        args,
                        null,
                        null,
                        sortOrder
                )
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI " + uri)
        }

        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues): Uri? {
        val match = sUriMatcher.match(uri)

        when (match) {
            PETS -> {
                return insertPet(uri, contentValues)
            }
            else -> throw IllegalArgumentException("Insertion is not supported for " + uri)
        }
    }

    override fun update(uri: Uri, contentValues: ContentValues, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val match = sUriMatcher.match(uri)
        when (match) {
            PETS -> return updatePet(uri, contentValues, selection, selectionArgs)
            PET_ID -> {
                val select = "${PetContract.PetEntry._ID} = ?"
                val args = arrayOf(ContentUris.parseId(uri).toString())
                return updatePet(uri, contentValues, select, args)
            }
            else -> throw IllegalArgumentException("Update is not supported for ${uri}")
        }

        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mDbHelper.writableDatabase
        val match = sUriMatcher.match(uri)

        when (match) {
            PETS -> return db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs)
            PET_ID -> {
                val select = "${PetContract.PetEntry._ID} = ?"
                val args = arrayOf(ContentUris.parseId(uri).toString())
                return db.delete(PetContract.PetEntry.TABLE_NAME, select, args)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }

        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    private fun insertPet(uri: Uri, contentValues: ContentValues): Uri? {
        val name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME)
        val breed = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_BREED)
        val weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT)
        val gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER)
        if (!PetContract.PetEntry.isValidName(name)) {
            throw IllegalArgumentException("Pet requires a name")
        }
        if (!PetContract.PetEntry.isValidBreed(breed)) {
            throw IllegalArgumentException("Pet requires a breed")
        }
        if (!PetContract.PetEntry.isValidWeight(weight)) {
            throw IllegalArgumentException("Pet requires a weight")
        }
        if (!PetContract.PetEntry.isValidGender(gender)) {
            throw IllegalArgumentException("Invalid pet gender: $gender")
        }

        val db = mDbHelper.writableDatabase
        val newPetID = db.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues)

        if (newPetID != -1L) {
            return ContentUris.withAppendedId(uri, newPetID)
        } else {
            return null
        }
    }

    private fun updatePet(uri: Uri, contentValues: ContentValues, selection: String?,
                          selectionArgs: Array<String>?): Int {
        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            val name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME)
            if (!PetContract.PetEntry.isValidName(name)) {
                throw IllegalArgumentException("Pet requires a name")
            }
        }
        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            val weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT)
            if (!PetContract.PetEntry.isValidWeight(weight)) {
                throw IllegalArgumentException("Pet requires a weight")
            }
        }
        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)) {
            val gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER)
            if (!PetContract.PetEntry.isValidGender(gender)) {
                throw IllegalArgumentException("Invalid pet gender")
            }
        }

        if (contentValues.size() == 0) {
            return 0
        }

        val db = mDbHelper.writableDatabase
        return db.update(PetContract.PetEntry.TABLE_NAME, contentValues, selection, selectionArgs)
    }
}