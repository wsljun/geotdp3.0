package com.geotdb.compile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geotdb.compile.R;
import com.geotdb.compile.activity.LoginActivity;
import com.geotdb.compile.utils.ToastUtil;

import net.qiujuer.genius.ui.widget.Button;

/**
 * Created by Administrator on 2017/6/9.
 */
public class NotLoginFramgment extends Fragment {
    private Button login_null_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frt_notlogin, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        login_null_btn = (Button) view.findViewById(R.id.login_null_btn);
        login_null_btn.setOnClickListener(goLoginListener);
    }

    View.OnClickListener goLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    };

}
