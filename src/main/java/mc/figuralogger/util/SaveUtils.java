package mc.figuralogger.util;

import mc.figuralogger.LoggerMod;
import mc.figuralogger.config.LoggerConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SaveUtils {

    public static void saveAvatar(NbtCompound nbt, UUID owner, byte[] rawBytesOrNull) {
        if (nbt == null || owner == null) return;

        // Compute short hash from compressed bytes
        byte[] bytes = rawBytesOrNull;
        if (bytes == null) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                NbtIo.writeCompressed(nbt, baos);
                bytes = baos.toByteArray();
            } catch (IOException e) {
                LoggerMod.LOG.warning("Failed to compress NBT for hashing: " + e.getMessage());
            }
        }

        String shortHash = "nohash";
        if (bytes != null) {
            try {
                var md = java.security.MessageDigest.getInstance("SHA-256");
                md.update(bytes);
                shortHash = HexFormat.of().formatHex(md.digest(), 0, 8);
            } catch (Exception ignored) {}
        }

        long ts = Instant.now().toEpochMilli();

        // ---------------------------
        // 1) Moderation zip
        // ---------------------------
        Path ownerDir = LoggerMod.LOG_DIR.resolve(owner.toString());
        String zipName = owner + "_" + shortHash + "_" + ts + ".zip";
        Path zipPath = ownerDir.resolve(zipName);

        try {
            Files.createDirectories(ownerDir);

            Path nbtPath = ownerDir.resolve("avatar.nbt");
            Path provPath = ownerDir.resolve("provenance.json");

            try (var out = Files.newOutputStream(nbtPath)) {
                NbtIo.writeCompressed(nbt, out);
            }

            String provJson = """
                {
                  "owner": "%s",
                  "hash": "%s",
                  "timestamp": "%s",
                  "notes": "captured by FiguraLogger"
                }
                """.formatted(owner, shortHash, Instant.now());
            Files.writeString(provPath, provJson);

            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                zs.putNextEntry(new ZipEntry("avatar.nbt"));
                Files.copy(nbtPath, zs);
                zs.closeEntry();

                zs.putNextEntry(new ZipEntry("provenance.json"));
                Files.copy(provPath, zs);
                zs.closeEntry();
            }

            Files.deleteIfExists(nbtPath);
            Files.deleteIfExists(provPath);

            LoggerMod.LOG.info("Saved moderation zip -> " + zipPath.toAbsolutePath());
        } catch (Exception e) {
            LoggerMod.LOG.warning("Failed to write moderation zip: " + e.getMessage());
        }

        // ---------------------------
        // 2) Mirrors for Figura Local
        //    A) Per-user archive: .minecraft/figura/local/captures/<uuid>/<timestamp>/avatar.nbt
        //    B) Fixed file Figura watches: .minecraft/figura/local/avatar.nbt
        // ---------------------------
        if (LoggerConfig.MIRROR_TO_FIGURA_LOCAL) {
            try {
                // A) per-user, time-stamped mirror
                Path mirrorDir = LoggerMod.LOCAL_DIR
                        .resolve(owner.toString())
                        .resolve(Long.toString(ts));
                Files.createDirectories(mirrorDir);

                Path mirrorNbt = mirrorDir.resolve("avatar.nbt");
                try (var out = Files.newOutputStream(mirrorNbt)) {
                    NbtIo.writeCompressed(nbt, out);
                }
                LoggerMod.LOG.info("Mirrored for Figura local -> " + mirrorNbt.toAbsolutePath());

                // B) fixed path: .../figura/local/avatar.nbt
                Path fixedLocalDir = LoggerMod.LOCAL_DIR.getParent(); // .../figura/local
                Files.createDirectories(fixedLocalDir);

                Path fixedNbt = fixedLocalDir.resolve("avatar.nbt");
                try (var out = Files.newOutputStream(fixedNbt)) {
                    NbtIo.writeCompressed(nbt, out);
                }
                LoggerMod.LOG.info("Mirrored latest to -> " + fixedNbt.toAbsolutePath());

            } catch (Exception e) {
                LoggerMod.LOG.warning("Failed to mirror to Figura local: " + e.getMessage());
            }
        }
    }
}
