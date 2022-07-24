package com.example.videoplayer.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.R
import com.example.videoplayer.databinding.VideoListItemBinding
import com.example.videoplayer.room.VideoListApiEntity

class VideoRecyclerViewAdapter(
    private val clickListener: (View, Int, Int, VideoListApiEntity) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {

    private var videoItemList = ArrayList<VideoListApiEntity>()
    var lastPlayedposition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItemBinding = VideoListItemBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(listItemBinding)

    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val videoItem = videoItemList[position]
        holder.bind(videoItem, clickListener)

        holder.binding.root.setOnClickListener {
            val timesPlayed = videoItem.timesPlayed + 1
            clickListener(it, videoItem.id, timesPlayed, videoItem)
            videoItem.timesPlayed += 1

            if (lastPlayedposition == -1) {
                videoItem.isPlaying = true
                lastPlayedposition = position
            } else {
                videoItemList.get(lastPlayedposition).isPlaying = false
                videoItem.isPlaying = true
                notifyItemChanged(lastPlayedposition)
                lastPlayedposition = position
            }
            notifyItemChanged(position)
        }
        setPlaying(videoItem.isPlaying, holder.binding.ivPlayPause)
        holder.binding.tvNoOfTimesPlayed.text =
            if (videoItem.timesPlayed == 0) "Not yet played" else videoItem.timesPlayed.toString()

    }

    override fun getItemCount(): Int {
        return videoItemList.size
    }

    fun setList(videoList: List<VideoListApiEntity>) {
        videoItemList.clear()
//        if (videoList.size > 0) {
//            videoList.get(0).isPlaying = true
//            videoList.get(0).timesPlayed += 1
//            lastPlayedposition = 0
//        }
        videoItemList.addAll(videoList)
        this.notifyDataSetChanged()
    }

    fun setPlaying(isPlaying: Boolean, imageView: ImageView) {
        if (isPlaying) {
            imageView.setImageResource(R.drawable.ic_baseline_pause_circle_24)
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        }
    }
}

class MyViewHolder(val binding: VideoListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        videoItem: VideoListApiEntity,
        clickListener: (View, Int, Int, VideoListApiEntity) -> Unit
    ) {
        binding.listItem = videoItem
        binding.ivMoreOptions.setOnClickListener {
            clickListener(it, videoItem.id, videoItem.timesPlayed, videoItem)
        }
    }
}