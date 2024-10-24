package com.example.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.dicoding_event)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val eventId = intent.getIntExtra("EVENT_ID", 0)
        if (eventId != 0) {
            detailViewModel.fetchEventDetail(eventId.toString())
            observeEventDetail()
        } else {
            Toast.makeText(this, getString(R.string.invalid_event_id), Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val registrationUrl = detailViewModel.eventDetail.value?.event?.link
            if (!registrationUrl.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(registrationUrl))
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.registration_link_not_available), Toast.LENGTH_SHORT).show()
            }
        }

        detailViewModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            var isFavorite = favoriteEvent != null
            updateFavoriteIcon(isFavorite)

            binding.fabFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) {
                    val event = detailViewModel.eventDetail.value?.event
                    if (event != null) {
                        val favoriteEntity = EventEntity(
                            id = event.id.toInt(),
                            name = event.name,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            imageUrl = event.imageUrl,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            link = event.link,
                            mediaCover = event.mediaCover,
                            summary = event.summary,
                            category = event.category,
                            active = event.active,
                            isBookmarked = true,
                            isFavorite = true
                        )
                        detailViewModel.addEventToFavorite(favoriteEntity)
                    }
                } else {
                    detailViewModel.removeEventFromFavorite(eventId)
                }
                updateFavoriteIcon(isFavorite)
            }
        }
    }

    private fun observeEventDetail() {
        detailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        detailViewModel.eventDetail.observe(this) { detailResponse ->
            detailResponse?.let {
                val event = it.event

                binding.tvEventName.text = event.name

                binding.tvDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

                Glide.with(this)
                    .load(event.imageLogo)
                    .into(binding.imageView)

                binding.tvEventOwner.text = event.ownerName
                binding.tvEventCity.text = event.cityName
                binding.tvQuota.text = getString(R.string.quota, event.quota)
                binding.tvRegistrants.text = getString(R.string.registrants, event.registrants)

                val remainingQuota = event.quota - event.registrants
                binding.tvRemainingQuota.text = getString(R.string.remaining_quota, remainingQuota)

                binding.tvEventTime.text = getString(R.string.event_time, event.beginTime, event.endTime)
            } ?: run {
                Log.e("DetailActivity", "Event detail is null")
                Toast.makeText(this, getString(R.string.failed_to_load_event_details), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_filled)
            binding.fabFavorite.contentDescription = getString(R.string.remove_from_favorites)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
            binding.fabFavorite.contentDescription = getString(R.string.add_to_favorites)
        }
    }
}
