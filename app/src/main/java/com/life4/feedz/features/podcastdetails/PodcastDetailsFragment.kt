package com.life4.feedz.features.podcastdetails

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.life4.core.core.view.BaseFragment
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentPodcastDetailsBinding
import com.life4.feedz.exoplayer.service.isPlaying
import com.life4.feedz.exoplayer.toPodcast
import com.life4.feedz.features.main.MainViewModel
import com.life4.feedz.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PodcastDetailsFragment :
    BaseFragment<FragmentPodcastDetailsBinding, PodcastDetailsViewModel>(R.layout.fragment_podcast_details) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: PodcastDetailsViewModel by viewModels()
    private val args: PodcastDetailsFragmentArgs by navArgs()

    private var curPlayingSong: RssPaginationItem? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getBinding().item = args.podcast
    }

    override fun setupListener() {
        subscribeToObservers()

        getBinding().ivPlayPauseDetail.setOnClickListener {
            mainViewModel.togglePlaybackState()
        }

        getBinding().ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        getBinding().ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        getBinding().ivprevious10.setOnClickListener {
            mainViewModel.rewind()
        }

        getBinding().ivForward10.setOnClickListener {
            mainViewModel.fastForward()
        }

        getBinding().seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }

        })
    }

    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        getBinding().tvCurTime.text = dateFormat.format(ms)
    }

    private fun subscribeToObservers() {
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            getBinding().item = it.toPodcast()
            Log.d("ResimPodcast", it.toPodcast()?.itunes?.image.toString())
            getBinding().executePendingBindings()
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            getBinding().ivPlayPauseDetail.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            getBinding().seekBar.progress = it?.position?.toInt() ?: 0
        }
        viewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if (shouldUpdateSeekbar) {
                getBinding().seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        }
        viewModel.curSongDuration.observe(viewLifecycleOwner) {
            getBinding().seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
            dateFormat.timeZone = TimeZone.getTimeZone("GMT")
            getBinding().tvSongDuration.text = dateFormat.format(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.bottomPlayBackVisibility.postValue(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.bottomPlayBackVisibility.postValue(true)

    }
}