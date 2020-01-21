package com.example.rickandmorty.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.R
import com.example.rickandmorty.adapter.EpisodeAdapter
import com.example.rickandmorty.data.characters.CharacterLocation
import com.example.rickandmorty.data.characters.CharactersResults
import com.example.rickandmorty.data.favourites.FavouriteModel
import com.example.rickandmorty.data.network.EpisodeResponse
import com.example.rickandmorty.viewmodel.EpisodesViewModel
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.character_header.*
import kotlinx.android.synthetic.main.character_summary.*
import kotlinx.android.synthetic.main.fragment_characters_detail.*
import javax.inject.Inject

class CharacterDetailsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EpisodesViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(EpisodesViewModel::class.java)
    }

    private val disposables = CompositeDisposable()
    private val episodesAdapter = EpisodeAdapter()

    private lateinit var characters: CharactersResults
    private val episodes = ArrayList<EpisodeResponse>()

    private fun selector(episode: EpisodeResponse): Int = episode.id.toInt()

    private var shouldFetchEpisodes = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_characters_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favouriteButton.init(activity)
        arguments?.let {
            val safeArgs = CharacterDetailsFragmentArgs.fromBundle(it)
            characters = safeArgs.character
        }

        loadUI()
    }

    private fun loadUI() {
        updateToolbar()
        loadCharacterHeader()
        loadCharacterSummary()
        setEpisodesAdapter()
        loadCharacterEpisodes()
        setListeners()
    }

    private fun loadCharacterHeader() {
        loadCharacterImage()
        loadCharacterName()
    }

    private fun setListeners() {
        setFavouriteButtonListener()
    }

    private fun loadCharacterImage() {
        Picasso.get().load(characters.image).into(characterDetailImage)
        if (viewModel.isFavourite(characters.id)) {
            Log.d("favouriteh", "yes")
            favouriteButton.isChecked = true
        } else {
            Log.d("favouriteh", "no")
            favouriteButton.isChecked = false

        }
    }

    private fun setFavouriteButtonListener() {
        favouriteButton.setOnClickListener {
            addToFavourite()
        }
    }

    private fun addToFavourite() {
        viewModel.insertFavourite(
            FavouriteModel(characters.id,
                characters.name,
                characters.status,
                characters.species,
                characters.gender,
                characters.image,
                CharacterLocation(characters.location.name),
                characters.episode
            )
        )

        // Just checking if item has been added to favourite will be removed
        viewModel.getFavourite()
    }

    private fun loadCharacterName() {
        characterDetailName.text = characters.name
    }

    private fun loadCharacterSummary() {
        characterStatus.text = characters.status
        characterSpecies.text = characters.species
        characterGender.text = characters.gender
        characterLocation.text = characters.location.name
    }

    private fun setEpisodesAdapter() {
        episodesRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        episodesRecyclerView.adapter = episodesAdapter
    }

    private fun loadCharacterEpisodes() {
        // TODO: Put this in viewmodel ;)
        for (i in characters.episode) {
            val disposable = viewModel.fetchEpisodes("$i/")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        onRetrieveEpisodesSuccess(it)
                    },
                    {
                        onRetrieveEpisodesError()
                    }
                )
            disposables.add(disposable)
        }
    }

    private fun onRetrieveEpisodesSuccess(episode: EpisodeResponse) {
        // This should be handled in the viewmodel
        episode.characterId = characters.id
        episodes.add(
            EpisodeResponse(
                episode.episodeId,
                episode.characterId,
                episode.id,
                episode.name,
                episode.airdate,
                episode.episode
            )
        ).also {
            episodes.sortBy { selector(it)}
        }
        episodesAdapter.setEpisodes(episodes)
        viewModel.insertEpisodes(episode)
    }

    private fun onRetrieveEpisodesError() {
        if (shouldFetchEpisodes) {
            episodesAdapter.setEpisodes(viewModel.getEpisodes(characters.id))
        }
        shouldFetchEpisodes = false
    }

    private fun updateToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.toolbar_overview_title)
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

}
