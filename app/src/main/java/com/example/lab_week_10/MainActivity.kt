package com.example.lab_week_10

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.viewmodels.TotalViewModel

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
        ).allowMainThreadQueries().build()
    }

    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)
        if (total.isEmpty()) {
            db.totalDao().insert(Total(id = 1, total = 0))
            Log.d("MainActivity", "Database empty, inserted initial value 0")
        } else {
            viewModel.setTotal(total.first().total)
            Log.d("MainActivity", "Loaded from database: ${total.first().total}")
        }
    }

    override fun onPause() {
        super.onPause()
        val currentTotal = viewModel.total.value ?: 0
        db.totalDao().update(Total(ID, currentTotal))
        Log.d("MainActivity", "Saved to database: $currentTotal")
    }

    companion object {
        const val ID: Long = 1
    }
}
