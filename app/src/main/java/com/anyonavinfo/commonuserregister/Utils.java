package com.anyonavinfo.commonuserregister;

import android.content.Context;
import android.content.Intent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Context mContext;

    public Utils(Context context) {
        mContext = context;
    }

    /**
     * @param type
     */
    private static void mySendBroadcast(String type) {
        Intent intent = new Intent();
        intent.setAction(type);
        mContext.sendBroadcast(intent);
    }

    /**
     * 检查EMAIL地址 用户名和网站名称必须>=1位字符
     * 地址结尾必须是以com|cn|com|cn|net|org|gov|gov.cn|edu|edu.cn结尾
     */
    public static Boolean checkmail(String strEmail) {
        if (strEmail.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_EMAIL_NULL);
            return false;
        } else {
            Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher m = p.matcher(strEmail);
            if (m.matches() == true) {
                return true;
            } else {
                mySendBroadcast(CommonData.MESSAGE_EMAIL_WRONG);
                return false;
            }
        }
    }

    /**
     * 检验姓名 取值范围为a-z,A-Z,0-9,"_",汉字 最少一位字符，最大字符位数无限制，不能以"_"结尾
     */
    public static Boolean checkUserName(String name) {
        if (name.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_NAME_NULL);
            return false;
        } else {
            Pattern p = Pattern.compile("^([\\u4e00-\\u9fa5]+|([a-z]+\\s?)+)$");
            Matcher m = p.matcher(name);
            if (m.matches() == true) {
                return true;
            } else {
                mySendBroadcast(CommonData.MESSAGE_NAME_WRONG);
                return false;
            }
        }
    }

    /**
     * 检验昵称 取值范围为a-z,A-Z,0-9,"_",汉字 最少一位字符，最大字符位数无限制，不能以"_"结尾
     */
    public static Boolean checkNickName(String nickname) {
        if (nickname.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_NICK_NULL);
            return false;
        } else {
            Pattern p = Pattern.compile("^[\\u4E00-\\u9FA5A-Za-z0-9_]+$");
            Matcher m = p.matcher(nickname);
            if (m.matches() == true) {
                return true;
            } else {
                mySendBroadcast(CommonData.MESSAGE_NICK_WRONG);
                return false;
            }
        }
    }


    /**
     * 检查手机号，不为空，以及合法性
     */
    public static Boolean checkMobile(String strMobile) {
        if (strMobile.length() == 0) {
            mySendBroadcast(CommonData.MESSAGE_MOBILE_NULL);
            return false;
        } else {
            Pattern p = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
            Matcher m = p.matcher(strMobile);
            if (m.matches() == true) {
                return true;
            } else {
                mySendBroadcast(CommonData.MESSAGE_MOBILE_WRONG);
                return false;
            }
        }
    }

    /**
     * 检查4S店名是否为空
     */
    public static Boolean check4S(String ssssName) {
        if (ssssName.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_4S_NULL);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查车型是否为空
     */
    public static Boolean checkType(String type) {
        if (type.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_TYPE_NULL);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查车架号是否为空
     */
    public static Boolean checkVin(String vinCode) {
        if (vinCode.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_VIN_NULL);
            return false;
        } else {
            Pattern p1 = Pattern.compile("[A-Z|a-z|0-9]{17}");
            Pattern p2 = Pattern.compile("[0-9]{17}");
            Pattern p3 = Pattern.compile("[a-zA-Z]{17}");
            Matcher m1 = p1.matcher(vinCode);
            Matcher m2 = p2.matcher(vinCode);
            Matcher m3 = p3.matcher(vinCode);
            if (m1.matches() == true & m2.matches() != true & m3.matches() != true) {
                return true;
            } else {
                mySendBroadcast(CommonData.MESSAGE_VIN_WRONG);
                return false;
            }
        }
    }

    /**
     * 检查生日是否为空
     *
     * @param birthday
     * @return
     */
    public static Boolean checkBirthday(String birthday) {
        if (birthday.isEmpty()) {
            mySendBroadcast(CommonData.MESSAGE_BIRTHDAY_NULL);
            return false;
        } else {
            return true;
        }
    }
}