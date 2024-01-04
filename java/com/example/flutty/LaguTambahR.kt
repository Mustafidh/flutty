package com.example.flutty

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class LaguTambahR : AppCompatActivity() {

    private var iv_album: ImageView? = null
    private var iv_lagu: ImageView? = null
    private var bitmapgambar: Bitmap? = null
    private var urigambar: Uri? = null
    private var uriLagu: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lagu_tambah)

        val edt_judul: EditText = findViewById(R.id.edt_judul)
        val edt_artis: EditText = findViewById(R.id.edt_artis)
        val btn_simpan: Button = findViewById(R.id.btn_simpan)
        iv_album = findViewById(R.id.iv_upload)
        iv_lagu = findViewById(R.id.iv_lagu)

        // buka galeri untuk gambar
        iv_album?.setOnClickListener {
            val galeri = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            pilih_gambar.launch(galeri)
        }

        // buka lagu (file mp3)
        iv_lagu?.setOnClickListener {
            val galeriLagu = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            pilih_lagu.launch(galeriLagu)
        }

        btn_simpan.setOnClickListener {
            val isi_judul: String = edt_judul.text.toString()
            val isi_artis: String = edt_artis.text.toString()

            // mengonversi gambar ke bytearray
            val fto = ByteArrayOutputStream()
            bitmapgambar?.compress(Bitmap.CompressFormat.JPEG, 100, fto)
            val bytearraygambar = fto.toByteArray()

            // membaca file lagu menjadi bytearray
            var bytearraylagu: ByteArray? = null
            try {
                val inputStream: InputStream? = uriLagu?.let { contentResolver.openInputStream(it) }
                if (inputStream != null) {
                    val lagutoByteArray = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        lagutoByteArray.write(buffer, 0, len)
                    }
                    bytearraylagu = lagutoByteArray.toByteArray()
                }
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // menyimpan data ke database
            val db: SQLiteDatabase = openOrCreateDatabase("flutty", MODE_PRIVATE, null)

            val sql = "INSERT INTO lagurock (jlagu, artis, album, lagu) VALUES (?,?,?,?)"
            val statement = db.compileStatement(sql)
            statement.clearBindings()
            statement.bindString(1, isi_judul)
            statement.bindString(2, isi_artis)
            statement.bindBlob(3, bytearraygambar)
            statement.bindBlob(4, bytearraylagu)
            statement.executeInsert()

            val pindah = Intent(this, LaguRock::class.java)
            startActivity(pindah)
        }
    }

    private val pilih_gambar =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val gambardiperoleh = result.data

            if (gambardiperoleh != null) {
                urigambar = gambardiperoleh.data

                bitmapgambar = MediaStore.Images.Media.getBitmap(contentResolver, urigambar)
                iv_album?.setImageBitmap(bitmapgambar)
            }
        }

    private val pilih_lagu =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val lagudiperoleh = result.data

            if (lagudiperoleh != null) {
                uriLagu = lagudiperoleh.data

                // Mendapatkan nama file dari URI
                val namaFile = uriLagu?.lastPathSegment

                // Menampilkan nama file di ImageView (iv_lagu)
                iv_lagu?.setImageURI(uriLagu)
                iv_lagu?.setImageResource(R.drawable.checklist)
            }
        }

}
