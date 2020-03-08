package my.com.Kylogger.JohnMelodyMe.Internet.vpn.Model;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Copyright 2020 © John Melody Melissa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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