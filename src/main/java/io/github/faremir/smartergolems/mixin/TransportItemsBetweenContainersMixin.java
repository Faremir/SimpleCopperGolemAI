package io.github.faremir.smartergolems.mixin;

import io.github.faremir.smartergolems.config.SmarterGolemsConfigManager;
import io.github.faremir.smartergolems.mixin.accessor.TransportItemsBetweenContainersAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers.TransportItemTarget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TransportItemsBetweenContainers.class)
public abstract class TransportItemsBetweenContainersMixin {

    /**
     * The item most recently picked from a source container.
     */
    @Unique
    @Nullable
    private Item smarterGolems$lastPickedItem;

    /**
     * The last chest where the remembered item was successfully deposited.
     */
    @Unique
    @Nullable
    private BlockPos smarterGolems$lastChest;

    /**
     * The target currently being interacted with while carrying an item.
     * <p>
     * This is temporary state used to associate a successful deposit with its
     * target before it becomes the remembered chest.
     */
    @Unique
    @Nullable
    private BlockPos smarterGolems$currentTargetPos;


    /**
     * Records the item picked by golem and preserves the remembered chest when the same item is picked again.
     */
    @Inject(method = "pickUpItems", at = @At("TAIL"))
    private void setLastItem(PathfinderMob body, Container container, CallbackInfo ci) {
        if (body.getMainHandItem().isEmpty()) {
            this.smarterGolems$lastPickedItem = null;
            this.smarterGolems$lastChest = null;
            return;
        }

        Item newItem = body.getMainHandItem().getItem();
        if (this.smarterGolems$lastPickedItem == newItem) {
            return;
        }

        this.smarterGolems$lastPickedItem = newItem;
        this.smarterGolems$lastChest = null;
    }

    /**
     * Remembers the current target after golem completely deposits the carried item. A partial or failed deposit is never remembered.
     */
    @Inject(method = "putDownItem", at = @At("TAIL"))
    private void setLastChest(PathfinderMob body, Container container, CallbackInfo ci) {
        if (body.getMainHandItem().isEmpty()) {
            if (this.smarterGolems$currentTargetPos != null) {
                this.smarterGolems$lastChest = this.smarterGolems$currentTargetPos;
            }
        } else {
            this.smarterGolems$lastChest = null;
        }

        this.smarterGolems$currentTargetPos = null;
    }

    /**
     * Stores the position of the target for the current deposit operation.
     */
    @Inject(method = "onReachedTarget", at = @At("HEAD"))
    private void setCurrentTargetPos(TransportItemTarget target, Level level, PathfinderMob body, CallbackInfo ci) {
        if (body.getMainHandItem().isEmpty()) {
            return;
        }

        this.smarterGolems$currentTargetPos = target.pos();
    }

    /**
     * Tries to use last chest if vanilla conditions allow it and golem carries same item as previously deposited.
     */
    @Inject(method = "getTransportTarget", at = @At("HEAD"), cancellable = true)
    private void tryUseLastChest(ServerLevel level, PathfinderMob body, CallbackInfoReturnable<Optional<TransportItemTarget>> cir) {
        if (!SmarterGolemsConfigManager.get().isPreferredChestDepositEnabled()) {
            return;
        }

        if (body.getMainHandItem().isEmpty() || this.smarterGolems$lastChest == null || body.getMainHandItem().getItem() != this.smarterGolems$lastPickedItem) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(this.smarterGolems$lastChest);

        if (!(blockEntity instanceof ChestBlockEntity)) {
            this.smarterGolems$lastChest = null;
            return;
        }

        TransportItemsBetweenContainersAccessor invoker = ((TransportItemsBetweenContainersAccessor) this);

        TransportItemTarget target = invoker.smarterGolems$isTargetValidToPick(body, level, blockEntity, TransportItemsBetweenContainersAccessor.smarterGolems$getVisitedPositions(body), TransportItemsBetweenContainersAccessor.smarterGolems$getUnreachablePositions(body), invoker.smarterGolems$getTargetSearchArea(body));

        if (target == null) {
            this.smarterGolems$lastChest = null;
            return;
        }

        cir.setReturnValue(Optional.of(target));
    }

    /**
     * Searches for last picked item when golem is selecting items from copper chest.
     * Falls back to native search when last item was not found.
     */
    @Redirect(method = "pickUpItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers;pickupItemFromContainer(Lnet/minecraft/world/Container;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack pickupLastItem(Container container) {
        if (!SmarterGolemsConfigManager.get().isPreferredItemPickupEnabled()) {
            return TransportItemsBetweenContainersAccessor.smarterGolems$pickupItemFromContainer(container);
        }

        Item preferredItem = this.smarterGolems$lastPickedItem;

        if (preferredItem != null) {
            int slot = 0;
            for (ItemStack itemStack : container) {
                if (!itemStack.isEmpty() && itemStack.getItem() == preferredItem) {
                    int itemCount = Math.min(itemStack.getCount(), 16);
                    return container.removeItem(slot, itemCount);
                }
                slot++;
            }
        }

        return TransportItemsBetweenContainersAccessor.smarterGolems$pickupItemFromContainer(container);
    }
}