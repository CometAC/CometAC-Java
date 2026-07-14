import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission
import versioning.BuildConfig

plugins {
    `maven-publish`
    comet.`base-conventions`
    comet.`shadow-conventions`
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
    id("xyz.jpenilla.run-paper") version "3.0.0-beta.1"
}

repositories {
    // 1. Fallback for non-exclusive deps (e.g. Maven Central deps)
    if (BuildConfig.mavenLocalOverride) mavenLocal()

    // 2. Exclusive Repositories (One HTTP request per dep)
    exclusive("https://repo.papermc.io/repository/maven-public/", { name = "papermc" }) {
        includeGroup("io.papermc.paper")
        includeGroup("net.md-5")
    }

    exclusive("https://libraries.minecraft.net", { mavenContent { releasesOnly() } }) {
        includeModule("com.mojang", "brigadier")
    }

    exclusive("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        includeGroup("me.clip")
    }

    exclusive("https://repo.grim.ac/snapshots") {
        includeGroup("ac.grim.grimac")
        includeGroup("com.github.retrooper")
    }

    exclusive("https://nexus.scarsz.me/content/repositories/releases", { mavenContent { releasesOnly() } }) {
        includeGroup("github.scarsz")
    }

    mavenCentral()
}


dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.luckperms)

    if (BuildConfig.shadePE) {
        implementation(libs.packetevents.spigot)
    } else {
        compileOnly(libs.packetevents.spigot)
    }
    implementation(libs.cloud.paper)
    implementation(libs.adventure.platform.bukkit)
    implementation(libs.comet.bukkit.internal)

    implementation(project(":common"))
    shadow(project(":common"))
}

bukkit {
    name = "CometAC"
    author = "KaelusMC"
    main = "ac.comet.cometac.platform.bukkit.CometACBukkitLoaderPlugin"
    website = "https://github.com/KaelusMC/CometAC"
    apiVersion = "1.13"
    foliaSupported = true

    if (!BuildConfig.shadePE) {
        depend = listOf("packetevents")
    }

    softDepend = listOf(
        "ProtocolLib",
        "ProtocolSupport",
        "Essentials",
        "ViaVersion",
        "ViaBackwards",
        "ViaRewind",
        "Geyser-Spigot",
        "floodgate",
        "FastLogin",
        "PlaceholderAPI",
        "LuckPerms",
        // Driver holder mods — softdepend so each backend's driver class
        // resolves through the linked classloader.
        "sqlite-jdbc",
        "mysql-jdbc",
        "postgresql-jdbc",
        "mongodb-driver",
        "jedis",
    )

    permissions {
        register("cometac.alerts") {
            description = "Receive alerts for violations"
            default = Permission.Default.OP
        }

        register("cometac.alerts.enable-on-join") {
            description = "Enable alerts on join"
            default = Permission.Default.OP
        }

        register("cometac.performance") {
            description = "Check performance metrics"
            default = Permission.Default.OP
        }

        register("cometac.profile") {
            description = "Check user profile"
            default = Permission.Default.OP
        }

        register("cometac.brand") {
            description = "Show client brands on join"
            default = Permission.Default.OP
        }

        register("cometac.brand.enable-on-join") {
            description = "Enable showing client brands on join"
            default = Permission.Default.OP
        }

        register("cometac.sendalert") {
            description = "Send cheater alert"
            default = Permission.Default.OP
        }

        register("cometac.nosetback") {
            description = "Disable setback"
            default = Permission.Default.FALSE
        }

        register("cometac.nomodifypacket") {
            description = "Disable modifying packets"
            default = Permission.Default.FALSE
        }

        register("cometac.disabled") {
            description = "Disable Comet checks while keeping player state tracked"
            default = Permission.Default.FALSE
        }

        register("cometac.exempt") {
            description = "Exempt from all checks"
            default = Permission.Default.FALSE
        }

        register("cometac.verbose") {
            description = "Receive verbose alerts for violations"
            default = Permission.Default.OP
        }

        register("cometac.verbose.enable-on-join") {
            description =
                "Enable verbose alerts on join"
            default = Permission.Default.FALSE
        }

        register("cometac.list") {
            description =
                "Shows lists of specific data"
            default = Permission.Default.FALSE
        }

        register("cometac.suspects") {
            description = "View players who recently flagged checks"
            default = Permission.Default.OP
        }

    }
}

publishing.publications.create<MavenPublication>("maven") {
    artifact(tasks["shadowJar"])
}

tasks {
    // 1.8.8 - 1.16.5   = Java 8
    // 1.17             = Java 16
    // 1.18 - 1.20.4    = Java 17
    // 1.20.5 - 1.21.11 = Java 21
    // 26.1+            = Java 25
    val version = "26.1.2"
    val javaVersion = JavaLanguageVersion.of(25)

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true",
        "-Dpaper.explicit-flush=true",
        "-DPaper.IgnoreJavaVersion=true"
    )

    runServer {
        minecraftVersion(version)
        runDirectory = projectDir.resolve("run/$version")

        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher = javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = javaVersion
        }

        jvmArgs = jvmArgsExternal
    }

    shadowJar {
        exclude("META-INF/services/javax.annotation.processing.Processor")

        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
}
