package com.example.videoplayer.views

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoplayer.MyApplication
import com.example.videoplayer.R
import com.example.videoplayer.adapter.VideoRecyclerViewAdapter
import com.example.videoplayer.databinding.ActivityMainBinding
import com.example.videoplayer.room.VideoListApiEntity
import com.example.videoplayer.utils.CommonUtils
import com.example.videoplayer.utils.Constants
import com.example.videoplayer.viewModel.MainViewModel
import com.example.videoplayer.viewModel.MainViewModelFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

//Sample comment

class MainActivity : AppCompatActivity(), Player.Listener {
    private var exoVideoPlayer: SimpleExoPlayer? = null
    private lateinit var adapter: VideoRecyclerViewAdapter
    private var videoItemList = arrayListOf<VideoListApiEntity>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private var playScreenFragment = PlayScreenFragment.newInstance(this)
    private lateinit var exoplayerListener: ExoplayerListener
    private var isMinimized: Boolean? = null
    private var currentVideoURL = ""
    private var currentVideotitle = ""
    private var seekTime: Long? = null
    private var mediaItem: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        isMinimized = false
        (application as MyApplication).applicationComponent.inject(this)
        mainViewModel = ViewModelProvider(
            this,
            mainViewModelFactory
        ).get(MainViewModel::class.java)
        if (CommonUtils.getBooleanSharedPreference(this, Constants.FIRST_APP_LAUNCH, true)) {
            CommonUtils.setBooleanSharedPreference(this, Constants.FIRST_APP_LAUNCH, false)
            if(CommonUtils.isInternetAvailable(this)) {
                lifecycleScope.launch {
                    mainViewModel.fetchVideoListFromUrl()
                }
            }
        }
        initializeRecyclerView()
        initializePlayer()
        setupPlayScreenFragment()

