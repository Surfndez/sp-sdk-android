/*
 * Copyright 2019 XCI JV, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xci.zenkey.sdk.internal.network.call.assetlinks

import android.support.annotation.VisibleForTesting

import com.xci.zenkey.sdk.internal.model.Package
import com.xci.zenkey.sdk.internal.network.stack.JsonConverter

import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

internal class AssetLinksResponseConverter
    : JsonConverter<List<Package>> {

    @Throws(JSONException::class)
    override fun convert(json: String): List<Package> {
        val packages = ArrayList<Package>()
        val responseArray = JSONArray(json)
        for (i in 0 until responseArray.length()) {
            val target = responseArray.getJSONObject(i).getJSONObject(TARGET_KEY)
            val packageName = target.getString(PACKAGE_NAME_KEY)
            val fingerprintsArray = target.getJSONArray(FINGERPRINTS_KEY)
            val fingerprints = ArrayList<String>()
            for (j in 0 until fingerprintsArray.length()) {
                fingerprints.add(fingerprintsArray.getString(j))
            }
            packages.add(Package(packageName, fingerprints))
        }
        return packages
    }

    companion object {
        @VisibleForTesting
        internal val TARGET_KEY = "target"
        @VisibleForTesting
        internal val FINGERPRINTS_KEY = "sha256_cert_fingerprints"
        @VisibleForTesting
        internal val PACKAGE_NAME_KEY = "package_name"
    }
}
