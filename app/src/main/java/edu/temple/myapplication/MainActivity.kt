    package edu.temple.myapplication

    import android.content.ComponentName
    import android.content.Intent
    import android.content.ServiceConnection
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.os.Handler
    import android.os.IBinder
    import android.view.Menu
    import android.view.MenuItem
    import android.widget.Button
    import android.widget.TextView

    class MainActivity : AppCompatActivity() {

        lateinit var timeBinder : TimerService.TimerBinder
        var isConnected =  false
        lateinit var handler: Handler

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
                timeBinder = service as TimerService.TimerBinder
                timeBinder.setHandler(handler)
                isConnected = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isConnected = false
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val startButton = findViewById<Button>(R.id.startButton)
            val stopButton = findViewById<Button>(R.id.stopButton)
            val textView = findViewById<TextView>(R.id.textView)

            handler = Handler(mainLooper) {
                textView.text = it.what.toString()
                true
            }

            bindService(
                Intent(this, TimerService::class.java),
                serviceConnection,
                BIND_AUTO_CREATE
            )

            startButton.setOnClickListener {
                if (isConnected) {
                    if (!timeBinder.isRunning && !timeBinder.paused) {
                        timeBinder.start(10)
                        startButton.text = "Pause"
                    } else {
                        timeBinder.pause()
                        startButton.text = if (timeBinder.paused) "Resume" else "Pause"
                    }
                }
            }

            stopButton.setOnClickListener {
                if (isConnected) {
                    timeBinder.stop()
                    startButton.text = "Start"
                    textView.text = "0"
                }
            }
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.main, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (!isConnected) return true

            when (item.itemId) {

                R.id.menu_start -> {
                    if (!timeBinder.isRunning && !timeBinder.paused) {
                        timeBinder.start(10)
                    } else {
                        timeBinder.pause()
                    }
                    return true
                }

                R.id.menu_stop -> {
                    timeBinder.stop()
                    findViewById<TextView>(R.id.textView).text = "0"
                    return true
                }
            }

            return super.onOptionsItemSelected(item)
        }
    }