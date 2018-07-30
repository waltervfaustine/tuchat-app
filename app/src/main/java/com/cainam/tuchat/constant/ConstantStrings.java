package com.cainam.tuchat.constant;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.bumptech.glide.load.engine.Resource;
import com.cainam.tuchat.R;

/**
 * Created by cainam on 9/11/17.
 */

public class ConstantStrings extends Application{

    Resources resources;

    public String CHAT = resources.getString(R.string.chat);
    public String FRIEND = resources.getString(R.string.friend);
    public String FOLLOW = resources.getString(R.string.follow);

}
