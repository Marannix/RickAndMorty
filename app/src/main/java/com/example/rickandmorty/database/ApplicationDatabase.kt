package com.example.rickandmorty.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickandmorty.data.Converters
import com.example.rickandmorty.data.characters.CharacterEpisodeResponse
import com.example.rickandmorty.data.characters.CharactersDao
import com.example.rickandmorty.data.characters.CharactersResults
import com.example.rickandmorty.data.episodes.EpisodesDao
import com.example.rickandmorty.data.episodes.EpisodesResult
import com.example.rickandmorty.data.favourites.FavouriteDao
import com.example.rickandmorty.data.favourites.FavouriteModel
import com.example.rickandmorty.data.settings.AccountSettings
import com.example.rickandmorty.data.settings.SettingsDao

@Database(
    entities = [CharactersResults::class, CharacterEpisodeResponse::class, EpisodesResult::class, FavouriteModel::class,
    AccountSettings::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun charactersDao(): CharactersDao
    abstract fun episodeDao(): EpisodesDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun settingsDao(): SettingsDao
}