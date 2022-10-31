package com.life4.feedz.features.podcastdetails

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.R
import com.life4.feedz.databinding.BottomSheetTimerBinding
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

        observe(viewModel.timerState) {
            if (it != null && (it == "0").not()) {
                getBinding().tvCountDown.text = it
                getBinding().layoutCountDown.isVisible = true
            } else
                getBinding().layoutCountDown.isVisible = false
        }

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

        getBinding().btnSleepTimer.setOnClickListener {
            showTimerDialog()
        }

        getBinding().cancelTimer.setOnClickListener {
            viewModel.cancelTimer()
            getBinding().layoutCountDown.isVisible = false
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

    private fun showTimerDialog() {
        BottomSheetDialog(requireContext()).apply {
            val inflater = LayoutInflater.from(context)
            val binding = DataBindingUtil.inflate<BottomSheetTimerBinding>(
                inflater,
                R.layout.bottom_sheet_timer,
                null,
                false
            )

            binding.layoutSelectYourTime.setOnClickListener {
                binding.layoutTimeSelection.isVisible = true
                binding.layoutDefaultTimes.isVisible = false
            }

            binding.layoutMin5.setOnClickListener {
                viewModel.setTimer("00:05")
                dismiss()
            }

            binding.layoutMin10.setOnClickListener {
                viewModel.setTimer("00:10")
                dismiss()
            }

            binding.layoutMin15.setOnClickListener {
                viewModel.setTimer("00:15")
                dismiss()
            }

            binding.layoutMin20.setOnClickListener {
                viewModel.setTimer("00:20")
                dismiss()
            }

            binding.timerSleep.setIs24HourView(true)
            var hour = "00"
            var minute = "00"

            binding.timerSleep.hour = 0
            binding.timerSleep.minute = 0

            binding.timerSleep.setOnTimeChangedListener { view, hourOfDay, minuteOfDay ->
                hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                minute = if (minuteOfDay < 10) "0$minuteOfDay" else minuteOfDay.toString()
            }

            binding.btnSet.setOnClickListener {
                Toast.makeText(requireContext(), "$hour:$minute", Toast.LENGTH_SHORT).show()
                viewModel.setTimer("$hour:$minute")
                dismiss()
            }

            setContentView(binding.root)
        }.show()
    }

    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        var formattedDate = dateFormat.format(ms)
        if (formattedDate.startsWith("00:"))
            formattedDate = formattedDate.substringAfter("00:")
        getBinding().tvCurTime.text = formattedDate
    }

    private fun subscribeToObservers() {
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            getBinding().item = it.toPodcast()
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
            var formattedDate = dateFormat.format(it)
            if (formattedDate.startsWith("00:"))
                formattedDate = formattedDate.substringAfter("00:")
            getBinding().tvSongDuration.text = formattedDate
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