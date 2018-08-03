/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.fabric8.launcher.booster.catalog.rhoar;

import org.arquillian.smart.testing.rules.git.server.GitServer;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.fabric8.launcher.booster.catalog.LauncherConfiguration.PropertyName.LAUNCHER_BOOSTER_CATALOG_REF;
import static io.fabric8.launcher.booster.catalog.LauncherConfiguration.PropertyName.LAUNCHER_BOOSTER_CATALOG_REPOSITORY;

public class RhoarBoosterCatalogServiceTest {

    private static final String HOST = "http://localhost";

    private static final int PORT = 8765;

    private static final String REPO_NAME = "gastaldi-booster-catalog";

    @ClassRule
    public static final GitServer gitServer = GitServer
            .fromBundle(REPO_NAME, "repos/custom-catalogs/gastaldi-booster-catalog.bundle")
            .usingPort(PORT)
            .create();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
            .set(LAUNCHER_BOOSTER_CATALOG_REPOSITORY, HOST + ":" + PORT + "/" + REPO_NAME)
            .set(LAUNCHER_BOOSTER_CATALOG_REF, "master");

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Nullable
    private static RhoarBoosterCatalogService defaultService;

    @Test
    public void testProcessMetadata() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        Path metadataFile = Paths.get(getClass().getResource("metadata.yaml").toURI());
        Map<String, Mission> missions = new HashMap<>();
        Map<String, Runtime> runtimes = new HashMap<>();

        service.processMetadata(metadataFile, missions, runtimes);

        softly.assertThat(missions).hasSize(6);
        softly.assertThat(runtimes).hasSize(5);
        softly.assertThat(runtimes.get("wildfly-swarm").getDescription()).isNotEmpty();
        softly.assertThat(runtimes.get("wildfly-swarm").getIcon()).isNotEmpty();
        softly.assertThat(runtimes.get("wildfly-swarm").getPipelinePlatform()).isEqualTo("maven");
        softly.assertThat(runtimes.get("wildfly-swarm").isSuggested()).isFalse();
        softly.assertThat(runtimes.get("spring-boot").isSuggested()).isTrue();
        softly.assertThat(runtimes.get("vert.x").getVersions()).hasSize(2);
        softly.assertThat(runtimes.get("vert.x").getVersions()).hasEntrySatisfying("community", v -> softly.assertThat(v.getName()).isEqualTo("3.5.0.Final (Community)"));
        softly.assertThat(missions.get("configmap").getDescription()).isNotEmpty();
        softly.assertThat(missions.get("rest-http").isSuggested()).isTrue();
        softly.assertThat(missions.get("configmap").isSuggested()).isFalse();
    }

    @Test
    public void testVertxVersions() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        service.index().get();

        Set<Version> versions = service.getVersions(new Mission("rest-http"), new Runtime("vert.x"));

        softly.assertThat(versions).hasSize(2);
    }

    @Test
    public void testGetBoosterRuntime() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        service.index().get();
        Runtime vertx = new Runtime("vert.x");

        Collection<RhoarBooster> boosters = service.getBoosters(BoosterPredicates.withRuntime(vertx));

        softly.assertThat(boosters.size()).isGreaterThan(0);
    }

    @Test
    public void testGetMissions() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        service.index().get();

        softly.assertThat(service.getMissions()).contains(new Mission("rest-http"));
    }

    @Test
    public void testGetMissionByRuntime() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        service.index().get();
        Runtime vertx = new Runtime("vert.x");

        Set<Mission> missions = service.getMissions(BoosterPredicates.withRuntime(vertx));

        softly.assertThat(missions.size()).isGreaterThan(0);
    }

    @Test
    public void testGetRuntimes() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        service.index().get();

        softly.assertThat(service.getRuntimes()).contains(new Runtime("vert.x"));
    }

    @Test
    public void testGetRuntimeByMission() throws Exception {
        RhoarBoosterCatalogService service = buildDefaultCatalogService();
        service.index().get();

        Mission resthttp = new Mission("rest-http");

        Set<Runtime> runtimes = service.getRuntimes(BoosterPredicates.withMission(resthttp));

        softly.assertThat(runtimes.size()).isGreaterThan(0);
    }

    @Test
    public void testFilter() throws Exception {
        RhoarBoosterCatalogService service = new RhoarBoosterCatalogService.Builder()
                .catalogRepository(System.getenv(LAUNCHER_BOOSTER_CATALOG_REPOSITORY)).catalogRef("vertx_two_versions")
                .filter(b -> {
                    Runtime r = b.getRuntime();
                    return r != null && r.getId().equals("vert.x");
                }).build();

        service.index().get();

        softly.assertThat(service.getRuntimes()).containsOnly(new Runtime("vert.x"));
    }

    private RhoarBoosterCatalogService buildDefaultCatalogService() {
        if (defaultService == null) {
            defaultService = new RhoarBoosterCatalogService.Builder()
                    .catalogRepository(HOST + ":" + PORT + "/" + REPO_NAME).catalogRef("vertx_two_versions")
                    .build();
        }
        return defaultService;
    }
}
