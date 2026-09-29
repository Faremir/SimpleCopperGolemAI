package io.github.faremir.smartergolems;

import io.github.faremir.smartergolems.command.SmarterGolemsCommand;
import io.github.faremir.smartergolems.config.SmarterGolemsConfigManager;
import net.fabricmc.api.ModInitializer;

public class SmarterGolemsFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        SmarterGolemsConfigManager.initialize();
        SmarterGolemsCommand.register();
    }

}