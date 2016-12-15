package com.anyonavinfo.commonuserregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hzj4135
 * @TODO 车架号验证（315用户需验证车架号，验证通过才能注册）
 * @创建时间：2014-8-6 下午3:42:52
 */
public class CarNumValidateActivity extends Activity implements OnClickListener {
    private EditText car_num;
    private Button camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carnumvalidate);
        AppManager.getAppManager().addActivity(this);
        initView();
    }

    private void initView() {
        car_num = (EditText) findViewById(R.id.car_num);
        camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                Intent camera = new Intent(CarNumValidateActivity.this,
                        QrcodeActivity.class);
                camera.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // camera.putExtra("screen", 1);
                startActivityForResult(camera, 1);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String content = bundle.getString("result");
            // 显示扫描到的内容
            if (!StringUtils.isEmpty(content)) {
                car_num.setText(content);
            }
        }
    }

}
