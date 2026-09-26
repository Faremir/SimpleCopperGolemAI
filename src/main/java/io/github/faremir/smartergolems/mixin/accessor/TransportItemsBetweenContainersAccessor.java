package io.github.faremir.smartergolems.mixin.accessor;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(TransportItemsBetweenContainers.class)
public interface TransportItemsBetweenContainersAccessor {

    @Invoker("isTargetValidToPick")
    @Nullable
    TransportItemsBetweenContainers.TransportItemTarget smarterGolems$isTargetValidToPick(PathfinderMob body, Level level, BlockEntity blockEntity, Set<GlobalPos> visitedPositions, Set<GlobalPos> unreachablePositions, AABB targetBlockSearchArea);

    @Invoker("getTargetSearchArea")
    AABB smarterGolems$getTargetSearchArea(PathfinderMob body);

    @Invoker("getVisitedPositions")
    static Set<GlobalPos> smarterGolems$getVisitedPositions(PathfinderMob body) {
        throw new AssertionError("Untransformed @Invoker");
    }

    @Invoker("getUnreachablePositions")
    static Set<GlobalPos> smarterGolems$getUnreachablePositions(PathfinderMob body) {
        throw new AssertionError("Untransformed @Invoker");
    }

    @Invoker("pickupItemFromContainer")
    static ItemStack smarterGolems$pickupItemFromContainer(Container container) {
        throw new AssertionError("Untransformed @Invoker");
    }
}