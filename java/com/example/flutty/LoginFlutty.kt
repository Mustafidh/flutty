package com.example.flutty

import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginFlutty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_flutty)

        val edt_email: EditText = findViewById(R.id.edt_email)
        val edt_password: EditText = findViewById(R.id.edt_password)
        val btn_login: Button = findViewById(R.id.btn_login)

        btn_login.setOnClickListener {
            val isi_email: String = edt_email.text.toString()
            val isi_password: String = edt_password.text.toString()

            val db: SQLiteDatabase = openOrCreateDatabase("flutty", MODE_PRIVATE, null)
            val query = db.rawQuery(
                "SELECT * FROM user WHERE email_user='$isi_email' AND password='$isi_password'",
                null)
            val cek = query.moveToNext()

            if (cek) {
                val id = query.getString(0)
                val nama = query.getString(1)
                val email= query.getString(2)
                val password = query.getString(3)


                val session: SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                val buattiket = session.edit()
                buattiket.putString("id_user", id)
                buattiket.putString("username", nama)
                buattiket.putString("email_user", email)
                buattiket.putString("password", password)
                buattiket.commit()

                val pindah: Intent = Intent(this, DashboardFlutty::class.java)
                startActivity(pindah)
            } else {
                Toast.makeText(this, "Email atau Password Salah!", Toast.LENGTH_LONG).show()
            }
        }
    }
}