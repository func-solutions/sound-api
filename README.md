# sound-api
Инструмент для работы с web-конфигурациями.

## Подключение sound-api
Для того чтобы использовать sound-api, нам всего лишь
нужно добавить зависимость в BuildScript (build.gradle)

```groovy
repositories {
    mavenCentral()
    maven {
        url 'https://repo.c7x.dev/repository/maven-public/'
        credentials {
            username System.getenv("CRI_REPO_LOGIN")
            password System.getenv("CRI_REPO_PASSWORD")
        }
    }
}

dependencies {
    implementation 'me.func:sound-api:1.0.5' // сама библиотека
    implementation 'me.func:animation-api:3.8.3' // нужная библиотека для работы
}
```

## Использование sound-api

Утилита Music (нужна для блокировки категорий звуков и определенных id)
```kotlin
Music // этот класс нужно дергать в onEnable, чтобы все загрузилось
    .block(Category.VOICE) 
    .block(Category.PLAYERS) // выключить звуки игроков
```

```kotlin
Music.stopSound(player, ...) // остановить музыку
```

Билдер Sound (инструмент для включения звуков игроку)
```kotlin
Sound("ссылка на .ogg файл") // если не работает, попробуйте Vorbis кодировку
    .category(Category.WEATHER) // категория звука (природа)
    .pitch(1.0f) // высота
    .volume(0.5f) // громкость
    .location(player.location) // локация (можно не указывать)
    .repeating(false) // будет ли звук повторяться
    .send(player, ...) // отправка игрокам
```

Быстрой код для тестирования через команду (не забудь включить звуки)

`/sound https://storage.c7x.dev/func/sound/example.ogg 0`

```kotlin
command("sound") { player, args ->
    Sound(args[0])
        .category(Category.values()[args[1].toInt()])
        .pitch(1.0f)
        .volume(0.5f)
        .location(player.location)
        .repeating(false)
        .send(player)
}
```

