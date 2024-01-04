package com.example.flutty

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaguItem(val ini:Context, private val id_rock:MutableList<String>, private val albums: MutableList<ByteArray>, private val jlagu: List<String>, private val artis: List<String>) : RecyclerView.Adapter<LaguItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lagu_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albm: ImageView = itemView.findViewById(R.id.albm)
        val txt_jlagu: TextView = itemView.findViewById(R.id.jlagu)
        val txt_artis: TextView = itemView.findViewById(R.id.artis)
        val ply: ImageView = itemView.findViewById(R.id.ply)
        val btn_hapus: ImageView = itemView.findViewById(R.id.hapus)
        val btn_ubah: ImageView = itemView.findViewById(R.id.edt)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set the ByteArray to ImageView directly
        val byteArray = albums[position]
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        holder.albm.setImageBitmap(bitmap)
        holder.txt_jlagu.text = jlagu[position]
        holder.txt_artis.text = artis[position]

        holder.btn_hapus.setOnClickListener{
            val id_lagu_terpilih:String = id_rock.get(position)

            val pindah:Intent = Intent(ini, LaguHapus::class.java)
            pindah.putExtra("id_lagu_terpilih", id_lagu_terpilih)
            ini.startActivity(pindah)
        }

        holder.ply.setOnClickListener{
            val id_lagu_terpilih:String = id_rock.get(position)

            val pindah:Intent = Intent(ini, PutarLagu::class.java)
            pindah.putExtra("id_lagu_terpilih", id_lagu_terpilih)
            ini.startActivity(pindah)
        }

        holder.btn_ubah.setOnClickListener{
            val id_lagu_terpilih:String = id_rock.get(position)

            val pindah:Intent = Intent(ini, LaguUbahR::class.java)
            pindah.putExtra("id_lagu_terpilih", id_lagu_terpilih)
            ini.startActivity(pindah)
        }
    }
}

