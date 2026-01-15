plugins {
    java
    id("java-library")
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bravos.steak"
version = "1.0.1"
description = "steak-security-starter"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-security:4.0.1")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    api("org.springframework.boot:spring-boot-autoconfigure:4.0.1")
    api("org.springframework:spring-context:7.0.2")
    api("org.springframework:spring-web:7.0.2")
    implementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.5.9")
    api("com.github.Bravos-World:steak-utils:v1.1.4")
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

