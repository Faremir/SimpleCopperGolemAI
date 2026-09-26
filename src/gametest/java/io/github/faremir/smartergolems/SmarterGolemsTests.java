package io.github.faremir.smartergolems;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SmarterGolemsTests {

    private static final int TEST_TIMEOUT = 300;

    /**
     * Creates a copper chest containing the given item.
     */
    private static void setCopperChest(GameTestHelper context, BlockPos pos, Item item) {
        context.setBlock(pos, Blocks.COPPER_CHEST.weathering().unaffected());
        ChestBlockEntity chest = context.getBlockEntity(pos, ChestBlockEntity.class);
        chest.setItem(0, new ItemStack(item, 10));
    }


    /**
     * Initializes test values.
     */
    private static TestSetup initSetup(GameTestHelper context) {
        TestSetup setup = new TestSetup(
                new BlockPos(1, 1, 1),
                new BlockPos(2, 1, 1),
                new BlockPos(3, 1, 1)
        );

        setCopperChest(context, setup.copperChestPos, Items.STONE);
        context.setBlock(setup.chestPos, Blocks.CHEST);

        CopperGolem golem = new CopperGolem(EntityTypes.COPPER_GOLEM, context.getLevel());
        BlockPos absolutePos = context.absolutePos(setup.golemPos);
        golem.setPos(absolutePos.getX() + 0.5, absolutePos.getY(), absolutePos.getZ() + 0.5);
        context.getLevel().addFreshEntity(golem);

        return setup;
    }

    @GameTest(maxTicks = TEST_TIMEOUT)
    public void testCopperGolemDepositsItem(GameTestHelper context) {
        TestSetup setup = initSetup(context);

        context.succeedWhen(() -> {
            ChestBlockEntity chest = context.getBlockEntity(setup.chestPos, ChestBlockEntity.class);

            context.assertTrue(chest.hasAnyMatching(item -> item.is(Items.STONE)), "The Copper Golem did not deposit the item.");
        });
    }

    @GameTest(maxTicks = TEST_TIMEOUT)
    public void testCopperGolemPrefersLastPickedItem(GameTestHelper context) {
        TestSetup setup = initSetup(context);

        AtomicBoolean firstItemDeposited = new AtomicBoolean(false);
        AtomicInteger firstItemCount = new AtomicInteger();

        context.succeedWhen(() -> {
            ChestBlockEntity chest = context.getBlockEntity(setup.chestPos, ChestBlockEntity.class);

            if (!firstItemDeposited.get() && chest.hasAnyMatching(item -> item.is(Items.STONE))) {
                firstItemDeposited.set(true);
                firstItemCount.set(chest.countItem(Items.STONE));

                setCopperChest(context, setup.copperChestPos, Items.DIRT);

                ChestBlockEntity copperChest = context.getBlockEntity(setup.copperChestPos, ChestBlockEntity.class);
                copperChest.setItem(1, new ItemStack(Items.STONE, 1));
            }

            if (!firstItemDeposited.get()) return;


            context.assertTrue(chest.countItem(Items.STONE) > firstItemCount.get(), "The Copper Golem did not prefer the item it picked previously.");
            ChestBlockEntity copperChest = context.getBlockEntity(setup.copperChestPos, ChestBlockEntity.class);
            context.assertTrue(copperChest.hasAnyMatching(item -> item.is(Items.DIRT)), "The Copper Golem picked a different item instead of the previously picked item.");
        });
    }

    @GameTest(maxTicks = TEST_TIMEOUT)
    public void testCopperGolemRemembersLastChest(GameTestHelper context) {
        TestSetup setup = initSetup(context);
        BlockPos otherChestPos = new BlockPos(3, 1, 5);

        AtomicBoolean firstItemDeposited = new AtomicBoolean(false);
        AtomicInteger firstItemCount = new AtomicInteger();

        context.succeedWhen(() -> {
            ChestBlockEntity chest = context.getBlockEntity(setup.chestPos, ChestBlockEntity.class);

            if (!firstItemDeposited.get() && chest.hasAnyMatching(item -> item.is(Items.STONE))) {
                firstItemDeposited.set(true);
                firstItemCount.set(chest.countItem(Items.STONE));

                setCopperChest(context, setup.copperChestPos, Items.STONE);
                context.setBlock(otherChestPos, Blocks.CHEST);
            }

            if (!firstItemDeposited.get()) return;


            context.assertTrue(chest.countItem(Items.STONE) > firstItemCount.get(), "The Copper Golem did not remember the chest where it last deposited STONE.");
            ChestBlockEntity otherChest = context.getBlockEntity(otherChestPos, ChestBlockEntity.class);
            context.assertTrue(otherChest.isEmpty(), "The Copper Golem did not use its remembered chest.");
        });
    }

    @GameTest(maxTicks = TEST_TIMEOUT)
    public void testCopperGolemUsesNormalTargetWhenItemChanges(GameTestHelper context) {
        TestSetup setup = initSetup(context);
        BlockPos secondChestPos = new BlockPos(10, 1, 10);

        context.setBlock(secondChestPos, Blocks.CHEST);

        AtomicBoolean firstItemDeposited = new AtomicBoolean(false);

        context.succeedWhen(() -> {
            ChestBlockEntity chest = context.getBlockEntity(setup.chestPos, ChestBlockEntity.class);

            if (!firstItemDeposited.get() && chest.hasAnyMatching(item -> item.is(Items.STONE))) {
                firstItemDeposited.set(true);
                setCopperChest(context, setup.copperChestPos, Items.DIRT);
                chest.setItem(0, new ItemStack(Items.GRAVEL, 1));
            }

            if (!firstItemDeposited.get()) return;


            ChestBlockEntity secondChest = context.getBlockEntity(secondChestPos, ChestBlockEntity.class);

            context.assertTrue(secondChest.hasAnyMatching(item -> item.is(Items.DIRT)), "The Copper Golem did not use normal target selection after the item changed.");
        });
    }

    @GameTest(maxTicks = TEST_TIMEOUT)
    public void testCopperGolemUsesNormalTargetWhenLastChestIsInvalid(GameTestHelper context) {
        TestSetup setup = initSetup(context);
        BlockPos fallbackChestPos = new BlockPos(3, 1, 5);

        AtomicBoolean firstItemDeposited = new AtomicBoolean(false);

        context.succeedWhen(() -> {
            if (!firstItemDeposited.get()) {
                ChestBlockEntity chest = context.getBlockEntity(setup.chestPos, ChestBlockEntity.class);

                if (chest.hasAnyMatching(item -> item.is(Items.STONE))) {
                    firstItemDeposited.set(true);
                    context.setBlock(setup.chestPos, Blocks.STONE);
                    setCopperChest(context, setup.copperChestPos, Items.STONE);
                    context.setBlock(fallbackChestPos, Blocks.CHEST);
                }
            }

            if (!firstItemDeposited.get()) return;


            ChestBlockEntity fallbackChest = context.getBlockEntity(fallbackChestPos, ChestBlockEntity.class);

            context.assertTrue(fallbackChest.hasAnyMatching(item -> item.is(Items.STONE)), "The Copper Golem did not fall back to normal target selection.");
        });
    }

    /**
     * Common Test values.
     */
    private record TestSetup(BlockPos golemPos, BlockPos copperChestPos, BlockPos chestPos) {
    }

}