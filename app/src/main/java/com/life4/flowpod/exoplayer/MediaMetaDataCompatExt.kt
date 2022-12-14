package com.life4.flowpod.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.life4.flowpod.models.rss_.Enclosure
import com.life4.flowpod.models.rss_.Itunes
import com.life4.flowpod.models.rss_.RssPaginationItem


fun MediaMetadataCompat.toPodcast(): RssPaginationItem? {
    return description?.let {
        RssPaginationItem(
            id = it.mediaId ?: "",
            title = it.title.toString(),
            contentSnippet = it.subtitle.toString(),
            enclosure = Enclosure(url = it.mediaUri.toString(), length = null, type = null),
            itunes = Itunes(
                image = it.iconUri.toString(),
                author = it.subtitle.toString(),
                explicit = null,
                owner = null,
                summary = null, duration = null
            ),
            author = null,
            comments = null,
            content = it.description.toString(),
            contentEncoded = it.mediaDescription.toString(),
            creator = null,
            dcCreator = null,
            guid = null,
            isoDate = null,
            link = null,
            pubDate = null,
            siteImage = null,
            categoryId = null,
            pKey = null
        )
    }
}
