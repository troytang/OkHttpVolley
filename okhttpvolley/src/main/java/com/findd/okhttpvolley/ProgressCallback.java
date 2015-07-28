package com.findd.okhttpvolley;

import java.io.File;

/**
 * Created by Troy Tang on 2015/7/24.
 */
public interface ProgressCallback {

    /**
     * ProgressCall 空对象，什么都不做
     */
    ProgressCallback NONE = new ProgressCallback() {
        @Override
        public void pregress(long current, long total) {

        }

        @Override
        public void done(File file) {

        }

        @Override
        public void error(Exception error) {

        }

        @Override
        public void cancel(File fileCanceled) {

        }
    };

    void pregress(long current, long total);

    void done(File file);

    void error(Exception error);

    void cancel(File fileCanceled);
}
