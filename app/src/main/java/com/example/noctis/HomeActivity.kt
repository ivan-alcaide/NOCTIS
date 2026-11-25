
package com.example.noctis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class HomeActivity : AppCompatActivity() {

    private val lanzadorAdaptacion =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->

            val txtResultado = findViewById<TextView>(R.id.txtResultado)

            if (resultado.resultCode == RESULT_OK) {
                txtResultado.text = "¡Adaptación ocular completada!"
            } else {
                txtResultado.text = "Adaptación ocular cancelada."
            }
        }

    companion object {
        const val EXTRA_DURACION = "duracion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnAdaptacion = findViewById<Button>(R.id.btnAdaptacion)
        val btnAcerca = findViewById<Button>(R.id.btnAcerca)

        btnAdaptacion.setOnClickListener {
            val intent = Intent(this, AdaptacionActivity::class.java)
            intent.putExtra(EXTRA_DURACION, 20 * 60 * 1000L)
            lanzadorAdaptacion.launch(intent)
        }

        btnAcerca.setOnClickListener {
            val intent = Intent(this, AcercaDeActivity::class.java)
            startActivity(intent)
        }
    }
}
