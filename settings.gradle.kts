val springBootVersion: String by settings
val springDependencyManagementVersion: String by settings
val kotlinVersion: String by settings
val benManesVersionsVersion: String by settings

pluginManagement {
    plugins {
        id("org.springframework.boot") version "$springBootVersion"
        id("io.spring.dependency-management") version "$springDependencyManagementVersion"
        kotlin("jvm") version "$kotlinVersion"
        kotlin("plugin.spring") version "$kotlinVersion"
        id("com.github.ben-manes.versions") version "$benManesVersionsVersion"
    }
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "TaskGraphTool"