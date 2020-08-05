package dev.kunaal.demo

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

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
        seekbar.progress = 85
        ratings_view.rating = seekbar.progress

        button.setOnClickListener {
            if (!enableThresholdColors) {
                enableThresholdColors = true
                button.text = "Disable Colors"
                ratings_view.addArcThresholdColor(thresholdColorsMap)
            } else {
                enableThresholdColors = false
                button.text = "Enable Colors"
                ratings_view.removeAllArcThresholdColor()
            }
        }

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
}