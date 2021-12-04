import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.31"
  //kotlin("jvm") version "1.6.0"
  id("org.jetbrains.compose") version "1.0.0"
}

group = "github.maartyl"
version = "1.0"

repositories {
  google()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation(compose.desktop.currentOs)
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0-RC")

//    implementation("org.neo4j.driver:neo4j-java-driver:4.3.5")
//    implementation("com.arangodb:arangodb-java-driver:6.14.0")
//    implementation("com.arangodb:jackson-dataformat-velocypack:3.0.0")
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

compose.desktop {
  application {
    mainClass = "MainKt"
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "LifeJournalTracking"
      packageVersion = "1.0.0"
    }
  }
}