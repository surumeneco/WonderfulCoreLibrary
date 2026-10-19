import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources

plugins { base }

allprojects {
    group = "co.surumene"
    version = "0.1.0-SNAPSHOT"
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java-library")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        withSourcesJar()
    }
    dependencies {
        "testImplementation"(platform("org.junit:junit-bom:6.0.0"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
    tasks.withType<Test>().configureEach { useJUnitPlatform() }
}

project(":wcl-core") {
    dependencies { "api"(project(":wcl-api")) }
}

project(":wcl-plugin") {
    dependencies {
        "api"(project(":wcl-api"))
        "implementation"(project(":wcl-core"))
        "compileOnly"("io.papermc.paper:paper-api:26.2.build.129-stable")
        "testImplementation"("io.papermc.paper:paper-api:26.2.build.129-stable")
    }
    tasks.named<ProcessResources>("processResources") {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") { expand("version" to project.version) }
    }
    tasks.named<Jar>("jar") {
        dependsOn(":wcl-api:classes", ":wcl-core:classes")
        archiveBaseName.set("WonderfulCoreLibrary")
        from(project(":wcl-api").layout.buildDirectory.dir("classes/java/main"))
        from(project(":wcl-core").layout.buildDirectory.dir("classes/java/main"))
    }
}
