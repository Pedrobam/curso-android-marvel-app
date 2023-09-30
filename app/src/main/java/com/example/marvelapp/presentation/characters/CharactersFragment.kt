package com.example.marvelapp.presentation.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.core.domain.model.Character
import com.example.marvelapp.databinding.FragmentCharactersBinding
import com.example.marvelapp.framework.imageloader.GlideImageLoader
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.presentation.detail.DetailViewArg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CharactersFragment : Fragment() {

    private lateinit var binding: FragmentCharactersBinding
    private lateinit var charactersAdapter: CharactersAdapter
    private val mViewModel: CharactersViewModel by viewModels()
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCharactersAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.charactersPagingData("").collect { pagingData ->
                    charactersAdapter.submitData(pagingData)
                }
            }
        }
        observeInitialLoadState()
    }

    private fun initCharactersAdapter() {
        charactersAdapter =
            CharactersAdapter(imageLoader) { character: Character, view: View ->
                val extras = FragmentNavigatorExtras(
                    view to character.name
                )

                val directions = CharactersFragmentDirections
                    .actionCharactersFragmentToDetailFragment(
                        character.name,
                        DetailViewArg(character.name, character.imageUrl)
                    )

                findNavController().navigate(directions, extras)
            }
        with(binding.recyclerCharacters) {
            scrollToPosition(0)
            setHasFixedSize(true)
            adapter = charactersAdapter.withLoadStateFooter(
                footer = CharactersLoadStateAdapter {
                    charactersAdapter.retry()
                }
            )
        }
    }

    private fun observeInitialLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            charactersAdapter.loadStateFlow.collectLatest { loadState ->
                binding.flipperCharacters.displayedChild = when (loadState.refresh) {
                    is LoadState.Loading -> {
                        setShimmerVisibility(true)
                        FLIPPER_CHILD_LOADING
                    }

                    is LoadState.NotLoading -> {
                        setShimmerVisibility(false)
                        FLIPPER_CHILD_CHARACTERS
                    }

                    is LoadState.Error -> {
                        setShimmerVisibility(false)
                        binding.includeViewCharactersErrorState.buttonRetry.setOnClickListener {
                            charactersAdapter.refresh()
                        }
                        FLIPPER_CHILD_ERROR
                    }
                }
            }
        }
    }

    private fun setShimmerVisibility(visibility: Boolean) {
        binding.includeViewCharactersLoadingState.shimmerCharacters.run {
            isVisible = visibility
            if (visibility) {
                startShimmer()
            } else {
                stopShimmer()
            }
        }
    }

    companion object {
        private const val FLIPPER_CHILD_LOADING = 0
        private const val FLIPPER_CHILD_CHARACTERS = 1
        private const val FLIPPER_CHILD_ERROR = 2
    }

}