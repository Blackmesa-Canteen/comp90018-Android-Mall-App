package com.comp90018.assignment2.config;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;

/**
 * video data source for local video playing
 *
 * Be used by yjPlay video player module
 *
 * @author xiaotian li
 */
public class VideoDataSource implements DataSourceListener {
    public static final String TAG = "VideoDataSourceLocal";
    private Context context;

    public VideoDataSource(Context context) {
        this.context = context;
    }

    @Override
    public DataSource.Factory getDataSourceFactory() {
        return new DefaultDataSourceFactory(context, context.getPackageName());
    }
}
