package com.example.dine_and_tell.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.R
import com.example.dine_and_tell.databinding.ItemRestaurantBinding
import com.example.dine_and_tell.model.Place
import com.squareup.picasso.Picasso

class RestaurantsAdapter(
    private var places: List<Place>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    override fun getItemCount() = places.size

    fun updateData(newPlaces: List<Place>) {
        places = newPlaces
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(places[position])
                }
            }
        }

        fun bind(place: Place) {
            binding.restaurantName.text = place.name
            binding.restaurantAddress.text = place.address

            if (place.imageUrl != null) {
                Picasso.get()
                    .load(place.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.restaurantImage)
            } else {
                val imageResource = when {
                    place.categories?.contains("catering.restaurant.pizza") == true -> R.drawable.pizza
                    place.categories?.contains("catering.restaurant.burger") == true -> R.drawable.burger
                    place.categories?.contains("catering.restaurant.sushi") == true -> R.drawable.sushi
                    place.categories?.contains("catering.cafe") == true -> R.drawable.cafe
                    else -> R.drawable.default_restaurant
                }
                binding.restaurantImage.setImageResource(imageResource)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(place: Place)
    }
}
