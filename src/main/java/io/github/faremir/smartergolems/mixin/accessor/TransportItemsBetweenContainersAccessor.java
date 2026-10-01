package io.github.faremir.smartergolems.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TransportItemsBetweenContainers.class)
public interface TransportItemsBetweenContainersAccessor {

    @Accessor("target")
    void smarterGolems$setTarget(@Nullable TransportItemsBetweenContainers.TransportItemTarget target);


    @Invoker("pickupItemFromContainer")
    static ItemStack smarterGolems$pickupItemFromContainer(Container container) {
        throw new AssertionError("Untransformed @Invoker");
    }


}