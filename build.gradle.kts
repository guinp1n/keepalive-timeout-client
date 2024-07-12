plugins {
    id ("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

group = "com.hivemq.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("com.hivemq:hivemq-mqtt-client:1.3.0")
    implementation("io.netty:netty-handler:4.1.77.Final")
}

/*tasks.getByName<Test>("test") {
    useJUnitPlatform()
}*/

tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        mergeServiceFiles()
        manifest {
            attributes(
                "Main-Class" to "com.hivemq.client.mqtt.examples.TlsDemo"
            )
        }
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}