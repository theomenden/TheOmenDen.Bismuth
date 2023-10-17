package com.theomenden.bismuth.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.theomenden.bismuth.adapters.*;
import com.theomenden.bismuth.colors.properties.ColorMappingProperties;
import com.theomenden.bismuth.models.ApplicableBlockStates;
import com.theomenden.bismuth.models.GridEntry;
import com.theomenden.bismuth.models.enums.Format;
import com.theomenden.bismuth.models.exceptions.InvalidColorMappingException;
import com.theomenden.bismuth.models.records.BismuthColor;
import com.theomenden.bismuth.models.records.PropertyImage;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.material.MaterialColor;

import java.io.*;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

public final class GsonUtils {
    private GsonUtils(){}
    public static final Gson PROPERTY_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new StringIdentifiableTypeAdapterFactory())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
            .registerTypeAdapter(ApplicableBlockStates.class, new ApplicableBlockStatesAdapter())
            .registerTypeAdapter(BismuthColor.class, new BismuthColorAdapter())
            .registerTypeAdapter(MaterialColor.class, new MaterialColorAdapter())
            .registerTypeAdapter(ChatFormatting.class, new ChatFormattingAdapter())
            .registerTypeAdapter(GridEntry.class, new GridEntryAdapter())
            .create();

    public static String resolveRelativeResourceLocation(String path, ResourceLocation identifier) {
        if(path.startsWith("./")) {
            String thisPath = identifier.toString();
            path = thisPath.substring(0, thisPath.lastIndexOf('/')) + path.substring(1);
        } else if(path.startsWith("~/")) {
            path = "optifine" + path.substring(1);
        } else if(!path.contains("/") && !path.contains(":")) {
            String thisPath = identifier.toString();
            path = thisPath.substring(0, thisPath.lastIndexOf('/') + 1) + path;
        }
        return path;
    }

    public static Reader getJsonReader(InputStream inputStream,
                                       ResourceLocation identifier,
                                       Function<String, String> keyMapper,
                                       Predicate<String> arrayValues) throws IOException {
        if (!identifier
                .getPath()
                .endsWith(".properties")) {
            return new InputStreamReader(inputStream);
        }

        Properties properties = new Properties();
        properties.load(inputStream);
        return new StringReader(GsonUtils.toJsonString(properties, keyMapper, arrayValues));

    }

    public static PropertyImage loadColorMapping(ResourceManager resourceManager, ResourceLocation identifier, boolean isCustom) {
        ColorMappingProperties properties = ColorMappingProperties.load(resourceManager, identifier,isCustom);

        if(properties.getFormat() == Format.FIXED) {
            return new PropertyImage(properties, null);
        }

        try(InputStream inputStream = resourceManager.getResourceOrThrow(properties.getSource()).open()) {
            NativeImage image = NativeImage.read(inputStream);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                for(int i = 0; i < image.getWidth(); i++) {
                    for(int j = 0; j < image.getHeight(); j++) {
                        int pixel = image.getPixelRGBA(i, j);
                        pixel = flipPixelColor(pixel);
                        image.setPixelRGBA(i, j, pixel);
                    }
                }
            }

            if(properties.getFormat() == Format.VANILLA
                    && (image.getWidth() != 256 || image.getHeight() != 256)) {
                throw new InvalidColorMappingException("Vanilla Colormap dimensions must be 256x256");
            }
            return new PropertyImage(properties, image);
        } catch(IOException e) {
            throw new InvalidColorMappingException(e);
        }
    }

    private static int flipPixelColor(int pixel) {
        int tmp = (pixel & 0xff0000) >> 16;
        tmp |= (pixel & 0x0000ff) << 16;
        pixel &= ~(0xff0000 | 0x0000ff);
        pixel |= tmp;
        return pixel;
    }

    @SuppressWarnings("unchecked")
    private static String toJsonString(Properties properties, Function<String, String> keyMappingFunction, Predicate<String> arrayValue) {
        Map<String, Object> propertyMap = Maps.newHashMap();
        for (String property: properties.stringPropertyNames()) {
            String[] keys = property.split("\\.");
            Map<String, Object> nestedProperties = propertyMap;
            int i;
            int length = keys.length - 1;
            for(i = 0; i < length; i++) {
                String key = keyMappingFunction.apply(keys[i]);
                Object temp = nestedProperties.computeIfAbsent(key, k -> new HashMap<>());

                if(temp instanceof Map<?,?>) {
                    nestedProperties = (Map<String,Object>)temp;
                } else {
                    Map<String, Object> newNestedMap = new HashMap<>();
                    newNestedMap.put("", temp);
                    nestedProperties.put(key, newNestedMap);
                    nestedProperties = newNestedMap;
                }
            }
            String key = keyMappingFunction.apply(keys[i]);
            String propertyValue = properties.getProperty(property);
            Object value = arrayValue.test(key) ? propertyValue.split("\\s+") : propertyValue;
            nestedProperties.merge(key, value, GsonUtils::mergeCompoundKeys);
        }

        return PROPERTY_GSON.toJson(propertyMap);
    }


    private static Object mergeCompoundKeys(Object existingValue, Object newValue) {
        if(existingValue  instanceof Map<?,?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> existingMap = (Map<String, Object>) existingValue;
            existingMap.put("", newValue);
            return existingMap;
        }

        return newValue;
    }
}
