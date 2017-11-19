package com.alexander.udacity.udacity_pets_sqlite

import android.app.LoaderManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract
import kotlinx.android.synthetic.main.activity_catalog.empty_view
import kotlinx.android.synthetic.main.activity_catalog.list_view_pets

class CatalogActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val TAG = CatalogActivity::class.java.simpleName
    private val ID_PET_LOADER = 0
    private lateinit var mCursorAdapter: PetCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        list_view_pets.emptyView = empty_view
        mCursorAdapter = PetCursorAdapter(this, null)
        list_view_pets.adapter = mCursorAdapter
        list_view_pets.setOnItemClickListener { _, _, pos, id ->
            val intent = Intent(this, EditorActivity::class.java)
            intent.setData(ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id))
            startActivity(intent)
        }

        loaderManager.initLoader(ID_PET_LOADER, null, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()
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

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor>? {
        if (id == ID_PET_LOADER) {
            val projection = arrayOf(
                    PetContract.PetEntry._ID,
                    PetContract.PetEntry.COLUMN_PET_NAME,
                    PetContract.PetEntry.COLUMN_PET_BREED
            )


            val cursorLoader = CursorLoader(this)
            cursorLoader.uri = PetContract.PetEntry.CONTENT_URI
            cursorLoader.projection = projection
            return cursorLoader
        }

        return null
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        if (loader.id == ID_PET_LOADER) {
            mCursorAdapter.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (loader.id == ID_PET_LOADER) {
            mCursorAdapter.swapCursor(null)
        }
    }
}
