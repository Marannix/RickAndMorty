package com.example.rickandmorty.data.characters

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "characters")
data class CharactersResults (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    @Embedded(prefix = "location_")
    val location: CharacterLocation,
    val episode: List<String>
) : Parcelable