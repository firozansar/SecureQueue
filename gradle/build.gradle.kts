import org.jetbrains.kotlin.konan.properties.Properties

apply {
    from("$rootDir/gradle/detekt.gradle")
    from("$rootDir/gradle/versioning-script.gradle.kts")
    from("$rootDir/gradle/update-changelog-script.gradle.kts")
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()

    val localPropertiesFile = project.rootProject.file("local.properties")
    val localProperties = Properties()
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    maven(url = uri("https://maven.pkg.github.com/ComparetheMarket/mobile.consumers.android")) {
        credentials {
            username = localProperties.getProperty("gpr.user")
                ?: System.getenv("CTM_GITHUB_PACKAGES_READ_WRITE_USERNAME")
            password = localProperties.getProperty("gpr.key")
                ?: System.getenv("CTM_GITHUB_PACKAGES_READ_WRITE_ACCESS_TOKEN")
        }
    }
    maven(url = uri("https://maven.pkg.github.com/ComparetheMarket/mobile.common.android")) {
        credentials {
            username = localProperties.getProperty("gpr.user")
                ?: System.getenv("CTM_GITHUB_PACKAGES_READ_WRITE_USERNAME")
            password = localProperties.getProperty("gpr.key")
                ?: System.getenv("CTM_GITHUB_PACKAGES_READ_WRITE_ACCESS_TOKEN")
        }
    }
}

plugins {
    kotlin("android")
    kotlin("kapt")

    id("com.android.library")
    id("org.jetbrains.dokka") version "1.6.10"
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        consumerProguardFiles("consumer-rules.pro")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // The following argument makes the Android Test Orchestrator run its
        // "pm clear" command after each test invocation. This command ensures
        // that the app's state is completely cleared between tests.
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        //multiDexEnabled = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        unitTests.all { it.useJUnitPlatform() }
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += "async"
    productFlavors {
        register("coroutines") {
            dimension = "async"
        }
        register("rxjava3") {
            dimension = "async"
        }
    }
    compileOptions {
        // Desugar Java 8+ https://developer.android.com/studio/write/java8-support#library-desugaring
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    useLibrary("android.test.runner")
    useLibrary("android.test.base")
    useLibrary("android.test.mock")

    // Required to run Espresso/Integration tests
    packagingOptions {
        resources.excludes += "META-INF/LICENSE.md"
        resources.excludes += "META-INF/LICENSE-notice.md"
        resources.excludes += "META-INF/AL2.0"
        resources.excludes += "META-INF/LGPL2.1"
        resources.excludes += "META-INF/gradle/incremental.annotation.processors"
    }
    configurations {
        androidTestImplementation {
            exclude(group = "io.mockk", module = "mockk-agent-jvm")
        }
    }

    publishing {
        multipleVariants {
            includeBuildTypeValues("release")
        }
    }
}

dependencies {

    coreLibraryDesugaring(libs.desugarJdk)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.rx3)

    implementation(libs.consumers)
    implementation(libs.commonFramework)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshiCodeGen)

    // Dagger
    implementation(libs.dagger.runtime)
    kapt(libs.dagger.compiler)

    // RxJava3
    implementation(libs.rxJava)
    implementation(libs.rxAndroid)

    // Unit test  dependencies
    testImplementation(libs.mockK)
    testRuntimeOnly(libs.junitVintageEngine)
    testImplementation(libs.junit5Api)
    testRuntimeOnly(libs.junit5Engine)
    testImplementation(libs.junit5Params)
    testImplementation(libs.extJUnit)
    testImplementation(libs.kotlinxCoroutinesTest)

    // Android test dependencies
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(libs.androidTestMonitor)
    androidTestImplementation(libs.kotlinxCoroutinesTest)
    androidTestImplementation(libs.mockKAndroid)
    androidTestImplementation(libs.mockKAgentJvm)
    androidTestImplementation(libs.androidTestRunner)
    androidTestUtil(libs.androidTestOrchestrator)

    androidTestImplementation(libs.dagger.runtime)
    kaptAndroidTest(libs.dagger.compiler)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//region build name & versioning
group = "com.comparethemarket.library"

val versionQualifier = ""

version = libs.versions.contentLibraryVersionName.get()

tasks.register("createVersionFile") {
    val folder = "$projectDir/build/libs"

    mkdir(folder)
    File("$folder/aar-version.txt")
        .writeText("$version")
}

fun getQualifier(): String {
    return versionQualifier.takeIf { it.isNotEmpty() }
        ?.let { "-$it" }
        .orEmpty()
}

fun getBuildNumber(): String {
    val bitriseBuildNumber = System.getenv("SOURCE_BITRISE_BUILD_NUMBER")
        ?: System.getenv("BITRISE_BUILD_NUMBER")

    return bitriseBuildNumber
        ?: "local"
}
//endregion

//region documentation creation
val dokkaDir = buildDir.resolve("dokka")

tasks.dokkaHtml.configure {
    outputDirectory.set(dokkaDir)
}
//endregion

// region publishing

tasks.register<Jar>(name = "sourcesJar") {
    from(android.sourceSets["main"].java.srcDirs)
    from(android.sourceSets["coroutines"].java.srcDirs)
    from(android.sourceSets["rxjava3"].java.srcDirs)
    archiveClassifier.set("sources")
}

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

publishing {
    repositories {
        maven {
            name = "mobile.content.android"
            url =
                uri("https://maven.pkg.github.com/ComparetheMarket/mobile.content.android")
            credentials {
                // Try to find the values from gradle properties e.g. local.properties, or check for an env variable
                username = localProperties.getProperty("gpr.user")
                    ?: System.getenv("CTM_GITHUB_PACKAGES_READ_WRITE_USERNAME")
                password = localProperties.getProperty("gpr.key")
                    ?: System.getenv("CTM_GITHUB_PACKAGES_READ_WRITE_ACCESS_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("coroutinesRelease") {
            groupId = "com.comparethemarket.library"
            artifactId = "content-coroutines"
            version = project.version.toString()
            description =
                "Content framework utilising Coroutines for asynchronous functions."
            afterEvaluate {
                from(components["coroutinesRelease"])
                artifact(tasks.named<Jar>("sourcesJar"))
            }
        }

        register<MavenPublication>("rxjava3Release") {
            groupId = "com.comparethemarket.library"
            artifactId = "content-rxjava3"
            version = project.version.toString()

            description =
                "Content framework utilising RxJava3 for asynchronous functions."
            afterEvaluate {
                from(components["rxjava3Release"])
                artifact(tasks.named<Jar>("sourcesJar"))
            }
        }
    }
}
// endregion
