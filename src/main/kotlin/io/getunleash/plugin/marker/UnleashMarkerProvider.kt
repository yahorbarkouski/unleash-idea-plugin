package io.getunleash.plugin.marker

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.CENTER
import com.intellij.psi.PsiElement
import io.getunleash.plugin.belongsToUnleash
import io.getunleash.plugin.marker.UnleashFeatureClient.getFeatureInfo
import io.getunleash.plugin.settings.UnleashSettings
import java.awt.Desktop.getDesktop
import java.awt.event.MouseEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.evaluateString
import org.jetbrains.uast.toUElement

/**
 * A line marker provider for Unleash feature flags.
 *
 * It adds a gutter icon for method calls that belong to Unleash, allowing users to navigate
 * to the corresponding feature flag in the unleash.com website.
 */
class UnleashMarkerProvider : LineMarkerProvider {

    /**
     * Provides a line marker info for the given PsiElement if it is an Unleash method call.
     *
     * @param element The PsiElement to check for Unleash method call.
     * @return A [LineMarkerInfo] with a gutter icon and navigation handler, or null if not an Unleash method call.
     */
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val uCallExpression = element.toUElement() as? UCallExpression ?: return null
        if (!uCallExpression.belongsToUnleash()) {
            return null
        }

        val methodCallParameter: UExpression = uCallExpression.valueArguments.firstOrNull() ?: return null
        val featureName = methodCallParameter.evaluateString() ?: return null

        val settings = UnleashSettings.instance.state
        val unleashFeatureData = runBlocking {
            withContext(Dispatchers.IO) { getFeatureInfo(settings, featureName) }
        } ?: return null

        val featureNavigationHandler = GutterIconNavigationHandler { _: MouseEvent?, _: PsiElement ->
            getDesktop().browse(unleashFeatureData.getFeatureURI(settings.apiUrl))
        }

        return LineMarkerInfo(
            element,
            element.textRange,
            // FIXME: production-ready unleash icon please
            AllIcons.Actions.Annotate,
            { TOOLTIP },
            featureNavigationHandler,
            CENTER,
            { TOOLTIP },
        )
    }

    companion object {

        /** The tooltip text displayed when hovering over the gutter icon. */
        const val TOOLTIP = "Go to unleash.com with the given feature"
    }
}