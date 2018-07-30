package com.cainam.tuchat.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.cainam.tuchat.R;
import com.cainam.tuchat.fragment.ChatFragment;
import com.cainam.tuchat.fragment.FollowFragment;
import com.cainam.tuchat.fragment.FriendFragment;

import static com.cainam.tuchat.R.string.friend;

/**
 * Created by cainam on 8/17/17.
 */

public class TabPagerMenuAdapter extends FragmentPagerAdapter{

    FragmentManager fmanager;

    public TabPagerMenuAdapter(FragmentManager fm) {
        super(fm);
        fmanager = fm;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return "Chats";
            case 1:
                return "Friends";
            case 2:
                return "Follows";
            default:
                return null;
        }

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                FriendFragment friendFragment = new FriendFragment();
                return friendFragment;
            case 2:
                FollowFragment followFragment = new FollowFragment();
                return followFragment;
            default:
                return null;

        }
    }
}
