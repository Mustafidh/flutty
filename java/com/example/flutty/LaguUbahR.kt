package com.example.flutty

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class LaguUbahR : AppCompatActivity() {

    private var iv_album: ImageView? = null
    private var iv_lagu: ImageView? = null
    private var bitmapgambar: Bitmap? = null
    private var urigambar: Uri? = null
    private var uriLagu: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lagu_ubah)

        val id_lagu_terpilih: String = intent.getStringExtra("id_lagu_terpilih").toString()

        val db: SQLiteDatabase = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val ambil = db.rawQuery("SELECT * FROM lagurock WHERE id_rock ='$id_lagu_terpilih'", null)
        ambil.moveToNext()

        val isi_judul: String = ambil.getString(1)
        val isi_artis: String = ambil.getString(2)
        val isi_album: ByteArray = ambil.getBlob(3)
        val isi_lagu: ByteArray = ambil.getBlob(4)

        val edt_judul: EditText = findViewById(R.id.edt_judul)
        val edt_artis: EditText = findViewById(R.id.edt_artis)
        val btn_ubah: Button = findViewById(R.id.btn_ubah)
        iv_album = findViewById(R.id.iv_upload)
        iv_lagu = findViewById(R.id.iv_lagu)

        try {
            val bis = ByteArrayInputStream(isi_album)
            val gambarbitmap: Bitmap = BitmapFactory.decodeStream(bis)
            iv_album?.setImageBitmap(gambarbitmap)
        } catch (abc: Exception) {
            val gambarbitmap: Bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.noimage)
            iv_album?.setImageBitmap(gambarbitmap)
        }

        try {
            val bis = ByteArrayInputStream(isi_lagu)
            val lagubitmap: Bitmap = BitmapFactory.decodeStream(bis)
            iv_lagu?.setImageBitmap(lagubitmap)
        } catch (abc: Exception) {
            // handle jika gambar lagu tidak ada atau tidak bisa di-decode
        }

        iv_album?.setOnClickListener {
            val galeri = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            pilih_gambar.launch(galeri)
        }

        iv_lagu?.setOnClickListener {
            // Buka aplikasi pemilihan audio
            val galeriLagu = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            pilih_lagu.launch(galeriLagu)
        }

        edt_judul.setText(isi_judul)
        edt_artis.setText(isi_artis)

        btn_ubah.setOnClickListener {
            val jbaru: String = edt_judul.text.toString()
            val abaru: String = edt_artis.text.toString()

            val fto = ByteArrayOutputStream()

            // Periksa apakah pengguna memilih gambar baru
            if (bitmapgambar != null) {
                bitmapgambar?.compress(Bitmap.CompressFormat.JPEG, 100, fto)
            } else {
                // Jika tidak ada gambar baru yang dipilih, gunakan gambar yang sudah ada
                val bis = ByteArrayInputStream(isi_album)
                val existingBitmap: Bitmap = BitmapFactory.decodeStream(bis)
                existingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fto)
            }

            val bytearraygambar = fto.toByteArray()

            // Periksa apakah pengguna memilih lagu baru
            val bytearraylagu: ByteArray = if (uriLagu != null) {
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(uriLagu!!)
                    inputStream.use { stream ->
                        stream?.readBytes()
                    } ?: byteArrayOf()
                } catch (e: Exception) {
                    byteArrayOf()
                }
            } else {
                isi_lagu
            }

            val sql =
                "UPDATE lagurock SET jlagu = ?, artis = ?, album = ?, lagu = ? WHERE id_rock ='$id_lagu_terpilih'"
            val statement = db.compileStatement(sql)

            statement.clearBindings()
            statement.bindString(1, jbaru)
            statement.bindString(2, abaru)
            statement.bindBlob(3, bytearraygambar)
            statement.bindBlob(4, bytearraylagu)
            statement.executeUpdateDelete()

            val pindah = Intent(this, LaguRock::class.java)
            startActivity(pindah)
        }
    }

    private val pilih_gambar =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val gambardiperoleh = result.data

            if (gambardiperoleh != null) {
                urigambar = gambardiperoleh.data

                bitmapgambar =
                    MediaStore.Images.Media.getBitmap(contentResolver, urigambar)
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
