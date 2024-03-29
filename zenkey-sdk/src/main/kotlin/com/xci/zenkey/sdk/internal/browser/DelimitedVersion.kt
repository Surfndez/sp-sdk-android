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
package com.xci.zenkey.sdk.internal.browser

/**
 * Represents a delimited version number for an application. This can parse common version number
 * formats, treating any sequence of non-numeric characters as a delimiter, and discards these
 * to retain just the numeric content for comparison. Trailing zeroes in a version number
 * are discarded to produce a compact, canonical representation. Empty versions are equivalent to
 * "0". Each numeric part is expected to fit within a 64-bit integer.
 */
internal class DelimitedVersion private constructor(
        private val mNumericParts: LongArray
) : Comparable<DelimitedVersion> {

    override fun toString(): String {
        if (mNumericParts.isEmpty()) {
            return "0"
        }

        val builder = StringBuilder()
        builder.append(mNumericParts[0])

        var index = 1
        while (index < mNumericParts.size) {
            builder.append('.')
            builder.append(mNumericParts[index])
            index++
        }

        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        return if (other == null || other !is DelimitedVersion) {
            false
        } else this.compareTo((other as DelimitedVersion?)!!) == 0

    }

    override fun hashCode(): Int {
        var result = 0

        for (numericPart in mNumericParts) {
            result = result * PRIME_HASH_FACTOR + (numericPart and BIT_MASK_32).toInt()
        }

        return result
    }

    override fun compareTo(other: DelimitedVersion): Int {
        var index = 0

        while (index < this.mNumericParts.size && index < other.mNumericParts.size) {
            val currentPartOrder = compareLongs(this.mNumericParts[index], other.mNumericParts[index])
            if (currentPartOrder != 0) {
                return currentPartOrder
            }
            index++
        }

        return compareLongs(this.mNumericParts.size.toLong(), other.mNumericParts.size.toLong())
    }

    private fun compareLongs(l1: Long, l2: Long): Int {
        if (l1 < l2) {
            return -1
        } else if (l1 > l2) {
            return 1
        }
        return 0
    }

    companion object {

        // See: http://stackoverflow.com/a/2816747
        private const val PRIME_HASH_FACTOR = 92821
        private const val BIT_MASK_32: Long = -0x1

        /**
         * Parses a delimited version number from the provided string.
         */
        fun parse(versionString: String?): DelimitedVersion {

            if (versionString == null) {
                return DelimitedVersion(LongArray(0))
            }

            val stringParts = versionString.split("[^0-9]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val parsedParts = LongArray(stringParts.size)
            var index = 0
            for (numericPart in stringParts) {
                if (numericPart.isEmpty()) {
                    continue
                }

                parsedParts[index] = java.lang.Long.parseLong(numericPart)
                index++
            }

            // discard all trailing zeroes
            index--
            while (index >= 0) {
                if (parsedParts[index] > 0) {
                    break
                }
                index--
            }

            val length = index + 1
            val onlyParsedParts = LongArray(length)
            System.arraycopy(parsedParts, 0, onlyParsedParts, 0, length)
            return DelimitedVersion(onlyParsedParts)
        }
    }
}
