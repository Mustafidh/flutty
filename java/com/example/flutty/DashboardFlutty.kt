package com.example.flutty

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

class DashboardFlutty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_flutty)


        val txt_user:TextView = findViewById(R.id.txt_user)
        val tiket:SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val nama_user:String = tiket.getString("username", null).toString()
        txt_user.text = nama_user

        val btn_logout:ImageView = findViewById(R.id.logout)
        btn_logout.setOnClickListener{
            val edittiket = tiket.edit()
            edittiket.clear()
            edittiket.commit()

            val keluar:Intent = Intent(this,LoginFlutty::class.java)
            startActivity(keluar)
            finish()
        }


        val hrock:CardView = findViewById(R.id.hrock)
        val hpop:CardView = findViewById(R.id.hpop)
        val hindie:CardView = findViewById(R.id.hindie)
        val hcountry:CardView = findViewById(R.id.hcountry)

        hrock.setOnClickListener{
            val pindah:Intent = Intent(this, LaguRock::class.java);
            startActivity(pindah);
        }
        hpop.setOnClickListener{
            val pindah:Intent = Intent(this, LaguPop::class.java);
            startActivity(pindah);
        }
        hindie.setOnClickListener{
            val pindah:Intent = Intent(this, LaguIndie::class.java);
            startActivity(pindah);
        }
        hcountry.setOnClickListener{
            val pindah:Intent = Intent(this, LaguCountry::class.java);
            startActivity(pindah);
        }
    }
}