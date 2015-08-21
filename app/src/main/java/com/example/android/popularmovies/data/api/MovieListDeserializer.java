package com.example.android.popularmovies.data.api;

import com.example.android.popularmovies.data.model.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;


class MovieListDeserializer implements JsonDeserializer<List<Movie>>
{
    @Override
    public List<Movie> deserialize(JsonElement json, Type listType, JsonDeserializationContext context) throws JsonParseException {

        //JsonArray content = json.getAsJsonObject().get("results").getAsJsonArray();


        return new Gson().fromJson(json.getAsJsonObject().get("results").getAsJsonArray(), listType);

    }

}
