package com.example.flutty

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LaguHapus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lagu_hapus)

        val id_lagu_terpilih:String = intent.getStringExtra("id_lagu_terpilih").toString()

        val db: SQLiteDatabase = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val query = db.rawQuery("DELETE FROM lagurock WHERE id_rock ='$id_lagu_terpilih'", null)
        query.moveToNext()

        val pindah = Intent(this, LaguRock::class.java)
        startActivity(pindah)
    }
}