package mc.figuralogger;

import mc.figuralogger.config.LoggerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class LoggerMod implements ClientModInitializer {
    public static final String MODID = "figuralogger";
    public static final Logger LOG = Logger.getLogger("FiguraLogger");

    // Where we put moderation zips
    public static Path LOG_DIR;

    // Where we mirror a ready-to-use avatar.nbt for Figura
    public static Path LOCAL_DIR;

    @Override
    public void onInitializeClient() {
        Path gameDir = FabricLoader.getInstance().getGameDir();

        LOG_DIR = gameDir.resolve("LoggedModels");
        LOCAL_DIR = LoggerConfig.figuraLocalCapturesDir();

        try { Files.createDirectories(LOG_DIR); } catch (Exception e) {
            LOG.warning("Failed to create log dir: " + e.getMessage());
        }
        try { Files.createDirectories(LOCAL_DIR); } catch (Exception e) {
            LOG.warning("Failed to create local mirror dir: " + e.getMessage());
        }

        LOG.info("[FiguraLogger] Ready. Zips -> " + LOG_DIR.toAbsolutePath());
        LOG.info("[FiguraLogger] Local mirror -> " + LOCAL_DIR.toAbsolutePath());
    }
}
