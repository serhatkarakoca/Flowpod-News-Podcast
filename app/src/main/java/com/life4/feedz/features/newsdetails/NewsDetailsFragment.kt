package com.life4.feedz.features.newsdetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.QuoteSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.life4.core.core.view.BaseFragment
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.R
import com.life4.feedz.data.MyPreference
import com.life4.feedz.databinding.FragmentNewDetailsBinding
import com.life4.feedz.databinding.ViewImageBinding
import com.life4.feedz.extensions.ImageGetter
import com.life4.feedz.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsDetailsFragment :
    BaseFragment<FragmentNewDetailsBinding, NewsDetailsViewModel>(R.layout.fragment_new_details) {
    private val viewModel: NewsDetailsViewModel by viewModels()

    private var progressDialog: Dialog? = null

    @Inject
    lateinit var pref: MyPreference
    override fun setupListener() {
        getBinding().buttonGoNews.setOnClickListener {
            if (pref.getBrowserInApp() == true)
                setWebview(url = viewModel.args?.news?.url ?: "")
            else
                openNewsExternal(viewModel.args?.news?.url ?: "")
        }
    }

    override fun setupData() {
        setupViewModel(viewModel)
        arguments?.let { args ->
            viewModel.args = NewsDetailsFragmentArgs.fromBundle(args)
            getBinding().item = NewsDetailsFragmentArgs.fromBundle(args).news
            displayHtml(NewsDetailsFragmentArgs.fromBundle(args).news.getHtmlContent() ?: "")
            activity?.let {
                it.title = NewsDetailsFragmentArgs.fromBundle(args).news.title
            }
        }
    }

    private fun displayHtml(html: String) {
        // Creating object of ImageGetter class you just created
        val imageGetter = ImageGetter(resources, getBinding().htmlViewer, requireContext())

        // Using Html framework to parse html
        val styledText =
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, null)

        Log.d("htmlFrom:styled", styledText.toString())

        replaceQuoteSpans(styledText as Spannable)
        imageClick(styledText as Spannable)

        // setting the text after formatting html and downloading and setting images
        getBinding().htmlViewer.text = styledText

        // to enable image/link clicking
        getBinding().htmlViewer.movementMethod = LinkMovementMethod.getInstance()

    }


    private fun replaceQuoteSpans(spannable: Spannable) {
        val quoteSpans: Array<QuoteSpan> =
            spannable.getSpans(0, spannable.length - 1, QuoteSpan::class.java)

        for (quoteSpan in quoteSpans) {
            val start: Int = spannable.getSpanStart(quoteSpan)
            val end: Int = spannable.getSpanEnd(quoteSpan)
            val flags: Int = spannable.getSpanFlags(quoteSpan)
            spannable.removeSpan(quoteSpan)
            spannable.setSpan(
                com.life4.feedz.extensions.QuoteSpan(
                    // background color
                    ContextCompat.getColor(requireContext(), R.color.white),
                    // strip color
                    ContextCompat.getColor(requireContext(), R.color.primaryColor),
                    // strip width
                    10F, 50F
                ),
                start, end, flags
            )
        }
    }

    // Function to parse image tags and enable click events
    private fun imageClick(html: Spannable) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                override fun onClick(v: View) {
                    //howImageDialog(span.source ?: "")
                }
            }, start, end, flags)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebview(url: String) {

        getBinding().nestedScrollview.isVisible = false
        getBinding().webView.isVisible = true
        getBinding().webView.webChromeClient = WebChromeClient()
        getBinding().webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError
            ) {
                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(requireContext())
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                activity?.let {
                    (it as MainActivity).showLoading(requestType = BaseViewModel.RequestType.ACTION)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                activity?.let {
                    (it as MainActivity).showContent(requestType = BaseViewModel.RequestType.ACTION)
                }
            }

        }

        getBinding().webView.settings.javaScriptEnabled = true
        getBinding().webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        getBinding().webView.settings.setSupportZoom(true)
        getBinding().webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        getBinding().webView.settings.domStorageEnabled = true
        getBinding().webView.settings.setSupportMultipleWindows(false)
        getBinding().webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        getBinding().webView.loadUrl(url)

    }

    private fun openNewsExternal(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun showImageDialog(url: String) {
        if (progressDialog == null) {
            progressDialog = Dialog(requireContext()).apply {
                val binding = DataBindingUtil.inflate<ViewImageBinding>(
                    LayoutInflater.from(requireContext()),
                    R.layout.view_image,
                    null,
                    false
                )
                binding.url = url
                setCancelable(true)
                setContentView(binding.root)

            }
        }
        if (progressDialog?.isShowing == false) {
            progressDialog?.show()
        }
    }
}