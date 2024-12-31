package Pickling.HTTP

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Http(
    val protocole: Protocole,
    val url: String,
    val header: Map<String, String>,
    val body: Serialisation? = null,
) {
    var réponse: Réponse? = null
    fun envoyer(): Http {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        try {
            urlConnection.requestMethod = protocole.name
            for ((key, value) in header) urlConnection.setRequestProperty(key, value)
            urlConnection.doOutput = true
            body?.let {
                val json = Json.encodeToJsonElement(it) as JsonObject
                val sansType = JsonObject(json.filterKeys { it != "type" })
                val jsonBody = Json.encodeToString(sansType)
                val outputStream: OutputStream = urlConnection.outputStream
                outputStream.write(jsonBody.toByteArray())
                outputStream.flush()
                outputStream.close()
                println(jsonBody)
            }
        } catch (e: IOException) {
            this.réponse = Réponse(
                urlConnection.responseCode,
                BufferedReader(InputStreamReader(urlConnection.inputStream)).readText()
            )
        } finally {
            this.réponse = Réponse(
                urlConnection.responseCode,
                BufferedReader(InputStreamReader(urlConnection.inputStream)).readText()
            )
            urlConnection.disconnect()
        }
        return this
    }

    inline fun après(crossinline traitement: Http.() -> Unit) {
        if (this.réponse != null) {
            traitement()
        }
    }

    inline fun <reified T> Désérialisation(json: String): T {
        return Json.decodeFromString(json)
    }

    fun extraireJson(message: String): String? {
        val startIndex = message.indexOf('{')
        val endIndex = message.lastIndexOf('}')
        return if (startIndex != -1 && endIndex != -1) {
            message.substring(startIndex, endIndex + 1)
        } else {
            null
        }
    }
}