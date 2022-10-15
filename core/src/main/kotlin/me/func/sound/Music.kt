package me.func.sound

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.listener
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedSoundEffect
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object Music : Listener {

    private val blockedCategories = hashSetOf<Int>()
    private val blockedSounds = hashSetOf<Int>()

    init {
        // Загрузка мода для звуков
        ModLoader.onJoining("https://storage.c7x.dev/func/sound/sound-mod.jar")

        // Обработка входа игроков
        listener(this)
    }

    @JvmStatic
    fun block(id: Int) = apply { blockedSounds.add(id) }

    @JvmStatic
    fun unblock(id: Int) = apply { blockedCategories.remove(id) }

    @JvmStatic
    fun block(category: Category) = apply { blockedCategories.add(category.ordinal) }

    @JvmStatic
    fun unblock(category: Category) = apply { blockedCategories.remove(category.ordinal) }

    @EventHandler
    fun PlayerJoinEvent.handle() {

        val pipeline = (player as CraftPlayer).handle.playerConnection.networkManager.channel.pipeline()

        // Добавляем логику для блокировок категорий и звуков
        pipeline.addBefore("packet_handler", "sound-" + player.name, object : ChannelDuplexHandler() {

            override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {

                // Отменить обычный пакет
                if (msg !is PacketPlayOutNamedSoundEffect) {
                    super.write(ctx, msg, promise)
                    return
                }

                // Узнать находится ли звук или категория в блокировке
                val blockCategory = blockedCategories.contains(msg.b.ordinal)
                val blockSound = blockedSounds.contains(msg.a.id)

                if (blockCategory || blockSound) return

                // Кидаем пакет дальше
                super.write(ctx, msg, promise)
            }
        })
    }

}