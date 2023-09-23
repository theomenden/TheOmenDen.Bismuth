package com.theomenden.bismuth.client;

import com.theomenden.bismuth.models.enums.BiomeBlendNames;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Bismuth.MODID)
public class BismuthConfig implements ConfigData {
    @ConfigEntry.Category("customColors")
    @ConfigEntry.Gui.Tooltip
    public boolean shouldClearSky = false;
    @ConfigEntry.Category("customColors")
    @ConfigEntry.Gui.Tooltip
    public boolean shouldClearVoid = false;
    @ConfigEntry.Category("customColors")
    @ConfigEntry.Gui.Tooltip
    public boolean shouldBlendSkyLight = true;
    @ConfigEntry.Category("customColors")
    @ConfigEntry.Gui.Tooltip
    public boolean shouldFlickerBlockLight = true;

    @ConfigEntry.Category("customColors")
    @ConfigEntry.Gui.Tooltip
    public double relativeBlockLightIntensity = -13.0;

    @ConfigEntry.Category("biomeBlendRadius")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public BiomeBlendNames blendingRadius = BiomeBlendNames.BROBDINGNAGIAN;

    public static double calculateScale(double relativeBlockLightIntensity) {
        final double QUARTER_LOG_2 = 0.25 * Math.log(2);
        return QUARTER_LOG_2 * relativeBlockLightIntensity;
    }
}
