package com.ryd.echokeepalive;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public final class LongLiveSocket {

    private static final String TAG = "LongLiveSocket";

    // 重连时间
    private static final long RETRY_INTERVAL_MILLIS = 3 * 1000;
    // 心跳间隔时间
    private static final long HEART_BEAT_INTERVAL_MILLIS = 5 * 1000;
    // 心跳超时时间
    private static final long HEART_BEAT_TIMEOUT_MILLIS = 2 * 1000;

    /**
     * 错误回调
     */
    public interface ErrorCallback {
        /**
         * 如果需要重连，返回 true
         */
        boolean onError();
    }


    /**
     * 读数据回调
     */
    public interface DataCallback {
        void onData(byte[] data, int offset, int len);
    }


    /**
     * 写数据回调
     */
    public interface WritingCallback {
        void onSuccess();
        void onFail(byte[] data, int offset, int len);
    }


    private final String mHost;
    private final int mPort;
    private final DataCallback mDataCallback;
    private final ErrorCallback mErrorCallback;

    private final HandlerThread mWriterThread;
    private final Handler mWriterHandler;
    private final Handler mUIHandler = new Handler(Looper.getMainLooper());

    private final Object mLock = new Object();
    private Socket mSocket;  // guarded by mLock 用锁保护
    private boolean isClosed; // guarded by mLock

    private volatile int mSeqNumHeartBeatSent;
    private volatile int mSeqNumHeartBeatRecv;


    public LongLiveSocket(String host, int port,
                          DataCallback dataCallback, ErrorCallback errorCallback) {
        mHost = host;
        mPort = port;
        mDataCallback = dataCallback;
        mErrorCallback = errorCallback;

        mWriterThread = new HandlerThread("socket-writer");
        mWriterThread.start();
        mWriterHandler = new Handler(mWriterThread.getLooper());
        mWriterHandler.post(this::initSocket);
    }

    // 创建socket
    // 启动客户端读取线程
    // 启动心跳任务
    private void initSocket() {
        while (true) {
            if (isClosed()) return;

            try {
                Socket socket = new Socket(mHost, mPort);
                synchronized (mLock) {
                    // 在我们创建 socket 的时候，客户可能就调用了 close()
                    if (isClosed) {
                        silentlyClose(socket);
                        return;
                    }
                    mSocket = socket;
                    // 每次创建新的 socket，会开一个线程来读数据
                    Thread reader = new Thread(new ReaderTask(socket), "socket-reader");
                    reader.start();
                    // 启动心跳
                    mWriterHandler.post(mHeartBeatTask);
                }
                break;
            } catch (IOException e) {
                Log.e(TAG, "initSocket: ", e);
                if (isClosed() || !mErrorCallback.onError()) {
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_INTERVAL_MILLIS);
                } catch (InterruptedException e1) {
                    // interrupt writer-thread to quit
                    break;
                }
            }
        }
    }

    // 心跳task,向服务端写心跳数据
    private final Runnable mHeartBeatTask = new Runnable() {
        private byte[] mHeartBeat = new byte[0];

        @Override
        public void run() {

            ++mSeqNumHeartBeatSent;
            // 我们使用长度为 0 的数据作为 heart beat
            write(mHeartBeat, new WritingCallback() {
                @Override
                public void onSuccess() {
                    // 每隔 HEART_BEAT_INTERVAL_MILLIS 发送一次
                    mWriterHandler.postDelayed(mHeartBeatTask, HEART_BEAT_INTERVAL_MILLIS);
                    // At this point, the heart-beat might be received and handled
                    if (mSeqNumHeartBeatRecv < mSeqNumHeartBeatSent) {
                        mUIHandler.postDelayed(mHeartBeatTimeoutTask, HEART_BEAT_TIMEOUT_MILLIS);
                        // double check
                        if (mSeqNumHeartBeatRecv == mSeqNumHeartBeatSent) {
                            mUIHandler.removeCallbacks(mHeartBeatTimeoutTask);
                        }
                    }
                }

                @Override
                public void onFail(byte[] data, int offset, int len) {
                    // nop
                    // write() 方法会处理失败
                }
            });
        }
    };

    // 心跳超时task
    private final Runnable mHeartBeatTimeoutTask = () -> {
        Log.e(TAG, "mHeartBeatTimeoutTask#run: heart beat timeout");
        closeSocketLocked();
    };

    // 读取服务器的数据
    private class ReaderTask implements Runnable {

        private final Socket mSocket;

        public ReaderTask(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            try {
                readResponse();
            } catch (IOException e) {

            }
        }

        private void readResponse() throws IOException {
            // For simplicity, assume that a msg will not exceed 1024-byte
            byte[] buffer = new byte[1024];
            InputStream inputStream = mSocket.getInputStream();
            DataInputStream in = new DataInputStream(inputStream);
            while (true) {
                int nbyte = in.readInt();

                // 心跳包
                if (nbyte == 0) {
                    Log.i(TAG, "readResponse: heart beat received");
                    mUIHandler.removeCallbacks(mHeartBeatTimeoutTask);
                    mSeqNumHeartBeatRecv = mSeqNumHeartBeatSent;
                    continue;
                }

                if (nbyte > buffer.length) {
                    throw new IllegalStateException("Receive message with len " + nbyte + " which exceeds limit " + buffer.length);
                }

                if (readn(in, buffer, nbyte) != 0) {
                    // Socket might be closed twice but it does no harm
                    silentlyClose(mSocket);
                    // Socket will be re-connected by writer-thread if you want
                    break;
                }
                mDataCallback.onData(buffer, 0, nbyte);
            }
        }

        private int readn(InputStream in, byte[] buffer, int n) throws IOException {
            int offset = 0;
            while (n > 0) {
                int readBytes = in.read(buffer, offset, n);
                if (readBytes < 0) {
                    // EoF
                    break;
                }
                n -= readBytes;
                offset += readBytes;
            }
            return n;
        }
    }

    // 向服务端写数据
    public void write(byte[] data, WritingCallback callback) {
        write(data, 0, data.length, callback);
    }

    // 向服务端写数据
    public void write(byte[] data, int offset, int len, WritingCallback callback) {
        mWriterHandler.post(() -> {
            Socket socket = getSocket();
            if (socket == null) {
                // 心跳超时的情况下，这里 socket 会是 null
                initSocket();
                socket = getSocket();
                if (socket == null) {
                    if (!isClosed()) {
                        callback.onFail(data, offset, len);
                    } /* else {
                        // silently drop the data
                    } */
                    return;
                }
            }
            try {
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outputStream);
                out.writeInt(len);
                out.write(data, offset, len);
                callback.onSuccess();
            } catch (IOException e) {
                Log.e(TAG, "write: ", e);
                closeSocketLocked();
                callback.onFail(data, offset, len);
                if (!isClosed() && mErrorCallback.onError()) {
                    // 重连
                    initSocket();
                }
            }
        });
    }



    private boolean isClosed() {
        synchronized (mLock) {
            return isClosed;
        }
    }

    private Socket getSocket() {
        synchronized (mLock) {
            return mSocket;
        }
    }


    private void closeSocketLocked() {
        synchronized (mLock){
            if (mSocket == null) return;

            silentlyClose(mSocket);
            mSocket = null;
            mWriterHandler.removeCallbacks(mHeartBeatTask);
        }

    }

    public void close() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            new Thread() {
                @Override
                public void run() {
                    doClose();
                }
            }.start();
        } else {
            doClose();
        }
    }

    private void doClose() {
        synchronized (mLock) {
            isClosed = true;
            // 关闭 socket，从而使得阻塞在 socket 上的线程返回
            closeSocketLocked();
        }
        mWriterThread.quit();
        // 在重连的时候，有个 sleep
        mWriterThread.interrupt();
    }


    private static void silentlyClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.e(TAG, "silentlyClose: ", e);
            }
        }
    }





}
