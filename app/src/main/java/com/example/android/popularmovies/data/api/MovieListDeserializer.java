/*
 * Copyright 2015 Nicolas Pintos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        return new Gson().fromJson(
                json.getAsJsonObject()
                        .get("results")
                        .getAsJsonArray(), listType);
    }

}
