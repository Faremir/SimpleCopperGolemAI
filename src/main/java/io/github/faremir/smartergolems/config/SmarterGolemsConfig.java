package io.github.faremir.smartergolems.config;

public class SmarterGolemsConfig {

    private boolean preferredItemPickupEnabled = true;
    private boolean preferredChestDepositEnabled = true;

    public boolean isPreferredItemPickupEnabled() {
        return this.preferredItemPickupEnabled;
    }

    public void setPreferredItemPickupEnabled(boolean enabled) {
        this.preferredItemPickupEnabled = enabled;
    }

    public boolean isPreferredChestDepositEnabled() {
        return this.preferredChestDepositEnabled;
    }

    public void setPreferredChestDepositEnabled(boolean enabled) {
        this.preferredChestDepositEnabled = enabled;
    }

}