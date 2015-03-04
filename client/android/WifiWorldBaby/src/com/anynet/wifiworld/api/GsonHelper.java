package com.anynet.wifiworld.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper
{
  private static Gson gson;

  public static Gson getGsonInstance()
  {
    if (gson == null)
    {
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().setDateFormat("yyyy-MM-dd hh:mm").serializeNulls().create();
    }
    return gson;
  }
}

