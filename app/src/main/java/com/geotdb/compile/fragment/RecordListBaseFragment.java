/*
 * Copyright (C) 2015 The Android Open Source Hole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geotdb.compile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geotdb.compile.R;

public class RecordListBaseFragment extends Fragment {

    public static final String KEY_HOLE_ID = "RecordListBaseFragment:holeID";
    RecyclerView recyclerView;
    String holeID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(KEY_HOLE_ID)) {
            holeID = getArguments().getString(KEY_HOLE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.frt_record_list, container, false);
        recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerView);
        setupRecyclerView(recyclerView);
        return convertView;
    }

    public void onRefreshList() {

    }


    public void setupRecyclerView(RecyclerView recyclerView) {

    }
}
