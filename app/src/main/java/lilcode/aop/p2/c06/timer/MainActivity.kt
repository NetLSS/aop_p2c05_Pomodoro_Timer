package lilcode.aop.p2.c06.timer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    // 후반부 에서는 뷰 바인딩을 사용할 것임.

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
    }

    private fun bindViews() {
        // 각각의 뷰에대한 리스너와 코드를 연결
        seekBar.setOnSeekBarChangeListener(
            //object로 선언하면 클래스 선언과 동시에 객체가 생성됩니다.
            //object 객체 역시 다른 class를 상속하거나 interface를 구현할 수 있습니다.
            object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n") // 현 프로젝트에서는 다국어를 지원안할 거라 이렇게
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    remainMinutesTextView.text = "%02d".format(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
    }
}