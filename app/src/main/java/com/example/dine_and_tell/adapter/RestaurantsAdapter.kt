package com.example.dine_and_tell.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.R
import com.example.dine_and_tell.databinding.ItemRestaurantBinding
import com.example.dine_and_tell.model.Place
import com.squareup.picasso.Picasso

class RestaurantsAdapter(
    private var places: MutableList<Place>,
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

    fun setData(newPlaces: List<Place>) {
        places.clear()
        places.addAll(newPlaces)
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
                    place.cuisine?.contains("pizza", ignoreCase = true) == true -> R.drawable.pizza
                    place.cuisine?.contains("italian", ignoreCase = true) == true -> R.drawable.pizza
                    place.categories?.contains("catering.restaurant.burger") == true -> R.drawable.burger
                    place.cuisine?.contains("burger", ignoreCase = true) == true -> R.drawable.burger
                    place.cuisine?.contains("meat", ignoreCase = true) == true -> R.drawable.burger
                    place.cuisine?.contains("steak_house", ignoreCase = true) == true -> R.drawable.burger
                    place.cuisine?.contains("grill", ignoreCase = true) == true -> R.drawable.burger
                    place.categories?.contains("catering.restaurant.sushi") == true -> R.drawable.sushi
                    place.categories?.contains("catering.restaurant.thai") == true -> R.drawable.sushi
                    place.cuisine?.contains("sushi", ignoreCase = true) == true -> R.drawable.sushi
                    place.cuisine?.contains("asian", ignoreCase = true) == true -> R.drawable.sushi
                    place.cuisine?.contains("thai", ignoreCase = true) == true -> R.drawable.sushi
                    place.cuisine?.contains("japanese", ignoreCase = true) == true -> R.drawable.sushi
                    place.categories?.contains("catering.cafe") == true -> R.drawable.cafe
                    place.cuisine?.contains("cafe", ignoreCase = true) == true -> R.drawable.cafe
                    place.cuisine?.contains("breakfast", ignoreCase = true) == true -> R.drawable.cafe
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
