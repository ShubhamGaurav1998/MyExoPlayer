package com.example.videoplayer.views

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.videoplayer.R
import com.example.videoplayer.databinding.FragmentPlayScreenBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class PlayScreenFragment: Fragment(), Player.Listener {
    companion object {
        const val TAG = "PlayScreenFragment"
        lateinit var mContext: Context
        fun newInstance(context: Context): PlayScreenFragment {
            val args = Bundle()
            mContext = context
            val playScreenFragment = PlayScreenFragment()
            playScreenFragment.arguments = args
            return playScreenFragment
        }
    }
    var videoPlayer: SimpleExoPlayer? = null
    lateinit var fragmentPlayScreenBinding: FragmentPlayScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPlayScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_screen, container, false)

        (mContext as MainActivity?)!!.setOnBundleSelected(object : MainActivity.ExoplayerListener {

            override fun onURLChange(url: String?, title: String?) {
                if (url != null) {
                    playVideoFromUrl(url)
                }
                fragmentPlayScreenBinding.tvTitle.text = title
            }
        })
        return fragmentPlayScreenBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
    }

    private fun buildMediaSource(sampleUrl: String): MediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(mContext, "sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(sampleUrl)))
    }

    private fun initializePlayer() {
        videoPlayer = SimpleExoPlayer.Builder(mContext).build()
        fragmentPlayScreenBinding.videoPlayer.player = videoPlayer
        fragmentPlayScreenBinding.videoPlayer.setShowNextButton(false);
        fragmentPlayScreenBinding.videoPlayer.setShowPreviousButton(false);
        videoPlayer?.addListener(this)
    }

     fun playVideoFromUrl(sampleUrl: String) {
        buildMediaSource(sampleUrl)?.let {
            videoPlayer?.prepare(it)
        }
    }

    override fun onStop() {
        super.onStop()
        videoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer?.playWhenReady = false
            releasePlayer()
    }

    private fun releasePlayer() {
        videoPlayer?.release()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when(playbackState){
            Player.STATE_BUFFERING -> {
                    fragmentPlayScreenBinding.progressBar.visibility = View.VISIBLE
        }
            Player.STATE_READY -> {
                fragmentPlayScreenBinding.progressBar.visibility = View.GONE
            }
            Player.STATE_IDLE -> {
                fragmentPlayScreenBinding.progressBar.visibility = View.GONE
            }
        }
    }


}