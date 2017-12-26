package com.geotdb.compile.utils;

import java.util.concurrent.Callable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public class AsyncUtil {
    /**
     * 基本被调用的 (int)
     *
     * @param <T>
     * @param pContext
     * @param pTitleResID
     *            标题 :String 的资源文件
     * @param pMessageResID
     *            提示内容 :String 的资源文件
     * @param pCallable
     *            调用时的方法
     * @param pCallback
     *            调用后的方法
     */
    public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback) {
        AsyncUtil.doAsync(pContext, pTitleResID, pMessageResID, pCallable, pCallback, null, false);
    }

    /**
     * 基本被调用的 (CharSequence)
     *
     * @param <T>
     * @param pContext
     * @param pTitle
     *            标题 :String 的资源文件
     * @param pMessage
     *            提示内容 :String 的资源文件
     * @param pCallable
     *            调用时的方法
     * @param pCallback
     *            调用后的方法
     */
    public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback) {
        AsyncUtil.doAsync(pContext, pTitle, pMessage, pCallable, pCallback, null, false);
    }

    /**
     * 这个方法可以控制弹出框是否显示(int)
     *
     * @param <T>
     * @param pContext
     * @param pTitleResID
     * @param pMessageResID
     * @param pCallable
     * @param pCallback
     * @param pCancelable
     *            是否阻止弹出框的显示(默认为:否false)
     */
    public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final boolean pCancelable) {
        AsyncUtil.doAsync(pContext, pTitleResID, pMessageResID, pCallable, pCallback, null, pCancelable);
    }

    /**
     * 这个方法可以控制弹出框是否显示(CharSequence)
     *
     * @param <T>
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pCallable
     * @param pCallback
     * @param pCancelable
     *            是否阻止弹出框的显示
     */
    public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final boolean pCancelable) {
        AsyncUtil.doAsync(pContext, pTitle, pMessage, pCallable, pCallback, null, pCancelable);
    }

    /**
     * 这个方法可以设置弹出框取消按钮的回调(int)
     *
     * @param <T>
     * @param pContext
     * @param pTitleResID
     * @param pMessageResID
     * @param pCallable
     * @param pCallback
     * @param pExceptionCallback
     *            取消操作的 回调
     */
    public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
        AsyncUtil.doAsync(pContext, pTitleResID, pMessageResID, pCallable, pCallback, pExceptionCallback, false);
    }

    /**
     * 这个方法可以设置弹出框取消按钮的回调(CharSequence)
     *
     * @param <T>
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pCallable
     * @param pCallback
     * @param pExceptionCallback
     *            取消操作的 回调
     */
    public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
        AsyncUtil.doAsync(pContext, pTitle, pMessage, pCallable, pCallback, pExceptionCallback, false);
    }

    /**
     * 将int的参数转为String的函数
     *
     * @param <T>
     * @param pContext
     * @param pTitleResID
     * @param pMessageResID
     * @param pCallable
     * @param pCallback
     * @param pExceptionCallback
     * @param pCancelable
     *            是否阻止弹出框的显示
     */
    public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback, final boolean pCancelable) {
        AsyncUtil.doAsync(pContext, pContext.getString(pTitleResID), pContext.getString(pMessageResID), pCallable, pCallback, pExceptionCallback, pCancelable);
    }

    /**
     * 主封装
     *
     * @param <T>
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pCallable
     * @param pCallback
     * @param pExceptionCallback
     * @param pCancelable
     */
    public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback, final boolean pCancelable) {
        new AsyncTask<Void, Void, T>() {
            private ProgressDialog mPD;
            private Exception mException = null;

            @Override
            public void onPreExecute() {
                this.mPD = ProgressDialog.show(pContext, pTitle, pMessage, true, pCancelable);
                if (pCancelable) {
                    this.mPD.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface pDialogInterface) {
                            pExceptionCallback.onCallback(new CancelledException());
                            pDialogInterface.dismiss();
                        }
                    });
                }
                super.onPreExecute();
            }

            @Override
            public T doInBackground(final Void... params) {
                try {
                    return pCallable.call();
                } catch (final Exception e) {
                    this.mException = e;
                }
                return null;
            }

            @Override
            public void onPostExecute(final T result) {
                try {
                    this.mPD.dismiss();
                } catch (final Exception e) {
                    L.e("Error", e.toString());
                }
                if (this.isCancelled()) {
                    this.mException = new CancelledException();
                }
                if (this.mException == null) {
                    pCallback.onCallback(result);
                } else {
                    if (pExceptionCallback == null) {
                        if (this.mException != null)
                            L.e("Error", this.mException.toString());
                    } else {
                        pExceptionCallback.onCallback(this.mException);
                    }
                }
                super.onPostExecute(result);
            }
        }.execute((Void[]) null);
    }

    public static <T> void doProgressAsync(final Context pContext, final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback) {
        AsyncUtil.doProgressAsync(pContext, pTitleResID, pCallable, pCallback, null);
    }

    /**
     * 显示一个进度条真实的请求
     *
     * @param <T>
     * @param pContext
     * @param pTitleResID
     * @param pCallable
     * @param pCallback
     * @param pExceptionCallback
     */
    public static <T> void doProgressAsync(final Context pContext, final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
        new AsyncTask<Void, Integer, T>() {
            private ProgressDialog mPD;
            private Exception mException = null;

            @Override
            public void onPreExecute() {
                this.mPD = new ProgressDialog(pContext);
                this.mPD.setTitle(pTitleResID);
                this.mPD.setIcon(android.R.drawable.ic_menu_save);
                this.mPD.setIndeterminate(false);
                this.mPD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                this.mPD.show();
                super.onPreExecute();
            }

            @Override
            public T doInBackground(final Void... params) {
                try {
                    return pCallable.call(new IProgressListener() {
                        @Override
                        public void onProgressChanged(final int pProgress) {
                            onProgressUpdate(pProgress);
                        }
                    });
                } catch (final Exception e) {
                    this.mException = e;
                }
                return null;
            }

            @Override
            public void onProgressUpdate(final Integer... values) {
                this.mPD.setProgress(values[0]);
            }

            @Override
            public void onPostExecute(final T result) {
                try {
                    this.mPD.dismiss();
                } catch (final Exception e) {
                    L.e("Error", e.getLocalizedMessage());
					/* Nothing. */
                }
                if (this.isCancelled()) {
                    this.mException = new CancelledException();
                }
                if (this.mException == null) {
                    pCallback.onCallback(result);
                } else {
                    if (pExceptionCallback == null) {
                        L.e("Error", this.mException.getLocalizedMessage());
                    } else {
                        pExceptionCallback.onCallback(this.mException);
                    }
                }
                super.onPostExecute(result);
            }
        }.execute((Void[]) null);
    }

    /**
     * 这个方法没弄明白是做什么的
     *
     * @param <T>
     * @param pContext
     * @param pTitleResID
     * @param pMessageResID
     * @param pAsyncCallable
     * @param pCallback
     * @param pExceptionCallback
     */
    public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final AsyncCallable<T> pAsyncCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
        final ProgressDialog pd = ProgressDialog.show(pContext, pContext.getString(pTitleResID), pContext.getString(pMessageResID));
        pAsyncCallable.call(new Callback<T>() {
            @Override
            public void onCallback(final T result) {
                try {
                    pd.dismiss();
                } catch (final Exception e) {
                    L.e("Error", e.getLocalizedMessage());
					/* Nothing. */
                }
                pCallback.onCallback(result);
            }
        }, pExceptionCallback);
    }

    public static class CancelledException extends Exception {
        private static final long serialVersionUID = -78123211381435595L;
    }


    public interface AsyncCallable<T> {
        // ===========================================================
        // Final Fields
        // ===========================================================
        // ===========================================================
        // Methods
        // ===========================================================
        /**
         * Computes a result asynchronously, return values and exceptions are to
         * be handled through the callbacks. This method is expected to return
         * almost immediately, after starting a {@link Thread} or similar.
         *
         * @return computed result
         * @throws Exception
         *             if unable to compute a result
         */
        public void call(final Callback<T> pCallback, final Callback<Exception> pExceptionCallback);
    }

    public interface Callback<T> {
        // ===========================================================
        // Final Fields
        // ===========================================================
        // ===========================================================
        // Methods
        // ===========================================================
        /**
         * 当加载完成后回调，加载完毕的事后处理
         */
        public void onCallback(final T pCallbackValue);
    }

    public interface IProgressListener {
        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Methods
        // ===========================================================
        /**
         * @param pProgress
         *            between 0 and 100.
         */
        public void onProgressChanged(final int pProgress);
    }

    public interface ProgressCallable<T> {
        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Methods
        // =========================================================s==
        /**
         * @param pListener
         *            between 0 and 100.
         */
        public T call(final IProgressListener pListener);
    }

}
