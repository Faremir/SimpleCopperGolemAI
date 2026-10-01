package io.github.faremir.smartergolems.mixin;

import io.github.faremir.smartergolems.config.SmarterGolemsConfigManager;
import io.github.faremir.smartergolems.mixin.accessor.TransportItemsBetweenContainersAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers.TransportItemTarget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TransportItemsBetweenContainers.class)
public abstract class TransportItemsBetweenContainersMixin {

    /**
     * The item most recently picked from a source container.
     */
    @Unique
    @Nullable
    private Item lastPickedItem;

    /**
     * The last chest where the remembered item was successfully deposited.
     */
    @Unique
    @Nullable
    private BlockPos lastChest;

    /**
     * The target currently being interacted with while carrying an item.
     */
    @Unique
    @Nullable
    private BlockPos currentTargetPos;


    /**
     * Runs after vanilla finishes picking up an item.
     */
    @Inject(method = "pickUpItems", at = @At("TAIL"))
    private void afterPickUpItems(PathfinderMob body, Container container, CallbackInfo ci) {
        setLastChestAsTarget(body);
        setLastItem(body);
    }

    /**
     * Runs after vanilla finishes placing the carried item into a container.
     */
    @Inject(method = "putDownItem", at = @At("TAIL"))
    private void afterPutDownItem(PathfinderMob body, Container container, CallbackInfo ci) {
        setLastChest(body);
    }

    /**
     * Runs when vanilla begins processing an interaction with a target.
     */
    @Inject(method = "onReachedTarget", at = @At("HEAD"))
    private void beforeOnReachedTarget(TransportItemTarget target, Level level, PathfinderMob body, CallbackInfo ci) {
        setCurrentTargetPos(target, body);
    }

    /**
     * Stores the position of the target for the current deposit operation.
     */
    @Unique
    private void setCurrentTargetPos(TransportItemTarget target, PathfinderMob body) {
        if (body.getMainHandItem().isEmpty()) {
            return;
        }
        this.currentTargetPos = target.pos();
    }

    /**
     * Records the item picked by golem and preserves the remembered chest when the same item is picked again.
     */
    @Unique
    private void setLastItem(PathfinderMob body) {
        if (body.getMainHandItem().isEmpty()) {
            this.lastPickedItem = null;
            this.lastChest = null;
            return;
        }

        Item newItem = body.getMainHandItem().getItem();
        if (this.lastPickedItem == newItem) {
            return;
        }

        this.lastPickedItem = newItem;
        this.lastChest = null;
    }

    /**
     * Remembers the current target after golem completely deposits the carried item. A partial or failed deposit is never remembered.
     */
    @Unique
    private void setLastChest(PathfinderMob body) {
        if (body.getMainHandItem().isEmpty()) {
            if (this.currentTargetPos != null) {
                this.lastChest = this.currentTargetPos;
            }
        } else {
            this.lastChest = null;
        }

        this.currentTargetPos = null;
    }

    /**
     * Creates new target from stored position after remembered item was picked up.
     */
    @Unique
    private void setLastChestAsTarget(PathfinderMob body) {
        if (!SmarterGolemsConfigManager.get().isPreferredChestDepositEnabled()) {
            return;
        }

        if (body.getMainHandItem().isEmpty() || this.lastChest == null || body.getMainHandItem().getItem() != this.lastPickedItem) {
            return;
        }

        TransportItemTarget newTarget = TransportItemTarget.tryCreatePossibleTarget(this.lastChest, body.level());
        ((TransportItemsBetweenContainersAccessor) this).smarterGolems$setTarget(newTarget);
    }


    /**
     * Searches for last picked item when golem is selecting items from copper chest.
     * Falls back to native search when last item was not found.
     */
    @Redirect(method = "pickUpItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers;pickupItemFromContainer(Lnet/minecraft/world/Container;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack pickupLastItemFromContainer(Container container) {
        if (!SmarterGolemsConfigManager.get().isPreferredItemPickupEnabled()) {
            return TransportItemsBetweenContainersAccessor.smarterGolems$pickupItemFromContainer(container);
        }

        if (this.lastPickedItem != null) {
            int slot = 0;
            for (ItemStack itemStack : container) {
                if (!itemStack.isEmpty() && itemStack.getItem() == this.lastPickedItem) {
                    int itemCount = Math.min(itemStack.getCount(), 16);
                    return container.removeItem(slot, itemCount);
                }
                slot++;
            }
        }

        return TransportItemsBetweenContainersAccessor.smarterGolems$pickupItemFromContainer(container);
    }
}