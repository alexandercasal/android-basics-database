package com.alexander.udacity.udacity_pets_sqlite.data

import android.content.ContentProvider
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

    override fun query(uri: Uri, projection: Array<String>, selection: String,
                       selectionArgs: Array<String>, sortOrder: String): Cursor? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues): Uri? {
        return null
    }

    override fun update(uri: Uri, contentValues: ContentValues, selection: String,
                        selectionArgs: Array<String>): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String, selectionArgs: Array<String>): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}