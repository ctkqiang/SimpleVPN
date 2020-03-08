package my.com.Kylogger.JohnMelodyMe.Internet.vpn.Model;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 * @PACKAGE: ByteBufferPool.class
 */

public class ByteBufferPool {
    private static final String TAG = "VPN";
    private static final int BUFFER_SIZE = 0x4000;
    private static final ConcurrentLinkedQueue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();

    public static ByteBuffer acquire() {
        synchronized (pool) {
            ByteBuffer buffer = pool.poll();
            if (buffer == null) {
                buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            }
            buffer.clear();
            return buffer;
        }
    }

    public static void release(ByteBuffer buffer) {
        synchronized (pool) {
            buffer.clear();
            //  pool.offer(buffer);
        }
    }

    public static void clear() {
        synchronized (pool) {
            pool.clear();
        }
    }
}