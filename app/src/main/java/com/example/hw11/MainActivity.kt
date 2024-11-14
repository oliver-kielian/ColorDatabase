package com.example.hw11

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : AppCompatActivity(), ItemAdapter.ItemAdapterListener {

    private lateinit var recyclerView: RecyclerView

    private lateinit var dbHelper : DatabaseHelper
    private lateinit var adapter : ItemAdapter
    private lateinit var textNum : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toobar = findViewById<Toolbar>(R.id.toolbar)
        recyclerView = findViewById(R.id.colors)
        textNum = findViewById(R.id.textViewNum)


        dbHelper = DatabaseHelper(applicationContext)
        adapter = ItemAdapter(dbHelper.getAllItems(), this)
        adapter.updateCursor(dbHelper.getAllItems())

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        setSupportActionBar(toobar)
        editTextViewNum()
    }

    fun editTextViewNum()
    {
        adapter.itemCount.toString().also { textNum.text = it }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.add ->
            {
                showInsertDialog()
                true
            }
            R.id.sort ->
            {
                showSortDialog()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun showInsertDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        builder.setTitle("Number of Entries")
        builder.setView(input)


        builder.setPositiveButton("Add"){
            dialog, which ->
            fillDatabase(input.text.toString().toInt())

        }
        builder.setNegativeButton("Cancel"){
            dialog, which ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showSortDialog()
    {
        val builder = AlertDialog.Builder(this)
        val sortLayout = layoutInflater.inflate(R.layout.sort_layout, null);
        builder.setView(sortLayout)

        val sortList = sortLayout.findViewById<ListView>(R.id.sortList)
        sortList.setOnItemClickListener { parent, view, position, id ->
            when (position){
                0 ->{
                    sortElements("red")
                }
                1->
                {
                    sortElements("blue")
                }
                2->{
                    sortElements("green")
                }
                3->
                {
                    sortElements("favorite")
                }
                4->{
                    sortElements("default")
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun fillDatabase(n: Int)
    {
        CoroutineScope(Dispatchers.IO).launch {

            var i = 0
            var red : Int
            var blue : Int
            var green : Int
            while (i != n)
            {
                withContext(Dispatchers.Main) {
                    red = Random.nextInt(0, 256)
                    blue = Random.nextInt(0, 256)
                    green = Random.nextInt(0, 256)
                }
                dbHelper.insertItem(red, green, blue)
                i++
            }

            withContext(Dispatchers.Main) {
                adapter.updateCursor(dbHelper.getAllItems())
                editTextViewNum()
            }
        }
    }

    fun sortElements(category : String)
    {
        if(category != "default") {
            adapter.updateCursor(dbHelper.sortColors(category))
        }
        else {
            adapter.updateCursor(dbHelper.getAllItems())
        }
    }

    override fun markAsFavorite(id: Int, checked: Boolean) {
        if(checked) {
            dbHelper.setAsFavorite(id)
        }
        else{
            dbHelper.removeFavorite(id)
        }
    }
}