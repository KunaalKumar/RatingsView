package dev.kunaal.demo

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private var thresholdColorsEnabled = false
    private var thresholdColorsMap = mapOf<Int, Int>(
            0 to 0xFFf44336.toInt(),
            35 to 0xFFffa726.toInt(),
            50 to 0xFFffeb3b.toInt(),
            75 to 0xFF66bb6a.toInt()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Initial value for demo purposes
            rating_seekbar.progress = (25..100).random()
            ratings_view.rating = rating_seekbar.progress
        } else {
            thresholdColorsEnabled = savedInstanceState.getBoolean("thresholdColorsEnabled")
            if (thresholdColorsEnabled)
                button.text = "Disable Threshold Colors"
        }

        setupThresholdColorsButton()
        setupBgViews()
        setupArcColorButton()
        setupTextColorButton()

        setupRatingViews()
        setupTextScaleSeekBar()
        setupArcWidthSeekbar()

        ratings_view.setOnClickListener {
            ratings_view.toggleLoadingAnimation()
        }

    }

    private fun setupRatingViews() {

        rating_picker.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                rating_seekbar.progress = min(100, max(0, rating_picker.text.toString().toInt()))
                rating_picker.text = SpannableStringBuilder(rating_seekbar.progress.toString())
                ratings_view.rating = rating_seekbar.progress
            }
            false
        }

        rating_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ratings_view.rating = seekBar!!.progress
                rating_picker.text = SpannableStringBuilder(seekBar.progress.toString())
            }
        })
    }

    private fun setupTextScaleSeekBar() {
        text_scale_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ratings_view.textScale = 1 + ((seekBar!!.progress - 50) / 100F)
            }
        })
    }

    private fun setupArcWidthSeekbar() {
        arc_width_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ratings_view.setArcWidthScale(seekBar!!.progress.toFloat() * 0.02F)
            }
        })
    }

    private fun setupThresholdColorsButton() {
        button.setOnClickListener {
            if (!thresholdColorsEnabled) {
                thresholdColorsEnabled = true
                button.text = "Disable Threshold Colors"
                ratings_view.addArcThresholdColor(thresholdColorsMap)
            } else {
                thresholdColorsEnabled = false
                button.text = "Enable Threshold Colors"
                ratings_view.removeAllArcThresholdColor()
            }
        }
    }

    private fun setupBgViews() {
        bg_button.setOnClickListener {
            ratings_view.setBgColor(getRandomColor())
        }

        clear_bg_button.setOnClickListener {
            ratings_view.setBgColor(android.R.color.transparent)
        }
    }

    private fun setupArcColorButton() {
        arc_color_button.setOnClickListener {
            ratings_view.setArcColor(getRandomColor())
        }
    }

    private fun setupTextColorButton() {
        text_color_button.setOnClickListener {
            ratings_view.setTextColor(getRandomColor())
        }
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("thresholdColorsEnabled", thresholdColorsEnabled)
    }
}