package com.example.marvelapp.presentation.characters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.domain.model.Character
import com.example.marvelapp.R
import com.example.marvelapp.databinding.ItemCharacterBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.util.onCharacterItemClick

class CharactersViewHolder(
    private val binding: ItemCharacterBinding,
    private val imageLoader: ImageLoader,
    private val onItemClick: onCharacterItemClick
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(character: Character) = binding.run {
        textName.text = character.name
        imageCharacter.transitionName = character.name
        imageLoader.load(imageCharacter, character.imageUrl, R.drawable.ic_img_loading_error)
        itemView.setOnClickListener {
            onItemClick.invoke(character, imageCharacter)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            imageLoader: ImageLoader,
            onItemClick: onCharacterItemClick
        ): CharactersViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemBinding = ItemCharacterBinding.inflate(inflater, parent, false)
            return CharactersViewHolder(itemBinding, imageLoader, onItemClick)
        }
    }
}