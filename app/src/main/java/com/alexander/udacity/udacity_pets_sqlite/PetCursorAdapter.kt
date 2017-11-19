package com.alexander.udacity.udacity_pets_sqlite

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.alexander.udacity.udacity_pets_sqlite.data.PetContract

/**
 * Created by Alexander on 11/18/2017.
 */
class PetCursorAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return v
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val name = view.findViewById<TextView>(R.id.name)
        val summary = view.findViewById<TextView>(R.id.summary)

        val petName = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME))
        val petBreed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED))

        name.text = petName
        summary.text = petBreed
    }
}