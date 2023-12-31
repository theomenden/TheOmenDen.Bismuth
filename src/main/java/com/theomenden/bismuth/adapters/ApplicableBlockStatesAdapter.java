package com.theomenden.bismuth.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.models.ApplicableBlockStates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ApplicableBlockStatesAdapter extends TypeAdapter<ApplicableBlockStates> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bismuth.class);
    private static final Set<String> MODIDS = Set.of(Bismuth.MODID, Bismuth.COLORMATIC_ID);

    @Override
    public void write(JsonWriter jsonWriter, ApplicableBlockStates applicableBlockStates) {
        throw new UnsupportedOperationException("writing is not supported");
    }

    @Override
    public ApplicableBlockStates read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            throw new JsonSyntaxException("Unexpected null value");
        }
        String s = jsonReader.nextString();
        return fromReadJson(s);
    }

    private static ApplicableBlockStates fromReadJson(String blockDescription) {
        ApplicableBlockStates applicableBlockStates = new ApplicableBlockStates();
        Block block;
        String[] parts = blockDescription.split(":");
        int beginningIndex;
        try {
            if(parts.length > 1
                    && parts[1].indexOf('=') < 0) {
                ResourceLocation id = new ResourceLocation(parts[0], parts[1]);

                if(MODIDS.contains(parts[0])) {
                    initializeSpecialBlockStates(applicableBlockStates, id, parts);
                    return applicableBlockStates;
                } else {
                    block = BuiltInRegistries.BLOCK.get(id);
                }
                beginningIndex = 2;
            } else {
                block =  BuiltInRegistries.BLOCK.get(new ResourceLocation(parts[0]));
                beginningIndex = 1;
            }
        } catch (Exception e) {
            throw new JsonSyntaxException("Invalid block identifier: " + blockDescription, e);
        }

        applicableBlockStates.block = block;
        BlockStatePredicate predicate = BlockStatePredicate.forBlock(block);

        for(int i = beginningIndex; i < parts.length; i++) {
            int splitIndex = parts[i].indexOf('=');
            if(splitIndex < 0) {
                throw new JsonSyntaxException("Invalid property syntax: " + parts[i]);
            }
            String propertyName = parts[i].substring(0, splitIndex);

            Property<?> propertyState = block
                    .defaultBlockState()
                    .getProperties()
                    .stream()
                    .filter(readableProperty -> readableProperty.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);

            if(propertyState == null) {
                throw new JsonSyntaxException("Invalid property: " + propertyName);
            }

            String[] propertyValues = parts[i].substring(splitIndex + 1).split(",");
            ObjectArrayList<Comparable<?>> container = new ObjectArrayList<>();

            for(String val : propertyValues) {
                putPropertyValue(container, propertyState, val);
            }

            predicate = predicate.where(propertyState, container::contains);
        }

        applicableBlockStates.states = new ObjectArrayList<>();
        boolean isExcluded = false;

        for(BlockState state: block.getStateDefinition()
                                   .getPossibleStates()) {
            if(predicate.test(state)) {
                applicableBlockStates.states.add(state);
            } else {
                isExcluded = true;
            }
        }

        if(!isExcluded) {
            applicableBlockStates.states.clear();
        }
        return applicableBlockStates;
    }

    private static void initializeSpecialBlockStates(ApplicableBlockStates states, ResourceLocation identifier, String[] parts) {
        states.specialKey = identifier;

        if (parts.length != 3) {
            LOGGER.warn("Special identifier does not specify a sole property: {}", Arrays.toString(parts));
        } else {
            for (int i = 2; i < parts.length; i++) {
                int split = parts[i].indexOf('=');
                if (split < 0) {
                    throw new JsonSyntaxException("Invalid property syntax: " + parts[i]);
                }
                String[] propertyValues = parts[i]
                        .substring(split + 1)
                        .split(",");

                for (String value : propertyValues) {
                    ResourceLocation location = ResourceLocation.tryParse(value.replaceFirst("/", ":"));

                    if (location == null) {
                        throw new JsonSyntaxException("Invalid identifier value: " + location);
                    }

                    states.specialLocations.add(location);
                }
            }
        }
    }

    private static <T extends Comparable<T>> void putPropertyValue(List<? super T> container, Property<T> propertyState, String propertyValue) {
        Optional<T> value = propertyState.getValue(propertyValue);

        value.ifPresentOrElse(container::add, () -> {
            throw new JsonSyntaxException("Invalid property value: " + propertyValue);});
    }
}