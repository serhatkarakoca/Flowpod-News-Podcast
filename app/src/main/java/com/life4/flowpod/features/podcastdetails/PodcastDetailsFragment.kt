package com.life4.flowpod.features.podcastdetails

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.text.Html
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.work.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.flowpod.R
import com.life4.flowpod.databinding.BottomSheetMoreBinding
import com.life4.flowpod.databinding.BottomSheetTimerBinding
import com.life4.flowpod.databinding.FragmentPodcastDetailsBinding
import com.life4.flowpod.exoplayer.service.isPlaying
import com.life4.flowpod.exoplayer.toPodcast
import com.life4.flowpod.features.main.MainViewModel
import com.life4.flowpod.features.podcast.offline.DownloadService
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.utils.serializeToJson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PodcastDetailsFragment :
    BaseFragment<FragmentPodcastDetailsBinding, PodcastDetailsViewModel>(R.layout.fragment_podcast_details) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: PodcastDetailsViewModel by viewModels()
    private val args: PodcastDetailsFragmentArgs by navArgs()

    private var job: Job? = null
    private var curPlayingSong: RssPaginationItem? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getDownloadedPodcasts()
        getBinding().item = args.podcast

        viewModel.podcastEpisode.value = args.podcast

        viewModel.podcastEpisode.value?.getHtmlContent()?.let {
            getBinding().tvPodcastDescription.text =
                Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
            getBinding().tvPodcastDescriptionLong.text =
                Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
        }

    }

    override fun setupListener() {
        subscribeToObservers()

        observe(viewModel.isError) {
            getBinding().isError = it
        }

        observe(viewModel.timerState) {
            if (it != null && (it == "0").not()) {
                getBinding().tvCountDown.text = it
                getBinding().layoutCountDown.isVisible = true
            } else
                getBinding().layoutCountDown.isVisible = false
        }

        getBinding().tvReadMore.setOnClickListener {
            getBinding().tvReadMore.text =
                if (getBinding().tvPodcastDescriptionLong.isVisible) getString(R.string.read_more) else getString(
                    R.string.read_less
                )
            getBinding().tvPodcastDescriptionLong.isVisible =
                !getBinding().tvPodcastDescriptionLong.isVisible
            getBinding().tvPodcastDescription.isVisible =
                !getBinding().tvPodcastDescription.isVisible
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

        getBinding().imgMore.setOnClickListener {
            showMoreBottomSheet()
        }

        getBinding().cancelTimer.setOnClickListener {
            viewModel.cancelTimer()
            getBinding().layoutCountDown.isVisible = false
            Snackbar.make(
                requireView(),
                getString(R.string.timer_cancelled_info),
                Snackbar.ANIMATION_MODE_SLIDE
            ).show()

        }

        getBinding().lottieDownload.setOnClickListener {
            args.podcast.enclosure?.url ?: return@setOnClickListener
            downloadFileFromUrl(args.podcast)
        }

        getBinding().iconDownloaded.setOnClickListener {
            args.podcast.enclosure?.url ?: return@setOnClickListener
            if (viewModel.isDownloaded) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(getString(R.string.warning))
                    setMessage(getString(R.string.are_u_sure_delete))
                    setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                    setPositiveButton(getString(R.string.remove)) { dialog, _ ->
                        val files = requireContext().filesDir.listFiles()
                        files?.let {
                            files.filter { it.canRead() && it.isFile && it.name.endsWith(".mp3") }
                                .firstOrNull {
                                    it.absolutePath.contains(
                                        args.podcast.enclosure?.url?.substringAfter("/media")
                                            .toString()
                                    )
                                }?.delete()
                        }
                        viewModel.deleteDownloadedPodcast(args.podcast)
                        getBinding().lottieDownload.repeatCount = 1
                        getBinding().executePendingBindings()
                        dialog.dismiss()
                    }
                }.show()
            }
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

    private fun showMoreBottomSheet() {
        BottomSheetDialog(requireContext()).apply {
            val binding = DataBindingUtil.inflate<BottomSheetMoreBinding>(
                LayoutInflater.from(context),
                R.layout.bottom_sheet_more,
                null,
                false
            )

            binding.root.setOnClickListener {
                args.podcast.enclosure?.url ?: return@setOnClickListener
                val files = requireContext().filesDir.listFiles()
                files?.let {
                    files.filter { it.canRead() && it.isFile && it.name.endsWith(".mp3") }
                        .firstOrNull {
                            it.absolutePath.contains(
                                args.podcast.enclosure?.url?.substringAfter("/media").toString()
                            )
                        }?.delete()
                }
                viewModel.deleteDownloadedPodcast(args.podcast)
                dismiss()
            }

            setContentView(binding.root)
        }.show()
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
            if (it > 0) {
                getBinding().seekBar.max = it.toInt()
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
                dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                var formattedDate = dateFormat.format(it)
                if (formattedDate.startsWith("00:"))
                    formattedDate = formattedDate.substringAfter("00:")
                getBinding().songDuration = formattedDate
            }
        }
    }


    private fun downloadFileFromUrl(podcast: RssPaginationItem) {

        val data = Data.Builder().putStringArray("url", arrayOf(podcast.enclosure?.url ?: ""))
            .putString("podcastItem", serializeToJson(podcast))
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val myWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<DownloadService>()
                .setConstraints(constraints)
                .setInputData(data)
                .build()

        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(myWorkRequest.id)
            .observe(this) {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    getBinding().lottieDownload.repeatCount = 1
                    viewModel.isError.postValue(false)

                } else if (it.state == WorkInfo.State.FAILED) {
                    WorkManager.getInstance(requireContext()).cancelAllWork()
                    getBinding().lottieDownload.repeatCount = 1
                    viewModel.isError.postValue(true)
                    Toast.makeText(
                        requireContext(),
                        getText(R.string.error_download),
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (it.state == WorkInfo.State.RUNNING) {
                    getBinding().lottieDownload.playAnimation()
                    getBinding().lottieDownload.repeatCount = ValueAnimator.INFINITE
                    viewModel.isError.postValue(false)

                } else if (it.state == WorkInfo.State.ENQUEUED) {
                    //enqued
                } else if (it.state == WorkInfo.State.CANCELLED) {
                    WorkManager.getInstance(requireContext()).cancelAllWork()
                    getBinding().lottieDownload.repeatCount = 1
                }

            }
    }

    private fun getDownloadedPodcasts() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDownloadedPodcasts().collectLatest { it ->
                viewModel.isDownloaded =
                    it.firstOrNull { args.podcast.title == it.podcastItem?.title } != null
                getBinding().isDownloaded = viewModel.isDownloaded
                viewModel.downloadedPodcasts.value = it
            }
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