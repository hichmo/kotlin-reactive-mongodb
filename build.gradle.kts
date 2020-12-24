import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.3.0"
    id("org.openapi.generator") version "4.3.1"
}

group = "com.hmo.rd"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.15.1"

dependencies {

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // OpenApi
    implementation("io.swagger:swagger-annotations:1.6.0")
    compileOnly("org.springdoc:springdoc-openapi-webflux-ui:1.5.1")

    // Jackson
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.openapitools:jackson-databind-nullable:0.2.0")

    // Testcontainers
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


val openApiDir = "${rootDir}/openapi"
val openApiBuildDir = "${buildDir}/openapi"

sourceSets.main {
    java.srcDirs("${openApiBuildDir}/src/main/java", "${openApiBuildDir}/src/main/kotlin")
    resources.srcDir("${openApiBuildDir}/src/main/resources")
}

val compileKotlin: KotlinCompile by tasks

openApiGenerate {
    generatorName.set("spring")
    apiPackage.set("com.hmo.rd.controller")
    modelPackage.set("com.hmo.rd.model")
    modelNameSuffix.set("DTO")
    inputSpec.set("${openApiDir}/api.yaml")
    outputDir.set(openApiBuildDir)
    val mapOptions = mutableMapOf<String, String>()
    mapOptions["dateLibrary"] = "java8"
    mapOptions["delegatePattern"] = "true"
    mapOptions["reactive"] = "true"
    configOptions.set(mapOptions)
}

task("copyDocs", Copy::class) {
    from("${openApiDir}/.openapi-generator-ignore")
    into(openApiBuildDir)
}

compileKotlin.dependsOn("copyDocs", "openApiGenerate")