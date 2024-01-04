package com.example.flutty

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

class PutarLagu : AppCompatActivity() {

    private lateinit var jputar: TextView
    private lateinit var aputar: TextView
    private lateinit var album: ImageView
    lateinit var music: MediaPlayer
    private lateinit var play_btn: ImageView
    private lateinit var prev_btn: ImageView
    private lateinit var next_btn: ImageView
    private lateinit var db: SQLiteDatabase
    private lateinit var seekBar: SeekBar
    private var isPlaying = false
    private var laguId: Int = -1
    private var id_lagu_terpilih: String = ""

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.putar_lagu)

        jputar = findViewById(R.id.jputar)
        aputar = findViewById(R.id.aputar)
        album = findViewById(R.id.album)
        play_btn = findViewById(R.id.play_btn)
        prev_btn = findViewById(R.id.prev_btn)
        next_btn = findViewById(R.id.next_btn)
        seekBar = findViewById(R.id.seekbar)

        id_lagu_terpilih = intent.getStringExtra("id_lagu_terpilih").toString()
        laguId = id_lagu_terpilih.toInt()

        // Gunakan direktori cache untuk menyimpan file sementara
        val cacheDir = cacheDir
        val albumFile = File(cacheDir, "album.jpg") // assuming it's an image file
        val laguFile = File(cacheDir, "lagu.mp3")

        // Query to get lagu data based on laguId
        db = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val query = "SELECT * FROM lagurock WHERE id_rock = ?"
        val selectionArgs = arrayOf(laguId.toString())
        val cursor: Cursor = db.rawQuery(query, selectionArgs)

        cursor.use {
            if (it.moveToFirst()) {
                val judul = it.getString(it.getColumnIndex("jlagu"))
                val artis = it.getString(it.getColumnIndex("artis"))
                val albumByteArray = it.getBlob(it.getColumnIndex("album"))
                val laguByteArray = it.getBlob(it.getColumnIndex("lagu"))
                val maxSizeInMB = 10 // Sesuaikan dengan kebutuhan

                blobToFile(albumByteArray, albumFile, maxSizeInMB)
                blobToFile(laguByteArray, laguFile, maxSizeInMB)

                jputar.text = judul
                aputar.text = artis

                // Set the album image using the data from the database
                album.setImageURI(albumFile.toUri())

                // Resize the album image (adjust these values accordingly)
                val newWidth = 1024
                val newHeight = 835
                resizeAlbumImage(newWidth, newHeight)

                music = MediaPlayer.create(this, laguFile.toUri())
                // Set other UI elements accordingly
            } else {
                // Handle the case when no data is found
                Toast.makeText(this, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        setButtonListeners()
        setupSeekBar()
    }

    private fun setButtonListeners() {
        play_btn.setOnClickListener { playMusic() }
        next_btn.setOnClickListener { playNext() }
        prev_btn.setOnClickListener { playPrev() }
    }

    @SuppressLint("Range")
    private fun playNext() {
        // Query untuk mendapatkan ID lagu selanjutnya
        db = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val queryNext = "SELECT * FROM lagurock WHERE id_rock > ? ORDER BY id_rock ASC LIMIT 1"
        val selectionArgsNext = arrayOf(laguId.toString())
        val cursorNext: Cursor = db.rawQuery(queryNext, selectionArgsNext)

        cursorNext.use {
            if (it.moveToFirst()) {
                laguId = it.getInt(it.getColumnIndex("id_rock"))
                playNewLagu()
            } else {
                // Tidak ada lagu selanjutnya
                Toast.makeText(this, "Tidak Ada Lagu Selanjutnya", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    private fun playPrev() {
        // Query untuk mendapatkan ID lagu sebelumnya
        db = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val queryPrev = "SELECT * FROM lagurock WHERE id_rock < ? ORDER BY id_rock DESC LIMIT 1"
        val selectionArgsPrev = arrayOf(laguId.toString())
        val cursorPrev: Cursor = db.rawQuery(queryPrev, selectionArgsPrev)

        cursorPrev.use {
            if (it.moveToFirst()) {
                laguId = it.getInt(it.getColumnIndex("id_rock"))
                playNewLagu()
            } else {
                // Tidak ada lagu sebelumnya
                Toast.makeText(this, "Tidak Ada Lagu Sebelumnya", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    private fun playNewLagu() {
        music.stop()
        music.release()

        // Lakukan pemutaran lagu yang baru
        val laguFile = File(cacheDir, "lagu.mp3")
        val albumFile = File(cacheDir, "album.jpg") // File sementara untuk album

        // Query untuk mendapatkan data lagu baru berdasarkan ID
        db = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
        val query = "SELECT * FROM lagurock WHERE id_rock = ?"
        val selectionArgs = arrayOf(laguId.toString())
        val cursor: Cursor = db.rawQuery(query, selectionArgs)

        cursor.use {
            if (it.moveToFirst()) {
                val laguByteArray = it.getBlob(it.getColumnIndex("lagu"))
                val albumByteArray = it.getBlob(it.getColumnIndex("album"))
                val maxSizeInMB = 10 // Sesuaikan dengan kebutuhan

                blobToFile(laguByteArray, laguFile, maxSizeInMB)
                blobToFile(albumByteArray, albumFile, maxSizeInMB)

                music = MediaPlayer.create(this, laguFile.toUri())

                // Atur UI elements untuk lagu baru
                jputar.text = it.getString(it.getColumnIndex("jlagu"))
                aputar.text = it.getString(it.getColumnIndex("artis"))

                // Set the album image using the data from the database
                val bitmap = BitmapFactory.decodeByteArray(albumByteArray, 0, albumByteArray.size)
                album.setImageBitmap(bitmap)
                resizeAlbumImage(1024, 835) // Sesuaikan dengan kebutuhan

                // Mulai pemutaran lagu baru
                music.start()
                play_btn.setImageResource(R.drawable.pause)
                isPlaying = true

                music.setOnCompletionListener {
                    // Reset the SeekBar when the music is completed
                    seekBar.progress = 0
                    isPlaying = false
                    play_btn.setImageResource(R.drawable.play)
                }

                // Update SeekBar every second
                if (isPlaying == false) {
                    seekBar.progress = 0
                    music.start()
                    play_btn.setImageResource(R.drawable.pause)
                    isPlaying = true

                    music.setOnCompletionListener {
                        // Reset the SeekBar when the music is completed
                        seekBar.progress = 0
                        isPlaying = false
                        play_btn.setImageResource(R.drawable.play)
                    }

                    // Update SeekBar every second
                    updateSeekBar()
                } else {
                    music.pause()
                    play_btn.setImageResource(R.drawable.play)
                    isPlaying = false
                }
            }
        }
    }


    private fun updateSeekBar() {
        val duration = music.duration
        val handler = Handler(mainLooper)
        val updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    val currentPosition = music.currentPosition
                    val progress = (currentPosition * 100) / duration
                    seekBar.progress = progress
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }
        handler.post(updateSeekBarRunnable)
    }

            private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = music.duration
                    val newPosition = (progress * duration) / 100
                    music.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops touching the SeekBar
            }
        })
    }

    private fun blobToFile(blob: ByteArray?, file: File, maxSizeInMB: Int) {
        blob?.let {
            val maxSizeInBytes = maxSizeInMB * 1024 * 1024
            if (it.size <= maxSizeInBytes) {
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(it)
                fileOutputStream.close()
            } else {
                // Blob size exceeds the maximum allowed size
                Toast.makeText(this, "Ukuran Terlalu Besar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resizeAlbumImage(newWidth: Int, newHeight: Int) {
        val layoutParams = album.layoutParams
        layoutParams.width = newWidth
        layoutParams.height = newHeight
        album.layoutParams = layoutParams
    }

    fun playMusic() {
        if (isPlaying == false) {
            seekBar.progress = 0
            music.start()
            play_btn.setImageResource(R.drawable.pause)
            isPlaying = true

            music.setOnCompletionListener {
                // Reset the SeekBar when the music is completed
                seekBar.progress = 0
                isPlaying = false
                play_btn.setImageResource(R.drawable.play)
            }
            val duration = music.duration
            val handler = Handler(mainLooper)
            handler.postDelayed(object : Runnable {
                override fun run() {
                    val currentPosition = music.currentPosition
                    val progress = (currentPosition * 100) / duration
                    seekBar.progress = progress

                    if (isPlaying) {
                        handler.postDelayed(this, 1000) // Update every second
                    }
                }
            }, 1000)
        } else {
            music.pause()
            play_btn.setImageResource(R.drawable.play)
            isPlaying = false
        }
    }
}
