package com.anyonavinfo.commonuserregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends Activity {
    /**************************
     * 界面上所需要的字段
     ******************************/
    @InjectView(R.id.imageView_picture_right)
    ImageView userPictureRight;/*头像*/
    @InjectView(R.id.user_name)
    EditText edittext_userName;/*姓名*/
    @InjectView(R.id.nick_name)
    EditText edittext_nickname;/*昵称*/
    @InjectView(R.id.user_phone)
    EditText edittext_userPhone;/*手机*/
    @InjectView(R.id.birth_day)
    TextView edittext_birth;/*生日*/
    @InjectView(R.id.address)
    EditText edittext_address;/*地址*/
    @InjectView(R.id.e_mail)
    EditText edittext_email;/*邮箱*/
    @InjectView(R.id.car_number)
    EditText edittext_carnumber;/*车牌号*/
    @InjectView(R.id.user_vin)
    EditText edittext_userVin;/*车架号*/
    @InjectView(R.id.user_vehicleType)
    TextView edittext_userVehicleType;/*车型*/
    @InjectView(R.id.user_orgId)
    TextView edittext_userOrgId;/*4S店*/

    /**************************
     * 界面上所需要的字段的可触发
     ******************************/
   /* @InjectView(R.id.inquire)
    ImageButton btnInquire;*//*查询4S店代号*/
    @InjectView(R.id.user_ok)
    Button btnOk;/*注册提交*/
    /* @InjectView(R.id.user_carmera)
     ImageButton btn_carmear;*//*扫描车架号按钮*/
    @InjectView(R.id.back)
    Button back;/*顶部退出按钮*/
   /* @InjectView(R.id.checkBox)
    CheckBox check;*//*是否已阅读过五菱E100协议*/

    /**************************
     * 界面上所需要的字段变量
     ******************************/
    private String serialNumber;/*pad序列号*/
    private String name;/*用户姓名*/
    private String nick_name;/*用户昵称*/
    private String mobile;/*用户电话*/
    private String vehicleType;/*车型*/
    private String sim;/*sim卡*/
    private String simPhone;/*sim卡号*/
    private String orgId;/*4S店名称代号*/
    private String vin;/*车架号*/
    private String birthday;/*用户生日*/
    private String email;/*用户邮箱*/
    private String address;/*用户地址*/
    private String car_number;/*用户车牌号*/

    public static String result = "";/*接口回调服务器返回值*/
    public CallBack callback;/*接口回调对象*/
    private CheckReceiver checkReceiver;

    private static final String[] m = {"宝骏560", "宝骏730", "E100"};
    private ArrayAdapter<String> adapter;
    private String type = "register";

    private String picPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.inject(this);

        Utils util = new Utils(MainActivity.this);
        registerReceiver();/*注册广播*/
        edittext_birth.setInputType(InputType.TYPE_NULL); /*点击生日框不需要弹出软键盘*/

      /*  //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        edittext_userVehicleType.setAdapter(adapter);
        //  添加事件Spinner事件监听
        edittext_userVehicleType.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        edittext_userVehicleType.setVisibility(View.VISIBLE);*/

        orgId = edittext_userOrgId.getText().toString();
        getSimInfo(this);
    }


    /**
     * 点击生日框选择日期
     *
     * @param view
     */
    @OnClick(R.id.birth_day)
    public void showDate(View view) {
        if (!NoDoubleClick.isDoubleClick()) {
            new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String data = String.format("%d-%d-%d", year, (monthOfYear + 1), dayOfMonth);
                    System.out.println(data);
                    edittext_birth.setText(data);
                }
            }, 1992, 6, 2).show();
        } else {

        }
    }

    /**
     * 用户注册提交信息
     *
     * @param view
     */
    @OnClick(R.id.user_ok)
    public void submit(View view) {
        if (isConnected(MainActivity.this)) {
            Log.d("ddddddddddd", "submit");
            /*if (check.isChecked()) {*/
            if (!NoDoubleClick.isDoubleClick()) {
                upLoad();
            } else {
                // Toast.makeText(MainActivity.this, "您点击速度过快,请慢点", Toast.LENGTH_SHORT).show();
            }
            /*}else {
                Toast.makeText(MainActivity.this, "请先阅读《五菱E100用户协议》", Toast.LENGTH_SHORT).show();
            }*/

        } else {
            new AlertDialog.Builder(MainActivity.this).setTitle("错误")
                    .setMessage("请检查网络连接!")
                    .setPositiveButton("确定", null).show();
        }
    }

    /**
     * 用户查询4S店代号
     *
     * @param view
     */
    @OnClick(R.id.inquire)
    public void inquire(View view) {
        Intent intents = new Intent(MainActivity.this, ShopActivity.class);
        startActivityForResult(intents, 2);
    }

    /**
     * 选择车型
     *
     * @param view
     */
    @OnClick(R.id.user_vehicleType)
    public void selectVehicleType(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("请选择车型")
                .setItems(CommonData.USERINFO_CARMODEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edittext_userVehicleType.setText(CommonData.USERINFO_CARMODEL[which]);
                        edittext_userVehicleType.setTextSize(20);
                        edittext_userVehicleType.setTextColor(Color.BLACK);
                    }
                }).show();
    }

    /**
     * 扫描车架号
     *
     * @param view
     *//*
    @OnClick(R.id.user_carmera)
    public void userCamera(View view) {
        Intent camera = new Intent(MainActivity.this, QrcodeActivity.class);
        camera.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(camera, 1);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }*/

    /**
     * 用户选择本地图片作为头像
     *
     * @param view
     */
    @OnClick(R.id.imageView_picture_right)
    public void showLogo(View view) {
        if (!NoDoubleClick.isDoubleClick()) {
            onOpenImage();
        } else {
            return;
        }
    }

    /**
     * 打开本地图库
     */
    private void onOpenImage() {
        /***
         * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
         */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 3);
    }


    /**
     * 顶部导航栏推出按钮退出APP
     */
    @OnClick(R.id.back)
    public void exitApp(View view) {
        MainActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String content = bundle.getString("result");
            // 显示扫描到的内容
            if (!StringUtils.isEmpty(content)) {
                edittext_userVin.setText(content);
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String shopId = bundle.getString("ID");
            String shopName = bundle.getString("NAME");
            //Log.d("shopId", shopId);
            edittext_userOrgId.setText(shopName);
            orgId = shopId;
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            /**
             * 当选择的图片不为空的话，在获取到图片的途径
             */
            Uri uri = data.getData();
            Log.e("Sjj--->", "uri = " + uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 设置options.inJustDecodeBounds为true
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888

            try {
                //此时不会把图片读入内存，只会获取图片宽高等信息
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                // 设置options.inJustDecodeBounds重新设置为false
                options.inJustDecodeBounds = false;
                int w = options.outWidth;
                int h = options.outHeight;
                Log.i("path", "" + w + "" + h);

                float hh = 800f;//这里设置高度为800f
                float ww = 480f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (options.outWidth / ww);
                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (options.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                options.inSampleSize = be;//设置缩放比例
                Log.d("Size", be + "");

                //得到压缩后的图片
                Bitmap bitmapSize = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                Log.d("sizePic", bitmapSize.getWidth() + "" + bitmapSize.getHeight());

                /**
                 * 这里逻辑性很强，我的想法也一直被摧残，本来想不做压缩了，可仔细一想没有程序是一步到位的，最后总算给我想到了办法
                 * 整段代码到picPath=path,说明:先本地得到压缩的图片，调用系统方法保存到本地，并且转换成Uri然后通过cursor
                 * 寻找图片，再把uri转换成图片所对应的本地url。
                 */
                Uri sizePicUrl = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmapSize, null, null));
                Log.d("sizePicUrl", sizePicUrl + "");

                String[] pojoSizePic = {MediaStore.Images.Media.DATA};
                Cursor cursorSizePic = managedQuery(sizePicUrl, pojoSizePic, null, null, null);
                if (cursorSizePic != null) {
                    //ContentResolver cr = this.getContentResolver();
                    int colunm_index = cursorSizePic.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursorSizePic.moveToFirst();
                    String pathSizePic = cursorSizePic.getString(colunm_index);
                    picPath = pathSizePic;
                }
                /*********************************************************************************************/
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, pojo, null, null, null);
                if (cursor != null) {
                    //ContentResolver cr = this.getContentResolver();
                    int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(colunm_index);
                    Log.d("path", path);
                    /***
                     * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
                     * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
                     */
                    if (path.endsWith("jpg") || path.endsWith("png")) {
                        //picPath = path;
                        // Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        userPictureRight.setImageBitmap(bitmapSize);
                    } else {
                        alert();
                    }
                } else {
                    alert();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 图片选择提示框
     */
    private void alert() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        picPath = null;
                    }
                }).create();
        dialog.show();
    }


    public void getSimInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        serialNumber = getSerialNumber();
        simPhone = serialNumber;
        sim = serialNumber;
        Log.d("NO", serialNumber);
    }

    public static String getSerialNumber() {
        String serial = "000000000000000";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * Post方式上传注册信息
     */
    public void upLoad() {
        Log.d("dddddd", "begin");
        address = edittext_address.getText().toString();
        car_number = edittext_carnumber.getText().toString();
        birthday = edittext_birth.getText().toString();
        email = edittext_email.getText().toString();
        name = edittext_userName.getText().toString().trim();
        nick_name = edittext_nickname.getText().toString().trim();
        mobile = edittext_userPhone.getText().toString();
        vin = edittext_userVin.getText().toString();
        vehicleType = edittext_userVehicleType.getText().toString();

        if (!Utils.checkUserName(name) || !Utils.checkBirthday(birthday) || !Utils.checkmail(email)
                || !Utils.checkNickName(nick_name) || !Utils.checkMobile(mobile) || !Utils.checkType(vehicleType)
                || !Utils.checkVin(vin) || !Utils.check4S(orgId)) {
            Log.d("dddddd", "checkfail");
            return;

        } else {
            try {
                File file = null;
                if (picPath != null && !"".equals(picPath)) {
                    file = new File(picPath);
                }
                JSONObject jsonUser = new JSONObject();
                jsonUser.put("padSerialNo", serialNumber);
                jsonUser.put("userName", name);
                jsonUser.put("nickName", nick_name);
                jsonUser.put("address", address);
                jsonUser.put("mobile", mobile);
                jsonUser.put("birthday", birthday);
                jsonUser.put("mail", email);
                jsonUser.put("vehicleInfo", vehicleType);
                jsonUser.put("licenseNo", car_number);
                jsonUser.put("storeId", orgId);
                jsonUser.put("vinCode", vin);
                jsonUser.put("cmd", type);

                Log.d("dddddd", "success");

                String urlStr = HttpApi.URL_REGISTER + HttpApi.PADSERIANO + serialNumber
                        + HttpApi.USERNAME + URLEncoder.encode(name, "UTF-8")
                        + HttpApi.NICKNAME + URLEncoder.encode(nick_name, "UTF-8")
                        + HttpApi.ADDRESS + URLEncoder.encode(address, "UTF-8")
                        + HttpApi.MOBILE + mobile + HttpApi.BIRTHDAY + birthday + HttpApi.MAIL + email
                        + HttpApi.VEHICLEINFO + URLEncoder.encode(vehicleType, "UTF-8")
                        + HttpApi.LICENSENO + URLEncoder.encode(car_number, "UTF-8") + HttpApi.STOREID + orgId
                        + HttpApi.VINCODE + vin + HttpApi.CMD + type;

                Log.d("url", urlStr);

                doPostAsyn(urlStr, file, new CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        Log.i("Sjjj--->", result.toString());
                        String message = GetMessage(result);

                        if (message.equals("paramBlank")) {
                            mHandler.obtainMessage(new Message().what = 1)
                                    .sendToTarget();
                        } else if (message.equals("success")) {
                            mHandler.obtainMessage(new Message().what = 2)
                                    .sendToTarget();
                        } else if (message.equals("noData")) {
                            mHandler.obtainMessage(new Message().what = 3)
                                    .sendToTarget();
                        } else if (message.equals("noPad")) {
                            mHandler.obtainMessage(new Message().what = 4)
                                    .sendToTarget();
                        } else if (message.equals("failed")) {
                            mHandler.obtainMessage(new Message().what = 5)
                                    .sendToTarget();
                        } else if (message.equals("repeated")) {
                            mHandler.obtainMessage(new Message().what = 6)
                                    .sendToTarget();
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    /**
     * 注销广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (checkReceiver != null) {
            unregisterReceiver(checkReceiver);
        }
    }

    /**
     * 绑定回调显示
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                new AlertDialog.Builder(MainActivity.this).setTitle("错误！")
                        .setMessage("请输入完整的信息!").setPositiveButton("确定", null)
                        .show();
            } else if (msg.what == 2) {
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setTitle("提示！")
                        .setMessage("注册成功")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.this.finish();
                                    }
                                }).show();
            } else if (msg.what == 3) {
                new AlertDialog.Builder(MainActivity.this).setTitle("错误！")
                        .setMessage("错误的4s店代号，请点击查询正确的4s店代号")
                        .setPositiveButton("确定", null).show();
            } else if (msg.what == 4) {
                new AlertDialog.Builder(MainActivity.this).setTitle("错误！")
                        .setMessage("非法Pad，请使用上汽通用五菱认证的正版Pad")
                        .setPositiveButton("确定", null).show();
            } else if (msg.what == 5) {
                new AlertDialog.Builder(MainActivity.this).setTitle("错误！")
                        .setMessage("服务器异常").setPositiveButton("确定", null)
                        .show();
            } else if (msg.what == 6) {
                new AlertDialog.Builder(MainActivity.this).setTitle("错误！")
                        .setMessage("该Pad已经被注册").setPositiveButton("确定", null)
                        .show();
            }
        }
    };

    /**
     * 判断网络是否连接
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Post请求，获得返回数据
     */
    public static String doPost(String urlStr, File file) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型

        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestProperty("encoding", "utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if (file != null) {
                /**
                 * 当文件不为空时执行上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + "utf-8" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream IS = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = IS.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                IS.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
            }


            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                    Log.d("Sjj--->", len + "");
                }
                baos.flush();
                return baos.toString();
            } else {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e) {
            // 检测网络连接超时和网络连接异常
            if (e instanceof SocketTimeoutException) {
                return "SocketTimeoutException";
            } else if (e instanceof UnknownHostException) {
                return "UnknownHostException";
            }
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
            }
        }

        return null;
    }

    /**
     * 异步的Post请求
     */
    public static void doPostAsyn(final String urlStr, final File file, final CallBack callBack) {
        new Thread() {
            public void run() {
                try {
                    String result = doPost(urlStr, file);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * GSON解析用户注册从服务器返回的尾部字段信息
     *
     * @param word
     * @return
     */
    public String GetMessage(String word) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Result1>() {
        }.getType();
        Result1 results = gson.fromJson(word, type);
        String message = results.message;
        System.out.print("sjj---" + message);
        return message;
    }

    /**
     * 用户注册成功所返回的尾部字段信息
     */
    private class Result1 {
        String message = new String();
        String resultCode = new String();
    }

    /**
     * Post上传成功回调接口
     */
    public interface CallBack {
        void onRequestComplete(String result);
    }

   /* public void SetTypes() {
        final GetCarTypes types = new GetCarTypes();
        GetCarTypes.doGetAsyn(new CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i("tag", "tag");
                CarTypes carTypes = types.GetTypes(result);
                for (int i = 0; i < carTypes.Types.size(); i++) {
                    m[i] = carTypes.GetType(i);
                    Log.i("types", m[i]);
                }
            }
        });

        // 将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        // 设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将adapter 添加到spinner中
        edittext_userVehicleType.setAdapter(adapter);
        // 添加事件Spinner事件监听
        edittext_userVehicleType.setOnItemSelectedListener(new SpinnerSelectedListener());
        // 设置默认值
        edittext_userVehicleType.setVisibility(View.VISIBLE);
    }*/

    // 使用数组形式操作
   /* class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            vehicleType = m[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }*/

    /**
     * 注册广播
     */
    private void registerReceiver() {
        checkReceiver = new CheckReceiver();
        IntentFilter messageFilter = new IntentFilter();
        messageFilter.addAction(CommonData.MESSAGE_NAME_NULL);
        messageFilter.addAction(CommonData.MESSAGE_NAME_WRONG);
        messageFilter.addAction(CommonData.MESSAGE_MOBILE_NULL);
        messageFilter.addAction(CommonData.MESSAGE_MOBILE_WRONG);
        messageFilter.addAction(CommonData.MESSAGE_4S_NULL);
        messageFilter.addAction(CommonData.MESSAGE_VIN_NULL);
        messageFilter.addAction(CommonData.MESSAGE_VIN_WRONG);
        messageFilter.addAction(CommonData.MESSAGE_BIRTHDAY_NULL);
        messageFilter.addAction(CommonData.MESSAGE_EMAIL_NULL);
        messageFilter.addAction(CommonData.MESSAGE_EMAIL_WRONG);
        messageFilter.addAction(CommonData.MESSAGE_NICK_WRONG);
        messageFilter.addAction(CommonData.MESSAGE_NICK_NULL);
        messageFilter.addAction(CommonData.MESSAGE_TYPE_NULL);
        registerReceiver(checkReceiver, messageFilter);
    }

    /**
     * 重写onTouchEvent方法，实现点击焦点外地方隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }


    /**
     * 判断用户输入的字段
     */
    public class CheckReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonData.MESSAGE_NAME_NULL)) {
                Toast.makeText(MainActivity.this, "姓名一定要填哦!", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_NAME_WRONG)) {
                Toast.makeText(MainActivity.this, "姓名不合法，请重新输入！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_BIRTHDAY_NULL)) {
                Toast.makeText(MainActivity.this, "生日一定要填哦!", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_EMAIL_NULL)) {
                Toast.makeText(MainActivity.this, "E-mail一定要填哦!", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_EMAIL_WRONG)) {
                Toast.makeText(MainActivity.this, "E-mail错误，请重新输入！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_NICK_NULL)) {
                Toast.makeText(MainActivity.this, "昵称一定要填哦！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_NICK_WRONG)) {
                Toast.makeText(MainActivity.this, "昵称不合法，请重新输入！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_MOBILE_NULL)) {
                Toast.makeText(MainActivity.this, "手机号码一定要填哦！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_MOBILE_WRONG)) {
                Toast.makeText(MainActivity.this, "手机号码错误，请重新输入！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_TYPE_NULL)) {
                Toast.makeText(MainActivity.this, "车型一定要填哦！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_VIN_NULL)) {
                Toast.makeText(MainActivity.this, "车架号一定要填哦！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_VIN_WRONG)) {
                Toast.makeText(MainActivity.this, "车架号错误，请重新输入！", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CommonData.MESSAGE_4S_NULL)) {
                Toast.makeText(MainActivity.this, "4S店一定要选哦！", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
