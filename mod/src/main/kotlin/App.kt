import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.clientapi.sound.SoundCategory.*
import dev.xdark.clientapi.sound.SoundRequest
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture

class App : KotlinMod() {

    private val music = hashMapOf<String, String>()

    private val sounds = HashMap<String, SoundRequest>()
    private val musicDir = Paths.get("cache/func/music")
    private var currentSoundKey: String? = null
    private var currentSound: String? = null

    override fun onEnable() {

        UIEngine.initialize(this)
        Files.createDirectories(musicDir)

        registerChannel("func:sound") {

            val url = NetUtil.readUtf8(this)

            println("Get request $url")

            music[url] = "FUNC${url.hashCode()}FUNC"

            var builder = SoundRequest.Builder.builder()
                .pitch(readFloat())
                .volume(readFloat())
                .repeating(readBoolean())
                .category(
                    when (readInt()) {
                        0 -> MASTER
                        1 -> MUSIC
                        2 -> RECORDS
                        3 -> WEATHER
                        4 -> BLOCKS
                        5 -> HOSTIL
                        6 -> NEUTRAL
                        7 -> PLAYERS
                        8 -> AMBIENT
                        else -> VOICE
                    }
                ).attenuationType(SoundRequest.AttenuationType.NONE)

            if (readBoolean()) builder = builder.posX(readFloat()).posY(readFloat()).posZ(readFloat())

            playSound(url, builder)
        }
    }

    private fun playSound(url: String, sound: SoundRequest.Builder) {

        println("Player sound: $url")

        if (currentSound != null) {
            clientApi.soundHandler().stopSound(currentSound)
            currentSound = null
            currentSoundKey = null
        }

        getSoundRequest(url, sound).thenAccept {
            if (it == null) {
                println("Request is null! $url")

                return@thenAccept
            }

            clientApi.minecraft().execute {
                println("Execute sound: $url")

                currentSoundKey = url
                currentSound = clientApi.soundHandler().playSound(it)
            }
        }
    }

    private fun getSoundRequest(urlMusic: String, sound: SoundRequest.Builder): CompletableFuture<SoundRequest> {
        val current = sounds[urlMusic]

        if (current != null) {
            println("Return cached sound! $urlMusic")
            return CompletableFuture.completedFuture(current)
        }

        val pair = music[urlMusic]

        return CompletableFuture.supplyAsync {

            println("Start async sound! $urlMusic")

            val path = musicDir.resolve("$pair.ogg")

            if (Files.notExists(path)) {
                println("Start loading sound! $urlMusic")

                var connection: HttpURLConnection? = null
                try {
                    val url = URL(urlMusic)
                    connection = url.openConnection() as HttpURLConnection
                    connection.useCaches = false
                    connection.addRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0"
                    )
                    connection.connect()

                    val bytes = connection.inputStream.readBytes()
                    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

                    ResourceLocation.of("file", path.toString())
                } catch (ex: IOException) {

                    ex.printStackTrace()
                    null
                } finally {
                    connection?.disconnect()
                }
            } else {
                println("Return exited sound! $urlMusic")

                ResourceLocation.of("file", path.toString())
            }
        }.thenApply {
            if (it == null) return@thenApply null

            println("Playing sound! $urlMusic")

            val builder = sound.location(it).build()

            sounds[urlMusic] = builder
            builder
        }
    }
}