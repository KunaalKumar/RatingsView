package dev.kunaal.demo

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var enableThresholdColors = false
    private val thresholdColorsMap = mapOf<Int, Int>(
            0 to 0xFFf44336.toInt(),
            35 to 0xFFffa726.toInt(),
            50 to 0xFFffeb3b.toInt(),
            75 to 0xFF66bb6a.toInt()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initial value for demo purposes
        seekbar.progress = (0..100).random()
        ratings_view.rating = seekbar.progress

        setupThresholdColorsButton()
        setupBgButton()
        setupArcColorButton()
        setupTextColorButton()

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ratings_view.rating = seekbar.progress
            }
        })
    }

    private fun setupThresholdColorsButton() {
        button.setOnClickListener {
            if (!enableThresholdColors) {
                enableThresholdColors = true
                button.text = "Disable Threshold Colors"
                ratings_view.addArcThresholdColor(thresholdColorsMap)
            } else {
                enableThresholdColors = false
                button.text = "Enable Threshold Colors"
                ratings_view.removeAllArcThresholdColor()
            }
        }
    }

    private fun setupBgButton() {
        bg_button.setOnClickListener {
            ratings_view.setBgColor(getRandomColor())
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
}