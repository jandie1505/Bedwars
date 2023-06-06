package net.jandie1505.bedwars.game.menu;

public class ArmorConfig {
    private final boolean enableArmorSystem;
    private final boolean copyHelmet;
    private final boolean copyChestplate;
    private final boolean copyLeggings;
    private final int defaultHelmet;
    private final int defaultChestplate;
    private final int defaultLeggings;
    private final int defaultBoots;

    public ArmorConfig(boolean enableArmorSystem, boolean copyHelmet, boolean copyChestplate, boolean copyLeggings, int defaultHelmet, int defaultChestplate, int defaultLeggings, int defaultBoots) {
        this.enableArmorSystem = enableArmorSystem;
        this.copyHelmet = copyHelmet;
        this.copyChestplate = copyChestplate;
        this.copyLeggings = copyLeggings;
        this.defaultHelmet = defaultHelmet;
        this.defaultChestplate = defaultChestplate;
        this.defaultLeggings = defaultLeggings;
        this.defaultBoots = defaultBoots;
    }

    public boolean isEnableArmorSystem() {
        return enableArmorSystem;
    }

    public boolean isCopyHelmet() {
        return copyHelmet;
    }

    public boolean isCopyChestplate() {
        return copyChestplate;
    }

    public boolean isCopyLeggings() {
        return copyLeggings;
    }

    public int getDefaultHelmet() {
        return defaultHelmet;
    }

    public int getDefaultChestplate() {
        return defaultChestplate;
    }

    public int getDefaultLeggings() {
        return defaultLeggings;
    }

    public int getDefaultBoots() {
        return defaultBoots;
    }
}
