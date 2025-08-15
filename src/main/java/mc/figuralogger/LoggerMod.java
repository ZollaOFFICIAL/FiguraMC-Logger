package mc.figuralogger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class LoggerMod implements ClientModInitializer {

    public static final String MODID = "figuralogger";
    public static final Logger LOG = Logger.getLogger("FiguraLogger");
    public static Path LOG_DIR;

    @Override
    public void onInitializeClient() {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        LOG_DIR = gameDir.resolve("LoggedModels");

        try {
            Files.createDirectories(LOG_DIR);
        } catch (Exception e) {
            LOG.warning("Failed to create log dir: " + e.getMessage());
        }

        LOG.info("[FiguraLogger] Mod loaded. Saving folder: " + LOG_DIR.toAbsolutePath());
    }
}
