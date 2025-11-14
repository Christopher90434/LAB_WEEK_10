package com.example.lab_week_10

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val db by lazy { prepareDatabase() }
    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            Log.d("MainActivity", "Creating new Fragment")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FirstFragment.newInstance())
                .commit()
        } else {
            Log.d("MainActivity", "Fragment already exists (rotation)")
        }

        initializeValueFromDatabase()
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        val total = db.totalDao().getTotal(ID)
        if (total.isNotEmpty()) {
            val lastUpdate = total.first().total.date
            Toast.makeText(this, "Last update: $lastUpdate", Toast.LENGTH_LONG).show()
            Log.d("MainActivity", "Last update: $lastUpdate")
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel(){
        viewModel.total.observe(this) {
            Log.d("MainActivity", "Total updated: $it")
            updateText(it)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)
        if (total.isEmpty()) {
            val currentDate = Date().toString()
            db.totalDao().insert(Total(id = 1, total = TotalObject(0, currentDate)))
            Log.d("MainActivity", "Database empty, inserted initial value 0 at $currentDate")
        } else {
            val loadedValue = total.first().total.value
            viewModel.setTotal(loadedValue)
            Log.d("MainActivity", "Loaded from database: $loadedValue")
        }
    }

    override fun onPause() {
        super.onPause()
        val currentTotal = viewModel.total.value ?: 0
        val currentDate = Date().toString()
        db.totalDao().update(Total(ID, TotalObject(currentTotal, currentDate)))
        Log.d("MainActivity", "Saved to database: $currentTotal at $currentDate")
    }

    companion object {
        const val ID: Long = 1
    }
}
