import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import versioning.BuildConfig

plugins {
    id("com.gradleup.shadow")
}

tasks.named<ShadowJar>("shadowJar") {
    minimize {
        // adventure's DataComponentValueConverter gson provider is only referenced via
        // ServiceLoader, so minimize() strips it and adventure's static init then throws
        // (ServiceConfigurationError) on enable. Keep the gson serializer's classes.
        exclude(dependency("net.kyori:adventure-text-serializer-gson:.*"))
    }
    archiveFileName = "${rootProject.name}-${project.name}-${rootProject.version}.jar"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    if (BuildConfig.relocate) {
        if (BuildConfig.shadePE) {
            relocate("io.github.retrooper.packetevents", "ac.comet.cometac.shaded.io.github.retrooper.packetevents")
            relocate("com.github.retrooper.packetevents", "ac.comet.cometac.shaded.com.github.retrooper.packetevents")
            relocate("net.kyori", "ac.comet.cometac.shaded.kyori") // use PE's built-in adventure instead when not shading PE
        }
        relocate("club.minnced", "ac.comet.cometac.shaded.discord-webhooks")
        relocate("org.slf4j", "ac.comet.cometac.shaded.slf4j") // Required by discord-webhooks
        relocate("github.scarsz.configuralize", "ac.comet.cometac.shaded.configuralize")
        relocate("com.github.puregero", "ac.comet.cometac.shaded.com.github.puregero")
        relocate("com.google.code.gson", "ac.comet.cometac.shaded.gson")
        relocate("alexh", "ac.comet.cometac.shaded.maps")
        relocate("it.unimi.dsi.fastutil", "ac.comet.cometac.shaded.fastutil")
        relocate("okhttp3", "ac.comet.cometac.shaded.okhttp3")
        relocate("okio", "ac.comet.cometac.shaded.okio")
        relocate("org.yaml.snakeyaml", "ac.comet.cometac.shaded.snakeyaml")
        relocate("org.json", "ac.comet.cometac.shaded.json")
        relocate("org.intellij", "ac.comet.cometac.shaded.intellij")
        relocate("org.jetbrains", "ac.comet.cometac.shaded.jetbrains")
        relocate("org.incendo", "ac.comet.cometac.shaded.incendo")
        relocate("io.leangen.geantyref", "ac.comet.cometac.shaded.geantyref") // Required by cloud
        relocate("com.zaxxer", "ac.comet.cometac.shaded.zaxxer") // Database history
    }
    mergeServiceFiles()
}

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}
