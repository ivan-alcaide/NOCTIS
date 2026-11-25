
package com.example.noctis

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class AdaptacionActivity : AppCompatActivity() {

    private var temporizador: CountDownTimer? = null
    private var tiempoRestante = 0L
    private var ejecutando = false
    private var reanudarAlVolver = false
    private var fechaFinalizacion = 0L

    private lateinit var txtTemporizador: TextView
    private lateinit var btnIniciar: Button

    companion object {
        private const val TAG = "NOCTIS_CICLO"
        private const val CLAVE_TIEMPO = "tiempo_restante"
        private const val CLAVE_EJECUTANDO = "ejecutando"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptacion)

        Log.d(TAG, "onCreate: pantalla creada")

        txtTemporizador = findViewById(R.id.txtTemporizador)
        btnIniciar = findViewById(R.id.btnIniciar)

        if (savedInstanceState == null) {
            tiempoRestante = intent.getLongExtra(HomeActivity.EXTRA_DURACION, 20 * 60 * 1000L)
        }

        if (savedInstanceState != null) {
            tiempoRestante = savedInstanceState.getLong(CLAVE_TIEMPO)
            reanudarAlVolver = savedInstanceState.getBoolean(CLAVE_EJECUTANDO)
        }

        actualizarPantalla()

        btnIniciar.setOnClickListener {
            if (ejecutando) {
                pausarTemporizador()
            } else {
                iniciarTemporizador()
            }
        }

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            val resultado = Intent()
            resultado.putExtra("resultado_adaptacion", "cancelada")
            setResult(RESULT_CANCELED, resultado)
            finish()
        }
    }

    private fun iniciarTemporizador() {
        if (tiempoRestante <= 0L) return

        ejecutando = true
        reanudarAlVolver = false
        fechaFinalizacion = SystemClock.elapsedRealtime() + tiempoRestante

        btnIniciar.text = "Pausar"

        temporizador = object : CountDownTimer(tiempoRestante, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished
                actualizarPantalla()
            }

            override fun onFinish() {
                tiempoRestante = 0L
                ejecutando = false
                temporizador = null

                val resultado = Intent()
                resultado.putExtra("resultado_adaptacion", "completada")
                setResult(RESULT_OK, resultado)

                actualizarPantalla()
            }
        }.start()
    }

    private fun pausarTemporizador() {
        tiempoRestante =
            (fechaFinalizacion - SystemClock.elapsedRealtime()).coerceAtLeast(0L)

        temporizador?.cancel()
        temporizador = null
        ejecutando = false
        reanudarAlVolver = false

        actualizarPantalla()
    }

    private fun actualizarPantalla() {
        val minutos = tiempoRestante / 60000
        val segundos = (tiempoRestante % 60000) / 1000

        txtTemporizador.text = String.format("%02d:%02d", minutos, segundos)

        btnIniciar.text = when {
            ejecutando -> "Pausar"
            tiempoRestante == 0L -> "Adaptación completada"
            tiempoRestante == 20 * 60 * 1000L -> "Iniciar adaptación"
            else -> "Reanudar"
        }

        btnIniciar.isEnabled = tiempoRestante > 0L
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (ejecutando) {
            tiempoRestante =
                (fechaFinalizacion - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        }

        outState.putLong(CLAVE_TIEMPO, tiempoRestante)
        outState.putBoolean(CLAVE_EJECUTANDO, ejecutando)

        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: pantalla visible")

        if (reanudarAlVolver) {
            iniciarTemporizador()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: aplicación en primer plano")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: aplicación pausada")
    }

    override fun onStop() {
        Log.d(TAG, "onStop: pantalla detenida")

        if (ejecutando) {
            tiempoRestante =
                (fechaFinalizacion - SystemClock.elapsedRealtime()).coerceAtLeast(0L)

            temporizador?.cancel()
            temporizador = null
            ejecutando = false
            reanudarAlVolver = true
        }

        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: pantalla destruida")
        temporizador?.cancel()
        super.onDestroy()
    }
}
