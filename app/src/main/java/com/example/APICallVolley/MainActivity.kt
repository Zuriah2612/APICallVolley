package com.example.APICallVolley

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.apicallvolley.R
import com.example.apicallvolley.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

private lateinit var binding : ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnQuote.setOnClickListener {
            handleRetrieveQuoteWithVolley()
        }
        }
        fun getQuote(view: View) {
            val thread = Thread {
                val url = URL("https://pokeapi.co/api/v2/pokemon/ditto")
                try {
                    val connection = url.openConnection()
                    if (connection is HttpURLConnection) {
                        connection.connectTimeout = 10000
                        connection.readTimeout = 10000
                        connection.requestMethod = "GET"
                        connection.connect()


                        val responseCode = connection.responseCode

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val scanner = Scanner(connection.inputStream)
                            scanner.useDelimiter("\\A")
                            val jsonData = if (scanner.hasNext()) scanner.next() else ""
                            Log.d("jsonData", jsonData)
                            val jsonArray = JSONArray(jsonData)
                            val quote = jsonArray[0].toString()
                            updateTextBoxFromThread(quote)
                            Log.d("jsonArray[0]", quote)
                        } else {
                            updateTextBoxFromThread("sorry")
                        }
                    } else {

                        Log.wtf("MAIN_ACTIVITY", "someone changed the API")
                    }
                } catch (e: IOException) {

                    updateTextBoxFromThread("Sorry, there was an error processing the data")

                }
            }
            thread.start()

        }
    private fun parseJson(jsonData: String?) : String{
        val jsonObject = JSONObject(jsonData)
        val quote = jsonObject.getString("value")
        return quote


    }

    private fun  updateTextBoxFromThread(text : String){
        runOnUiThread{
            binding.textView.text = text
        }
    }
    private fun handleRetrieveQuoteWithVolley() {
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, "https://pokeapi.co/api/v2/pokemon/ditto", null,
            { response -> binding.textView.text = parseJson(response.toString()) },

            { binding.textView.text = "That didn't work!" + ": $it" }
        )
        queue.add(jsonObjectRequest)
    }
}
