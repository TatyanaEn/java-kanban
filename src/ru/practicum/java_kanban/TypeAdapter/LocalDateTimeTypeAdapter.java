package ru.practicum.java_kanban.TypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
    }
}