package lilcode.aop.p2.c06.timer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
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

    private val button25min: Button by lazy{
        findViewById(R.id.button25min)
    }

    private val soundPool = SoundPool.Builder().build() // soundPool 선언

    private var currentCountDownTImer: CountDownTimer? = null

    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setOnClickListener()
        initSounds() // soundPool 사용
    }

    override fun onResume() {
        super.onResume()
        // 앱이 다시 시작되는 경우
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        // 앱이 화면에 보이지 않을 경우
        //soundPool.pause() // 특정 스트림 아이디로 정지
        soundPool.autoPause() // 모든 활성 스트림 정지
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // 더이상 필요 없으면 사운드풀 메모리에서 해제
    }

    private fun initSounds() {
        // sound 로드
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
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
                    if (fromUser) { // updateSeekBar 에서 변경되는 경우도 있기때문에 유저가 만질때만.
                        // 프로그레스바를 조정하고 있으면 초를 0으로 맞춰주기 위해 추가 (텍스트뷰 갱신)
                        updateRemainTimes(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 조정하기 시작하면 기존 타이머가 있을 때 cancel 후 null
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return

                    if (seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }
            }
        )

    }

    private fun setOnClickListener(){
        button25min.setOnClickListener {
            val minutes25 = 25*1000*60L // 현재 버튼 1개라 일단 이렇게 구현.

            stopCountDown()

            updateSeekBar(minutes25)
            updateRemainTimes(minutes25)

            startCountDown()
        }
    }

    private fun startCountDown() {
        // 사용자가 바에서 손을 떼는 순간 새로운 타이머 생성
        currentCountDownTImer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTImer?.start()

        // 소리 재생 (null 아닌 경우 사운드 재생)
        // 디바이스 자체에 요청하는 거기 때문에 화면 종료시 계속 재생될 수 있음
        // 생명주기이 따라 처리 필요.
        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTImer?.cancel()
        currentCountDownTImer = null
        soundPool.autoPause()
    }


    // 타이머 생성 함수
    private fun createCountDownTimer(initialMillis: Long): CountDownTimer =
        object : CountDownTimer(initialMillis, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                updateRemainTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun completeCountDown() {
        updateRemainTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    // 초가 지날 때마다 텍스트뷰 갱신
    @SuppressLint("SetTextI18n")
    private fun updateRemainTimes(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt() // 분
    }
}