plugins {
    id("java")
}

group = "de.jotoho.fhswf.se"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.slf4j", "slf4j-simple", "2.0.+")
}

tasks.test {
    useJUnitPlatform()
}
