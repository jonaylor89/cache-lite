
import java.util.concurrent.TimeUnit

class ExpirableCache(private val delegate: Cache, 
                     private val flushInterval: Long = TimeUnit.MINUTES.toMillis(1)) : Cache {

    private var lastFlushTime = System.nanoTime() 

    override val size: Int 
        get() = delegate.size

    override fun set(key: Any, value: Any) {
        delegate[key] = value
    }

    override fun remove(key: Any): Any? {
        recycle()
        return delegate.remove(key)
    }

    override fun get(key: Any): Any? {
        recycle()
        return delegate[key]
    }

    override fun clear() = delegate.clear()

    private fun recycle() {
        val shouldRecycle = System.nanoTime() - lastFlushTime >= TimeUnit.MILLISECONDS.toNanos(flushInterval)
        if (!shouldRecycle) return
        delegate.clear()
    }

}