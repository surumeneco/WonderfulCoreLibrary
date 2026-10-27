import org.gradle.jvm.tasks.Jar

val verifyStandalonePluginJar by tasks.registering {
    dependsOn(tasks.named("jar"))
    doLast {
        val file = tasks.named<Jar>("jar").get().archiveFile.get().asFile
        val contents = zipTree(file)
        for (required in listOf(
                "co/surumene/wcl/api/runtime/WorkIndex.class",
                "co/surumene/wcl/core/runtime/DefaultWorkIndex.class",
                "co/surumene/wcl/plugin/WonderfulCoreLibraryPlugin.class")) {
            check(contents.matching { include(required) }.files.isNotEmpty()) {
                "WCL plugin JAR missing $required"
            }
        }
        val resource = contents.matching { include("plugin.yml") }.singleFile.readText()
        check(resource.contains("name: WonderfulCoreLibrary"))
        check(!resource.contains("\${version}")) { "Unexpanded plugin version" }
        check(!resource.contains("SNAPSHOT")) { "Release-candidate JAR must not use a SNAPSHOT version" }
        check(contents.matching { include("config.yml") }.files.isEmpty()) {
            "WCL must not have an independent config.yml"
        }
    }
}

tasks.named("check") { dependsOn(verifyStandalonePluginJar) }

dependencies {
    testImplementation("org.mockito:mockito-core:5.20.0")
}
