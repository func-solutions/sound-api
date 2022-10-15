dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    compileOnly("cristalix:dark-paper:21.02.03")
    compileOnly("me.func:animation-api:3.8.3")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "sound-api"
            from(components["java"])
        }
    }
}