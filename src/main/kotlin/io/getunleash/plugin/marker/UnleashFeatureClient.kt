package io.getunleash.plugin.marker

import com.google.gson.JsonParser
import io.getunleash.plugin.adjusted
import io.getunleash.plugin.settings.UnleashSettings
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

/**
 * Client for fetching feature data from Unleash API
 */
object UnleashFeatureClient {

    /**
     * Fetches feature information for a given feature name
     *
     * @param settings The Unleash settings containing API URL and token
     * @param featureName The name of the feature
     * @return A [FeatureDataResponse] containing feature information, or null if the request failed
     */
    fun getFeatureInfo(settings: UnleashSettings.State, featureName: String): FeatureDataResponse? {
        val url = URL("${settings.apiUrl.adjusted()}/api/client/features/$featureName")
        val connection = url.openConnection() as HttpURLConnection

        connection.apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", settings.apiToken)
        }

        return connection.responseCode.takeIf { it == HttpURLConnection.HTTP_OK }?.let {
            readResponse(connection).parseFeatureData(it)
        }
    }

    private fun readResponse(connection: HttpURLConnection): String {
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun String.parseFeatureData(responseCode: Int): FeatureDataResponse {
        val jsonObject = JsonParser.parseString(this).asJsonObject
        val project = jsonObject.get("project").asString
        val name = jsonObject.get("name").asString
        val description = jsonObject.get("description").asString

        return FeatureDataResponse(
            responseCode = responseCode,
            name = name,
            project = project,
            description = description
        )
    }

    /**
     * Represents a feature toggle data response from Unleash API
     */
    data class FeatureDataResponse(
        val responseCode: Int,
        val name: String? = null,
        val project: String? = null,
        // optional,
        // explores the possibility of displaying a feature descriptions right in the IDEA,
        // but is not used by default
        val description: String? = null
    ) {

        /**
         * Constructs the URI for the feature in the Unleash API
         *
         * @param unleashApiURL The base URL of the Unleash API, based on IDEA settings
         * @return A [URI] pointing to the feature
         */
        fun getFeatureURI(unleashApiURL: String): URI {
            return URI("${unleashApiURL.adjusted()}/projects/${project}/features/$name")
        }
    }
}