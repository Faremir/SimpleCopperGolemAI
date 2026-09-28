package io.github.faremir.smartergolems.client;

import io.github.faremir.smartergolems.config.SmarterGolemsConfig;
import io.github.faremir.smartergolems.config.SmarterGolemsConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class SmarterGolemsConfigScreen extends OptionsSubScreen {

    public SmarterGolemsConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("smarter-golems.settings.title"));
    }

    @Override
    protected void addOptions() {
        SmarterGolemsConfig config = SmarterGolemsConfigManager.get();

        if (this.list == null) {
            return;
        }

        this.list.addHeader(Component.translatable("smarter-golems.settings.memory.category").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
        );
        OptionInstance<Boolean> preferredItemPickup = OptionInstance.createBoolean(
                "smarter-golems.settings.memory.category.remember_picked_items.option",
                OptionInstance.cachedConstantTooltip(Component.translatable("smarter-golems.settings.memory.category.remember_picked_items.tooltip")),
                OptionInstance.BOOLEAN_TO_STRING,
                config.isPreferredItemPickupEnabled(),
                config::setPreferredItemPickupEnabled
        );
        OptionInstance<Boolean> preferredChestDeposit = OptionInstance.createBoolean(
                "smarter-golems.settings.memory.category.remember_destination_chests.option",
                OptionInstance.cachedConstantTooltip(Component.translatable("smarter-golems.settings.memory.category.remember_destination_chests.tooltip")),
                OptionInstance.BOOLEAN_TO_STRING,
                config.isPreferredChestDepositEnabled(),
                config::setPreferredChestDepositEnabled
        );
        this.list.addBig(preferredItemPickup);
        this.list.addBig(preferredChestDeposit);
    }

    @Override
    public void removed() {
        SmarterGolemsConfigManager.save();
    }

}