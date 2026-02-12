package com.example.dine_and_tell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.databinding.FooterItemBinding

class FooterAdapter(private val retry: () -> Unit) : RecyclerView.Adapter<FooterAdapter.FooterViewHolder>() {

    private var state: FooterState = FooterState.Done

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val binding = FooterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FooterViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        holder.bind(state)
    }

    override fun getItemCount(): Int {
        return if (state == FooterState.Done) 0 else 1
    }

    fun setState(state: FooterState) {
        this.state = state
        notifyDataSetChanged()
    }

    class FooterViewHolder(private val binding: FooterItemBinding, private val retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(state: FooterState) {
            binding.footerProgressBar.visibility = if (state == FooterState.Loading) View.VISIBLE else View.GONE
            binding.footerRetryButton.visibility = if (state == FooterState.Error) View.VISIBLE else View.GONE
            binding.footerRetryButton.setOnClickListener { retry() }
        }
    }

    enum class FooterState {
        Loading,
        Error,
        Done
    }
}
