package lilcode.aop.p2.c06.timer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    // 후반부 에서는 뷰 바인딩을 사용할 것임.
    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private var currentCountDownTImer: CountDownTimer? = null

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
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser){ // updateSeekBar 에서 변경되는 경우도 있기때문에 유저가 만질때만.
                        // 프로그레스바를 조정하고 있으면 초를 0으로 맞춰주기 위해 추가 (텍스트뷰 갱신)
                        updateRemainTimes(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 조정하기 시작하면 기존 타이머가 있을 때 cancel 후 null
                    currentCountDownTImer?.cancel()
                    currentCountDownTImer = null
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    // 사용자가 바에서 손을 떼는 순간 새로운 타이머 생성
                    currentCountDownTImer = createCountDownTimer(seekBar.progress * 60 * 1000L)
                    currentCountDownTImer?.start()
                }
            }
        )
    }
    
    
    // 타이머 생성 함수
    private fun createCountDownTimer(initialMillis: Long): CountDownTimer =
        object : CountDownTimer(initialMillis, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                updateRemainTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                updateRemainTimes(0)
                updateSeekBar(0)
            }
        }

    // 초가 지날 때마다 텍스트뷰 갱신
    @SuppressLint("SetTextI18n")
    private fun updateRemainTimes(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long){
        seekBar.progress = (remainMillis / 1000 / 60).toInt() // 분
    }
}