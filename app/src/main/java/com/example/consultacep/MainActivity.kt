package com.example.consultacep

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {

    private lateinit var cepEditText: EditText
    private lateinit var consultarButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var messageTextView: TextView
    private lateinit var resultContainer: LinearLayout
    private lateinit var cepResultTextView: TextView
    private lateinit var logradouroTextView: TextView
    private lateinit var bairroTextView: TextView
    private lateinit var cidadeTextView: TextView
    private lateinit var dddTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cepEditText = findViewById(R.id.cepEditText)
        consultarButton = findViewById(R.id.consultarButton)
        progressBar = findViewById(R.id.progressBar)
        messageTextView = findViewById(R.id.messageTextView)
        resultContainer = findViewById(R.id.resultContainer)
        cepResultTextView = findViewById(R.id.cepResultTextView)
        logradouroTextView = findViewById(R.id.logradouroTextView)
        bairroTextView = findViewById(R.id.bairroTextView)
        cidadeTextView = findViewById(R.id.cidadeTextView)
        dddTextView = findViewById(R.id.dddTextView)

        consultarButton.setOnClickListener {
            esconderTeclado()
            consultarCep()
        }
    }

    private fun consultarCep() {
        val cepDigitado = cepEditText.text.toString().filter { it.isDigit() }

        if (cepDigitado.isBlank()) {
            mostrarErro("Digite um CEP antes de consultar.")
            return
        }

        if (cepDigitado.length != 8) {
            mostrarErro("O CEP precisa ter 8 números. Exemplo: 01001000.")
            return
        }

        setCarregando(true)
        messageTextView.text = ""
        resultContainer.visibility = View.GONE

        Thread {
            try {
                val json = buscarCepNaApi(cepDigitado)
                val endereco = interpretarResposta(json)

                runOnUiThread {
                    setCarregando(false)
                    mostrarEndereco(endereco)
                }
            } catch (erro: Exception) {
                Log.e("ConsultaCep", "Erro ao consultar CEP", erro)
                runOnUiThread {
                    setCarregando(false)
                    mostrarErro(erro.message ?: "Não foi possível consultar o CEP.")
                }
            }
        }.start()
    }

    private fun buscarCepNaApi(cep: String): String {
        val url = URL("https://viacep.com.br/ws/$cep/json/")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("A API retornou erro ${connection.responseCode}. Tente novamente.")
            }

            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun interpretarResposta(json: String): Endereco {
        val objeto = JSONObject(json)

        if (objeto.optBoolean("erro", false)) {
            throw Exception("CEP não encontrado. Confira os números digitados.")
        }

        return Endereco(
            cep = objeto.optString("cep", "Não informado"),
            logradouro = objeto.optString("logradouro", "Não informado").ifBlank { "Não informado" },
            bairro = objeto.optString("bairro", "Não informado").ifBlank { "Não informado" },
            cidade = objeto.optString("localidade", "Não informado").ifBlank { "Não informado" },
            uf = objeto.optString("uf", "Não informado").ifBlank { "Não informado" },
            ddd = objeto.optString("ddd", "Não informado").ifBlank { "Não informado" }
        )
    }

    private fun mostrarEndereco(endereco: Endereco) {
        messageTextView.text = "Consulta realizada com sucesso."
        messageTextView.setTextColor(cor(R.color.success))

        cepResultTextView.text = getString(R.string.resultado_cep, endereco.cep)
        logradouroTextView.text = getString(R.string.resultado_logradouro, endereco.logradouro)
        bairroTextView.text = getString(R.string.resultado_bairro, endereco.bairro)
        cidadeTextView.text = getString(R.string.resultado_cidade, endereco.cidade, endereco.uf)
        dddTextView.text = getString(R.string.resultado_ddd, endereco.ddd)

        resultContainer.visibility = View.VISIBLE
    }

    private fun mostrarErro(mensagem: String) {
        setCarregando(false)
        resultContainer.visibility = View.GONE
        messageTextView.text = mensagem
        messageTextView.setTextColor(cor(R.color.error))
    }

    private fun setCarregando(carregando: Boolean) {
        progressBar.visibility = if (carregando) View.VISIBLE else View.GONE
        consultarButton.isEnabled = !carregando
        consultarButton.text = if (carregando) "Consultando..." else "Consultar CEP"
    }

    private fun esconderTeclado() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        cepEditText.windowToken?.let { token ->
            inputMethodManager.hideSoftInputFromWindow(token, 0)
        }
    }

    @Suppress("DEPRECATION")
    private fun cor(colorId: Int): Int {
        return resources.getColor(colorId)
    }
}

data class Endereco(
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    val ddd: String
)
