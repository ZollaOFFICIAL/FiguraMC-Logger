package mc.figuralogger.mixin;

import mc.figuralogger.util.SaveUtils;
import net.minecraft.nbt.NbtCompound;
import org.figuramc.figura.avatar.UserData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Hooks Figura's UserData#loadAvatar(NbtCompound) to save the exact tag.
 * Your decompile showed: public void loadAvatar(class_2487 nbt)
 * In Yarn named dev that's NbtCompound.
 */
@Mixin(UserData.class)
public class UserDataMixin {

    @Inject(method = "loadAvatar(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void figlog$onLoadAvatar(NbtCompound nbt, CallbackInfo ci) {
        UserData self = (UserData)(Object)this;
        UUID owner = self.id; // public final UUID id
        SaveUtils.saveAvatar(nbt, owner, null);
    }
}
