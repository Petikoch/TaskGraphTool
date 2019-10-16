import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.github.ben-manes.versions")
}

group = "ch.petikoch.toools"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

extra["vaadinVersion"] = "8.9.1"

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {
    // spring boot starters
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.vaadin:vaadin-spring-boot-starter")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // libraries with version managed by spring boot
    implementation("org.apache.commons:commons-lang3")

    // additional libraries
    implementation("guru.nidi:graphviz-kotlin:0.11.0")
    implementation("com.thoughtworks.xstream:xstream:1.4.11.1")
    implementation("com.google.guava:guava:28.1-jre")
    implementation("org.jooq:joox-java-6:1.6.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict") // spring boot recommends this, see https://github.com/spring-io/initializr/issues/591
        jvmTarget = "1.8"
    }
}