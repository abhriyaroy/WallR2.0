package zebrostudio.wallr100.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

interface ImageLoader {
  fun load(context: Context, @DrawableRes drawableRes: Int, target: ImageView)
  fun load(context: Context, imagePath: String, target: ImageView)
  fun loadWithListener(context: Context,
    imageLink: String,
    target: ImageView,
    listener: LoaderListener)

  fun loadWithListener(context: Context,
    imageBitmap: Bitmap,
    target: ImageView,
    listener: LoaderListener)

  fun loadWithFixedSize(context: Context,
    imageLink: String,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL,
    transitionOptions: DrawableTransitionOptions = withCrossFade(),
    width: Int,
    height: Int)

  fun loadWithCenterCropping(context: Context,
    bitmap: Bitmap,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE)

  fun loadWithCenterCropping(context: Context,
    bitmap: Bitmap,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE,
    transitionOptions: DrawableTransitionOptions)

  fun loadWithPlaceHolder(context: Context,
    imageLink: String,
    target: ImageView, @DrawableRes placeHolder: Int)

  fun loadWithThumbnail(context: Context,
    highQualityImageLink: String,
    lowQualityImageLink: String,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE,
    listener: LoaderListener)
}

class ImageLoaderImpl : ImageLoader {

  override fun load(context: Context, drawableRes: Int, target: ImageView) {
    Glide.with(context).load(drawableRes).into(target)
  }

  override fun load(context: Context, imagePath: String, target: ImageView) {
    Glide.with(context).load(imagePath).into(target)
  }

  override fun loadWithListener(context: Context,
    imageLink: String,
    target: ImageView,
    listener: LoaderListener) {
    Glide.with(context).load(imageLink).listener(object : RequestListener<Drawable> {
      override fun onLoadFailed(e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean): Boolean {
        return listener.onLoadFailed(e, model, target, isFirstResource)
      }

      override fun onResourceReady(resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean): Boolean {
        return listener.onResourceReady(resource, model, target, dataSource, isFirstResource)
      }

    }).into(target)
  }

  override fun loadWithListener(context: Context,
    imageBitmap: Bitmap,
    target: ImageView,
    listener: LoaderListener) {
    Glide.with(context).load(imageBitmap).listener(object : RequestListener<Drawable> {
      override fun onLoadFailed(e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean): Boolean {
        return listener.onLoadFailed(e, model, target, isFirstResource)
      }

      override fun onResourceReady(resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean): Boolean {
        return listener.onResourceReady(resource, model, target, dataSource, isFirstResource)
      }

    }).into(target)
  }

  override fun loadWithFixedSize(context: Context,
    imageLink: String,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy,
    transitionOptions: DrawableTransitionOptions,
    width: Int,
    height: Int) {
    with(RequestOptions()
        .diskCacheStrategy(cacheStrategy)
        .override(width, height)
        .centerCrop()) {
      Glide.with(context).load(imageLink).transition(transitionOptions).apply(this).into(target)
    }
  }

  override fun loadWithCenterCropping(context: Context,
    bitmap: Bitmap,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy) {
    with(RequestOptions().diskCacheStrategy(cacheStrategy).centerCrop()) {
      Glide.with(context).load(bitmap).apply(this).transition(withCrossFade()).into(target)
    }
  }

  override fun loadWithCenterCropping(context: Context,
    bitmap: Bitmap,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy,
    transitionOptions: DrawableTransitionOptions) {
    with(RequestOptions().diskCacheStrategy(cacheStrategy).centerCrop()) {
      Glide.with(context).load(bitmap).apply(this).transition(transitionOptions).into(target)
    }
  }

  override fun loadWithPlaceHolder(context: Context,
    imageLink: String,
    target: ImageView,
    placeHolder: Int) {
    val options = RequestOptions().placeholder(placeHolder).dontAnimate()
    Glide.with(context).load(imageLink).apply(options).into(target)
  }

  override fun loadWithThumbnail(context: Context,
    highQualityImageLink: String,
    lowQualityImageLink: String,
    target: ImageView,
    cacheStrategy: DiskCacheStrategy,
    listener: LoaderListener) {
    RequestOptions().diskCacheStrategy(cacheStrategy).centerCrop().let { thumbNailOptions ->
      RequestOptions().diskCacheStrategy(cacheStrategy).centerCrop().let { imageOptions ->
        Glide.with(context).load(highQualityImageLink)
            .thumbnail(Glide.with(context).load(lowQualityImageLink).apply(thumbNailOptions))
            .apply(imageOptions).listener(object : RequestListener<Drawable> {
              override fun onLoadFailed(e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean): Boolean {
                return listener.onLoadFailed(e, model, target, isFirstResource)
              }

              override fun onResourceReady(resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean): Boolean {
                return listener.onResourceReady(resource,
                  model,
                  target,
                  dataSource,
                  isFirstResource)
              }
            }).into(target)
      }
    }
  }
}

interface LoaderListener {
  fun onLoadFailed(e: GlideException?,
    model: Any?,
    target: Target<Drawable>?,
    isFirstResource: Boolean): Boolean

  fun onResourceReady(resource: Drawable?,
    model: Any?,
    target: Target<Drawable>?,
    dataSource: DataSource?,
    isFirstResource: Boolean): Boolean
}