import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("http://localhost:8081/repository/maven/") }
}

plugins {
    java
    jacoco
    // kotlin("jvm") version "1.3.70"
    // kotlin("kapt") version "1.3.70"
    id("org.springframework.boot") version "2.3.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

jacoco {
    toolVersion = "0.8.4"
    reportsDir = file("$buildDir/jacoco/reports")
}

dependencies {
    //region Common
    val lombokV = "org.projectlombok:lombok:1.18.12"
    compileOnly(lombokV)
    annotationProcessor(lombokV)
    testCompileOnly(lombokV)
    testAnnotationProcessor(lombokV)

    implementation("org.apache.commons:commons-text:1.8")
    implementation("com.jayway.jsonpath:json-path:2.4.0")
    implementation("mysql:mysql-connector-java:5.1.49")
    implementation("mysql:mysql-connector-java:8.0.19")
    //endregion Common

    //region Spring
    val springBoot = "org.springframework.boot:spring-boot"
    val springBootV = "2.3.0.RELEASE"

    implementation("$springBoot-starter:$springBootV")
    implementation("$springBoot-starter-web:$springBootV")
    implementation("$springBoot-starter-mail:$springBootV")
    implementation("$springBoot-starter-jdbc:$springBootV")
    implementation("$springBoot-starter-security:$springBootV")
    implementation("$springBoot-starter-data-jpa:$springBootV")
    implementation("$springBoot-starter-actuator:$springBootV")
    implementation("$springBoot-starter-data-rest:$springBootV")
    implementation("$springBoot-starter-validation:$springBootV")
    implementation("$springBoot-configuration-processor:$springBootV")
    implementation("org.springframework.cloud:spring-cloud-starter:2.2.3.RELEASE")

    val springSecurityV = "5.3.2.RELEASE"
    val springSecurity = "org.springframework.security:spring-security"
    implementation("$springSecurity-web:$springSecurityV")
    implementation("$springSecurity-core:$springSecurityV")
    implementation("$springSecurity-config:$springSecurityV")

    testImplementation("$springBoot-test:$springBootV")
    //endregion Spring

    //region FM
    implementation("com.h2database:h2:1.4.200")
    implementation("org.jsmpp:jsmpp:2.3.9")
    implementation("org.snmp4j:snmp4j:3.4.1")
    implementation("org.snmp4j:snmp4j-agent:3.3.4")
    //endregion FM

    //region CM
    implementation("org.jline:jline:3.15.0")
    implementation("info.picocli:picocli:4.3.2")
    implementation("org.apache.commons:commons-lang3:3.10")
    implementation("org.erlang.otp:jinterface:1.6.1")
    implementation("com.google.code.gson:gson:2.8.6")

    //endregion CM

    //region WEB
    // implementation("org.zkoss:zsoup:1.8.2.5")
    // implementation("org.zkoss.zk:zk:9.0.0.1")
    // implementation("org.zkoss.zk:zhtml:9.0.0.1")
    // implementation("org.zkoss.zk:zkbind:9.0.0.1")
    // implementation("org.zkoss.common:zcommon:9.0.0.1")

    //endregion WEB

    //region Addition
    // implementation(kotlin("stdlib"))
    // implementation(kotlin("stdlib-jdk8"))
    // implementation(kotlin("reflect"))

    implementation("com.google.guava:guava:29.0-jre")
    implementation("org.apache.commons:commons-compress:1.20")
    implementation("javax.validation:validation-api:2.0.1.Final")

    //endregion
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "com.viettel.ems.fm.FM"
}

//region JACOCO
tasks.test {
    useJUnitPlatform()

    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$rootDir/jacoco/jacocoTest.exec"))
    }
    doLast {
        tasks["jacocoTestReport"]
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        html.destination = file("$buildDir/jacoco/html")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }

        rule {
            enabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}

//endregion JACOCO
