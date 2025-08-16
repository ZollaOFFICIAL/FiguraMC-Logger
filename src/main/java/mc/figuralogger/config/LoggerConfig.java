package mc.figuralogger.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LoggerConfig {
    // Turn this off if you only want the moderation .zip
    public static boolean MIRROR_TO_FIGURA_LOCAL = true;

    // Where to mirror for Figura’s local loader:
    // .minecraft/figura/local/captures/
    public static Path figuraLocalCapturesDir() {
        return FabricLoader.getInstance()
                .getGameDir()
                .resolve("figura")
                .resolve("local")
                .resolve("captures");
    }
}
