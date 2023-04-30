package io.getunleash.plugin

import org.jetbrains.uast.UCallExpression

/**
 * Handles variable backslashes at the end of the URL,
 * converting to the base format without the final backslash
 *
 * Example:
 * ```
 * "https://google.com/" -> "https://google.com"
 * "https://google.com" -> "https://google.com"
 * ```
 */
fun String.adjusted(): String {
    return if (this.endsWith("/")) this.substringBeforeLast("/") else this
}

/**
 * Checks whether a given UCallExpression belongs to either defined Unleash classes.
 * @return true if the UCallExpression belongs, false otherwise.
 */
fun UCallExpression.belongsToUnleash(): Boolean {
    val callClassName = this.receiverType?.canonicalText
    val unleashClassNames = setOf("no.finn.unleash.Unleash", "io.getunleash.Unleash")
    return unleashClassNames.contains(callClassName)
}