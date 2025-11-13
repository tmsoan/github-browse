package com.anos.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.anos.model.OwnerInfo
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter
class RepoTypeConverter @Inject constructor(
  private val json: Json,
) {
  @TypeConverter
  fun fromString(value: String): OwnerInfo? {
    return json.decodeFromString(value)
  }

  @TypeConverter
  fun fromInfoType(type: OwnerInfo?): String {
    return json.encodeToString(type)
  }

  @TypeConverter
  fun fromStringList(value: String): List<String> {
    return json.decodeFromString(value)
  }

  @TypeConverter
  fun fromListToString(list: List<String>): String {
    return json.encodeToString(list)
  }
}
