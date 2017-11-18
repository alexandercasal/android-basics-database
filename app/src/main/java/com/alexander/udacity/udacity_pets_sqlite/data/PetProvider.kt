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
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
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
        if (name?.isEmpty() ?: true) {
            throw IllegalArgumentException("Pet requires a name")
        }
        if (breed?.isEmpty() ?: true) {
            throw IllegalArgumentException("Pet requires a breed")
        }
        if (weight == null) {
            throw IllegalArgumentException("Pet requires a weight")
        }
        if (gender == null || (gender != null
                && (gender != PetContract.PetEntry.GENDER_FEMALE
                && gender != PetContract.PetEntry.GENDER_MALE
                && gender != PetContract.PetEntry.GENDER_UNKNOWN))) {
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
}