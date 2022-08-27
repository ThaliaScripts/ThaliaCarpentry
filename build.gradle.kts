plugins {
    kotlin("jvm") version "1.6.21"
    id("gg.essential.defaults") version "0.1.10"
    id("gg.essential.loom") version "0.10.0.+"
    idea
}

val modVersion = "0.1"
val modGroup = "me.mi0"
val modBaseName = "carpentry"
version = modVersion
group = modGroup
base.archivesName.set(modBaseName)

loom {
    forge {
        mixinConfig("carpentry.mixins.json")
    }
    mixin {
        defaultRefmapName.set("carpentry.mixins.refmap.json")
    }
    launchConfigs {
        getByName("client") {
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            arg("--mixin", "carpentry.mixins.json")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.sk1er.club/repository/maven-public/")
    maven("https://repo.sk1er.club/repository/maven-releases/")
    maven("https://jitpack.io")
}

val embed by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    embed("gg.essential:loader-launchwrapper:1.1.3")
    compileOnly("gg.essential:essential-1.8.9-forge:2581") {
        exclude(module = "asm")
        exclude(module = "asm-commons")
        exclude(module = "asm-tree")
        exclude(module = "gson")
    }

    embed("com.github.LlamaLad7:MixinExtras:0.0.8")
    annotationProcessor("com.github.LlamaLad7:MixinExtras:0.0.8")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn", "-Xjvm-default=all", "-Xrelease=8", "-Xbackend-threads=0")
        languageVersion = "1.6"
    }
}

kotlin {
    jvmToolchain {
        check(this is JavaToolchainSpec)
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.jar {
    from(embed.files.map { zipTree(it) })

    manifest.attributes(mapOf(
        "ModSide" to "CLIENT",
        "Main-Class" to "me.mi0.Carpentry",
        "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
        "TweakOrder" to "0",
        "MixinConfigs" to "carpentry.mixins.json"
    ))
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", "1.8.9")

    filesMatching("mcmod.info") {
        expand(mapOf("version" to project.version, "mcversion" to "1.8.9"))
    }
    dependsOn(tasks.compileJava)
}