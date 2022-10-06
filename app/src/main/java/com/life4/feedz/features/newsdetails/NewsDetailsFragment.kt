package com.life4.feedz.features.newsdetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.QuoteSpan
import android.text.style.URLSpan
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.life4.core.core.view.BaseFragment
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.R
import com.life4.feedz.data.MyPreference
import com.life4.feedz.databinding.FragmentNewDetailsBinding
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
                redirectUsingCustomTab(viewModel.args?.news?.url ?: "")
        }
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupData() {
        super.setupData()
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

        val imageGetter = ImageGetter(resources, getBinding().htmlViewer, requireContext())

        val styledText =
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, null)

        replaceQuoteSpans(styledText as Spannable)

        imageClick(styledText as Spannable)

        getBinding().htmlViewer.text = styledText

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

    private fun redirectUsingCustomTab(url: String) {
        val uri = Uri.parse(url)
        val intentBuilder = CustomTabsIntent.Builder()
        val customTabsIntent = intentBuilder.build()
        customTabsIntent.launchUrl(requireContext(), uri)
    }
}