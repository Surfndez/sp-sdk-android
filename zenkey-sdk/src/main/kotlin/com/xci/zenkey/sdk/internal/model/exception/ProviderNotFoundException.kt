package com.xci.zenkey.sdk.internal.model.exception

import com.xci.zenkey.sdk.internal.Json
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse
import com.xci.zenkey.sdk.internal.network.stack.JsonConverter

import org.json.JSONException
import org.json.JSONObject

/**
 * An exception throw when the OpenId discovery failed because the Carrier isn't supported.
 */
internal class ProviderNotFoundException(
        val discoverUiEndpoint: String?
) : Exception() {
    companion object {

        fun from(response: HttpResponse<DiscoveryResponse>): ProviderNotFoundException {
            try {
                return object : JsonConverter<ProviderNotFoundException> {
                    override fun convert(json: String): ProviderNotFoundException {
                        var discoverUIEndpoint: String? = null
                        val jsonObject = JSONObject(json)
                        if (jsonObject.has(Json.KEY_REDIRECT_URI)) {
                            discoverUIEndpoint = jsonObject.getString(Json.KEY_REDIRECT_URI)
                        }
                        return ProviderNotFoundException(discoverUIEndpoint)
                    }
                }.convert(response.rawBody)
            } catch (e: JSONException) {
                e.printStackTrace()
                return ProviderNotFoundException(null)
            }
        }
    }
}
