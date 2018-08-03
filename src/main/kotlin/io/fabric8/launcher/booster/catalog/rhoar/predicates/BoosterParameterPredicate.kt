package io.fabric8.launcher.booster.catalog.rhoar.predicates

import io.fabric8.launcher.booster.catalog.Booster
import org.apache.commons.beanutils.PropertyUtils
import java.util.Objects
import java.util.function.Predicate

/**
 * @author [George Gastaldi](mailto:ggastald@redhat.com)
 */
class BoosterParameterPredicate(private val parameters: Map<String, List<String>>) : Predicate<Booster> {

    override fun test(booster: Booster): Boolean {
        for ((path, values) in parameters) {
            val actualValue = getValueByPath(booster, path)
            val notFound = values.map { v -> if (!v.isEmpty()) v else "true" }
                    .find { v -> v.equals(actualValue, true) }.orEmpty().isEmpty()
            if (notFound) {
                return false
            }
        }
        return true
    }

    private fun getValueByPath(b: Booster, path: String): String? {
        var target: Any = b
        try {
            target = PropertyUtils.getNestedProperty(target, path)
        } catch (ignored: Exception) {
            return "false"
        }

        return Objects.toString(target, "false")
    }

}
