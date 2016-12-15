package com.anyonavinfo.commonuserregister;

/**
 * Created by shijj on 2016/5/4.
 */
public class HttpApi {
    //用户注册信息提交接口
    //生产
    public static final String URL_REGISTER = "http://vehicle.chexiang.com/vehicle/user.action?";

    //test(sit)
    //public static final String URL_REGISTER = "http://10.32.174.198:8080/vehicle/user.action?";

    //test(pre)
    // public static final String URL_REGISTER = "http://10.32.190.197:8080/vehicle/user.action?";
    public static final String PADSERIANO = "padSerialNo=";
    public static final String USERNAME = "&userName=";
    public static final String NICKNAME = "&nickName=";
    public static final String ADDRESS = "&address=";
    public static final String MOBILE = "&mobile=";
    public static final String BIRTHDAY = "&birthday=";
    public static final String MAIL = "&mail=";
    public static final String VEHICLEINFO = "&vehicleInfo=";
    public static final String LICENSENO = "&licenseNo=";
    public static final String STOREID = "&storeId=";
    public static final String VINCODE = "&vinCode=";
    public static final String CMD = "&cmd=";

    //用户注册4S店选择接口
    //生产
    public static final String URL_MAIN = "http://vehicle.chexiang.com/vehicle/manuStore.action?";

    //test(sit)
    //public static final String URL_MAIN = "http://10.32.174.198:8080/vehicle/manuStore.action?";

    //test(pre)
    //public static final String URL_MAIN = "http://10.32.190.197:8080/vehicle/manuStore.action?";
    public static final String CMD_PROVINCE = "cmd=findProvinceByBrandId";
    public static final String BRANDID = "&brandId=9";
    public static final String CMD_CITY = "cmd=findCityByBrandIdAndProvinceId";
    public static final String PROVINCECODE = "&provinceId=";

}
