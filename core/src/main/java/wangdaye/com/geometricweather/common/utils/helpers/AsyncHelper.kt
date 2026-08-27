package wangdaye.com.geometricweather.common.utils.helpers

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

object AsyncHelper {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mainHandler = Handler(Looper.getMainLooper())

    class Controller internal constructor(private val job: Job) {
        fun cancel() {
            job.cancel()
        }
    }

    class Emitter<T> internal constructor(
        private val sendAction: (T?, Boolean) -> Unit
    ) {
        fun send(t: T?, done: Boolean) {
            sendAction(t, done)
        }
    }

    fun interface Task<T> {
        fun execute(emitter: Emitter<T>)
    }

    fun interface Callback<T> {
        fun call(t: T?, done: Boolean)
    }

    @JvmStatic
    fun <T> runOnIO(task: Task<T>, callback: Callback<T>): Controller {
        val job = applicationScope.launch(Dispatchers.IO) {
            try {
                task.execute(Emitter { t, done ->
                    postToMain { callback.call(t, done) }
                })
            } catch (_: CancellationException) {
                throw CancellationException()
            } catch (_: Exception) {
                // Match Rx create(): uncaught errors in the task fail the stream.
            }
        }
        return Controller(job)
    }

    @JvmStatic
    fun runOnIO(runnable: Runnable): Controller {
        val job = applicationScope.launch(Dispatchers.IO) {
            runnable.run()
        }
        return Controller(job)
    }

    @JvmStatic
    fun <T> runOnExecutor(task: Task<T>, callback: Callback<T>, executor: Executor): Controller {
        val dispatcher = executor.asCoroutineDispatcher()
        val job = applicationScope.launch(dispatcher) {
            try {
                task.execute(Emitter { t, done ->
                    postToMain { callback.call(t, done) }
                })
            } catch (_: CancellationException) {
                throw CancellationException()
            } catch (_: Exception) {
            }
        }
        return Controller(job)
    }

    @JvmStatic
    fun runOnExecutor(runnable: Runnable, executor: Executor): Controller {
        val dispatcher = executor.asCoroutineDispatcher()
        val job = applicationScope.launch(dispatcher) {
            runnable.run()
        }
        return Controller(job)
    }

    @JvmStatic
    fun delayRunOnIO(runnable: Runnable, milliSeconds: Long): Controller {
        val job = applicationScope.launch(Dispatchers.IO) {
            delay(milliSeconds)
            runnable.run()
        }
        return Controller(job)
    }

    @JvmStatic
    fun delayRunOnUI(runnable: Runnable, milliSeconds: Long): Controller {
        val job = applicationScope.launch(Dispatchers.Main.immediate) {
            delay(milliSeconds)
            runnable.run()
        }
        return Controller(job)
    }

    @JvmStatic
    fun intervalRunOnUI(
        runnable: Runnable,
        intervalMilliSeconds: Long,
        initDelayMilliSeconds: Long
    ): Controller {
        val job = applicationScope.launch(Dispatchers.Main.immediate) {
            delay(initDelayMilliSeconds)
            while (isActive) {
                runnable.run()
                delay(intervalMilliSeconds)
            }
        }
        return Controller(job)
    }

    private fun postToMain(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }
}
