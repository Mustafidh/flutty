package com.example.flutty

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayInputStream

class LaguRock : AppCompatActivity() {

    private lateinit var rv_lagurock: RecyclerView
    private lateinit var mi: LaguItem
    private val id_rock: MutableList<String> = mutableListOf()
    private val album: MutableList<ByteArray> = mutableListOf()
    private val jlagu: MutableList<String> = mutableListOf()
    private val artis: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lagu_rock)

        rv_lagurock = findViewById(R.id.rv_lagurock)

        val btn_tambah: ImageView = findViewById(R.id.btn_tambah)
        btn_tambah.setOnClickListener {
            val pindah: Intent = Intent(this, LaguTambahR::class.java)
            startActivity(pindah)
        }
    }

    override fun onResume() {
        super.onResume()
        // Bersihkan data sebelum memuat data terbaru
        id_rock.clear()
        album.clear()
        jlagu.clear()
        artis.clear()

        loadDataFromDatabase()

        // Inisialisasi adapter dan layout manager
        mi = LaguItem(this, id_rock, album, jlagu, artis)
        rv_lagurock.adapter = mi
        rv_lagurock.layoutManager = LinearLayoutManager(this)
    }

    private fun loadDataFromDatabase() {
        val db: SQLiteDatabase = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val kumpul_rock = db.rawQuery("SELECT id_rock, jlagu, artis, album FROM lagurock", null)
        while (kumpul_rock.moveToNext()) {
            var gambarByteArray: ByteArray? = null
            if (kumpul_rock.getBlob(3) != null) {
                gambarByteArray = kumpul_rock.getBlob(3)
            } else {
                // Jika gambar tidak ada, bisa diisi dengan default ByteArray atau null
                // Contoh: gambarByteArray = defaultByteArray
                // atau biarkan null jika ingin menangani ini di bagian tampilan
            }
            id_rock.add(kumpul_rock.getString(0))
            album.add(gambarByteArray?: ByteArray(0))
            jlagu.add(kumpul_rock.getString(1))
            artis.add(kumpul_rock.getString(2))
        }

        kumpul_rock.close()
        db.close()
    }
}

