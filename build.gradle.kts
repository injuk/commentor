import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.0"

    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion

    id("org.openapi.generator") version "6.6.0"
    id("nu.studer.jooq") version "8.2.1"

    id("io.kotest") version "0.4.10"
}

group = "ga.injuk"
version = "1.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    testImplementation("io.projectreactor:reactor-test")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


    // kotest
    val kotestVersion = "5.5.5"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion") // optional, for kotest assertions
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion") // required
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion") // for data test
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2") // for spring test

    // mockk
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.ninja-squad:springmockk:3.1.1")

    // test containters
    val testContainersVersion = "1.18.0"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    // jooq
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.18.3")
    jooqGenerator("org.postgresql:postgresql:42.3.4")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.hashids:hashids:1.0.3")
    testImplementation("org.flywaydb:flyway-core")
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDirs(
            listOf(
                "$buildDir/generated/openapi/src/main",
                "$buildDir/generated/jooq/src/main/jooq",
            )
        )
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    generateApiDocumentation.set(false)
    generateModelDocumentation.set(false)

    inputSpec.set("$rootDir/src/main/resources/commentor-api.yml")
    outputDir.set("$buildDir/generated/openapi/")

    apiPackage.set("ga.injuk.commentor.operations")
    modelPackage.set("ga.injuk.commentor.models")

    configOptions.set(
        mapOf(
            "reactive" to "false",
            "interfaceOnly" to "true",
            "enumPropertyNaming" to "UPPERCASE",
            "useBeanValidation" to "true",
            "useTags" to "true",
            "annotationLibrary" to "none",
            "documentationProvider" to "none",
            "useSpringBoot3" to "true"
        )
    )

    additionalProperties = mapOf(
        "gradleBuildFile" to "false"
    )
}

jooq {
    version.set("3.18.6")
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.INFO
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://127.0.0.1:35432/commentor"
                    user = "postgres"
                    password = "my-first-nest-pw"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = System.getenv("COMMENTOR_SCHEMA")
                        excludes = "flyway_schema_history.*"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isFluentSetters = false
                    }
                    target.apply {
                        packageName = "com.mzc.cloudplex.download.persistence.jooq"
                        directory = "$buildDir/generated/jooq/src/main/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}