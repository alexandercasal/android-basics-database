package com.alexander.udacity.udacity_pets_sqlite

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem

class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { v ->
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_insert_dummy_data -> {
                return true
            }
            R.id.action_delete_all_entries -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
