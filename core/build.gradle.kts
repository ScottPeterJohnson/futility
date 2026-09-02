description = "All futility"

dependencies {}

sourceSets.create("generator")

val generateWrappers = tasks.register<JavaExec>("generateWrappers") {
    description = "Generates the primitive wrapper value classes and operators."
    classpath = sourceSets["generator"].runtimeClasspath
    mainClass.set("net.justmachinery.futility.generator.WrappersGeneratorKt")
    val outputDir = layout.buildDirectory.dir("generated/wrappers")
    argumentProviders.add(CommandLineArgumentProvider { listOf(outputDir.get().asFile.path) })
    outputs.dir(outputDir)
}

kotlin.sourceSets.named("main") {
    kotlin.srcDir(generateWrappers)
}
