package me.func.sound

import me.func.mod.conversation.ModTransfer
import org.bukkit.Location
import org.bukkit.entity.Player
import ru.cristalix.core.math.V3

class Sound(var url: String) {

    private var pitch = 1f
    private var volume = 1f
    private var repeating = false
    private var category = Category.MASTER

    private var v3: V3? = null

    fun url(url: String) = apply { this.url = url }
    fun pitch(pitch: Float) = apply { this.pitch = pitch }
    fun volume(volume: Float) = apply { this.volume = volume }
    fun repeating(repeating: Boolean) = apply { this.repeating = repeating }
    fun category(category: Category) = apply { this.category = category }
    fun location(v3: V3) = apply { this.v3 = v3 }
    fun location(x: Double, y: Double, z: Double) = location(V3(x, y, z))
    fun location(location: Location) = location(location.x, location.y, location.z)

    fun send(player: Collection<Player>) = send(*player.toTypedArray())

    fun send(vararg player: Player) {
        val transfer = ModTransfer()
            .string(url)
            .float(pitch)
            .float(volume)
            .boolean(repeating)
            .integer(category.ordinal)

        val located = v3 != null

        transfer.boolean(located)

        if (located) transfer
            .float(v3?.x?.toFloat() ?: 0f)
            .float(v3?.y?.toFloat() ?: 0f)
            .float(v3?.z?.toFloat() ?: 0f)

        transfer.send("func:sound", *player)
    }

}