package com.selvia.quiz20.Adpt;

import com.twitter.sdk.android.core.models.User;

/**
 * Created by Administrator on 2016-07-21.
 */
public class ItemUserDetail {
    public User user;
    public int win_count=0, lose_count=0;
    public long user_key;
    public String user_id, user_name;
    public String profileImgUrl;


    //Global.user.lang 내 상태메세지
    //Global.user.screenName 아이디
    public ItemUserDetail(User user)
    {
        this.user = user;
        user_key = user.id;
        user_name = user.name;
        user_id = user.screenName;
        profileImgUrl = user.profileImageUrl;
    }

    public ItemUserDetail(String id, String name, String img )
    {
        user_name = name;
        user_id = id;
        profileImgUrl = img;
    }
}
