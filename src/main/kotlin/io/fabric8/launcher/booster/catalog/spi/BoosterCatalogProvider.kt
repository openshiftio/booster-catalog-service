/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.fabric8.launcher.booster.catalog.spi

import java.io.IOException

import io.fabric8.launcher.booster.catalog.BoosterCatalogService

/**
 * A SPI interface used in [BoosterCatalogService]
 */
interface BoosterCatalogProviderx {
    /**
     * Fetches a Catalog and returns its contents as a [List]
     */
    @Throws(IOException::class)
    fun fetchCatalog(): List<Map<String, Any?>>
}

typealias BoosterCatalogProvider = () -> List<Map<String, Any?>>
