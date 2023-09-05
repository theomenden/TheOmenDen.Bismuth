package com.theomenden.bismuth.adapters;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.theomenden.bismuth.models.records.BismuthColor;
import com.theomenden.bismuth.utils.ColorConverter;

import java.io.IOException;

public class BismuthColorAdapter extends TypeAdapter<BismuthColor> {
    @Override
    public void write(JsonWriter jsonWriter, BismuthColor vanadiumColor) throws IOException {
        if(vanadiumColor == null) {
            jsonWriter.nullValue();
        } else {
            String hexValue = ColorConverter.rgbToHex(vanadiumColor.rgb());
            jsonWriter.value(hexValue);
        }
    }

    @Override
    public BismuthColor read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonParseException(new NullPointerException("Null value"));
        }

        String readColor = jsonReader.nextString().trim();
        try {
            readColor = readColor.replace("#","").trim();
            return new BismuthColor(readColor);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }
}