        binding.playScreenFrameLayout.setOnClickListener {
            maximizeExoPlayer()
        }
    }

    private fun setupPlayScreenFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.play_screen_frame_layout, playScreenFragment, PlayScreenFragment.TAG)
            .commitAllowingStateLoss()
    }

    private fun displayVideosList() {

        mainViewModel.getVideosFromDB()
        mainViewModel.videos.observe(this, Observer {
            if (it != null) {
                it.iterator().forEach { videoListItem ->
                    videoItemList.add(videoListItem)
                }
                adapter.setList(it)
                if (it.size > 0) {
                    currentVideoURL = it.get(0).rating
                    currentVideotitle = it.get(0).fullName
                    it.get(0).isPlaying = true
                    it.get(0).timesPlayed += 1
                    adapter.lastPlayedposition = 0
                    mainViewModel.updateVideoInfoById(it.get(0).timesPlayed, System.currentTimeMillis(), it.get(0).id)
                    playScreenFragment.fragmentPlayScreenBinding.tvTitle.text = currentVideotitle
                    if(isMinimized == true) {
                        exoplayerListener.onURLChange(currentVideoURL, currentVideotitle)
                    }
                    else {
                        playVideoFromUrl(currentVideoURL)
                    }
                } else {
                        maximizeExoPlayer()
                        currentVideoURL = ""
                        currentVideotitle = this.getString(R.string.no_vids_found)
                        playScreenFragment.videoPlayer?.clearMediaItems()
                        playScreenFragment.videoPlayer?.seekToDefaultPosition()
                        playScreenFragment.fragmentPlayScreenBinding.progressBar.visibility = View.GONE
                        exoVideoPlayer?.clearMediaItems()
                        exoVideoPlayer?.seekToDefaultPosition()
                        binding.progressBar.visibility = View.GONE
                        exoVideoPlayer?.stop()
                        playScreenFragment.videoPlayer?.stop()
                }
            }
        })
    }

    private fun initializeRecyclerView() {

        val recyclerView = binding.rcvVideoList
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            VideoRecyclerViewAdapter({ view: View, id: Int, timesPlayed: Int, videoListItem: VideoListApiEntity ->
                listItemClicked(view, id, timesPlayed, videoListItem)
            })
        recyclerView.adapter = adapter
        displayVideosList()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.add -> {
                maximizeExoPlayer()
                if(CommonUtils.isInternetAvailable(this)) {
                    lifecycleScope.launch {
                        mainViewModel.fetchVideoListFromUrl()
                    }
                }
                true
            }
            R.id.minimize -> {
                if(isMinimized == false) {
                    minimizeExoPlayer()
                }
                else {
                    maximizeExoPlayer()
                }
                true
            }
            R.id.sort_by_last_viewed_desc -> {
                lifecycleScope.launch {
                    mainViewModel.arrangeByLastPlayed(false)
                }
                true
            }
            R.id.sort_by_most_viewed_desc -> {
                lifecycleScope.launch {
                    mainViewModel.arrangeByNoOfTimesPlayed(false)
                }
                true
            }
            R.id.delete_all -> {
                lifecycleScope.launch {
                    mainViewModel.deleteAllVideos()
                }
                playScreenFragment.fragmentPlayScreenBinding.tvTitle.text = this.getString(R.string.no_vids_found)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun minimizeExoPlayer() {
        isMinimized = true
        binding.videoPlayer.visibility = View.GONE
        seekTime = exoVideoPlayer?.currentPosition
        mediaItem = exoVideoPlayer?.currentMediaItemIndex
        exoVideoPlayer?.playWhenReady = false
        binding.playScreenFrameLayout.visibility = View.VISIBLE
        playScreenFragment.fragmentPlayScreenBinding.tvTitle.text = currentVideotitle
        playScreenFragment.playVideoFromUrl(currentVideoURL)
        mediaItem?.let { seekTime?.let { it1 -> playScreenFragment.videoPlayer?.seekTo(it, it1) } }
        playScreenFragment.videoPlayer?.playWhenReady = true
    }

    private fun maximizeExoPlayer() {
        isMinimized = false
        binding.playScreenFrameLayout.visibility = View.GONE
        seekTime = playScreenFragment.videoPlayer?.currentPosition
        mediaItem = playScreenFragment.videoPlayer?.currentMediaItemIndex
        playScreenFragment.videoPlayer?.playWhenReady = false
        binding.videoPlayer.visibility = View.VISIBLE
        playVideoFromUrl(currentVideoURL)
        mediaItem?.let { seekTime?.let { it1 -> exoVideoPlayer?.seekTo(it, it1) } }
        exoVideoPlayer?.playWhenReady = true
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when(playbackState){
            Player.STATE_BUFFERING -> {
                    binding.progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                binding.progressBar.visibility = View.GONE
            }
            Player.STATE_IDLE -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {

        if(isMinimized == false) {
            minimizeExoPlayer()
        } else {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, id -> this@MainActivity.finish() }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun buildMediaSource(sampleUrl: String): MediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(this, "sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(sampleUrl)))
    }

    private fun initializePlayer() {
        exoVideoPlayer = SimpleExoPlayer.Builder(this).build()
        binding.videoPlayer.player = exoVideoPlayer
        binding.videoPlayer.setShowNextButton(false)
        binding.videoPlayer.setShowPreviousButton(false)
        exoVideoPlayer?.addListener(this)
    }

    private fun playVideoFromUrl(sampleUrl: String) {
        buildMediaSource(sampleUrl)?.let {
            exoVideoPlayer?.prepare(it)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.videoPlayer.visibility = View.VISIBLE
        exoVideoPlayer?.playWhenReady = true
        binding.playScreenFrameLayout.visibility = View.GONE
        isMinimized = false
    }

    override fun onStop() {
        super.onStop()
        exoVideoPlayer?.playWhenReady = false
        if (isFinishing) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        exoVideoPlayer?.release()
    }

    private fun listItemClicked(
        view: View,
        id: Int,
        timesPlayed: Int,
        videoListItem: VideoListApiEntity
    ) {
        if (view.id == R.id.ivMoreOptions) {
            val popup = PopupMenu(this, view)
            popup.inflate(R.menu.list_item_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    when (p0?.itemId) {
                        R.id.delete -> {
                            lifecycleScope.launch {
                                mainViewModel.deleteVideo(videoListItem)
                            }
                            maximizeExoPlayer()
                        }
                    }
                    return true
                }

            })
            popup.show();
        } else {
            val lastPlayed = System.currentTimeMillis()
            mainViewModel.updateVideoInfoById(timesPlayed, lastPlayed, id)
            currentVideoURL = videoListItem.rating
            currentVideotitle = videoListItem.fullName
            maximizeExoPlayer()
            exoVideoPlayer?.seekTo(0)
        }
    }

    interface ExoplayerListener {
        fun onURLChange(url: String?, title: String?)
    }

    fun setOnBundleSelected(exoplayerListener: ExoplayerListener) {
        this.exoplayerListener = exoplayerListener
    }

}
