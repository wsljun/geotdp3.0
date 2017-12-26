package com.geotdb.compile.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.slide.AbstractSlideExpandableListAdapter;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Hole;

import net.qiujuer.genius.ui.widget.Button;

import java.util.List;

import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/3/23.
 */
public class HoleListAdapter extends AbstractSlideExpandableListAdapter<HoleListAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<Hole> mValues;
    private Context context;
    private FragmentActivity activity;

    public HoleListAdapter(Context context, List<Hole> items) {
        this.context = context;
        this.activity = (FragmentActivity) context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_hole, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rv, final int position) {
        super.onBindViewHolder(rv, position);
        final ViewHolder holder = (ViewHolder) rv;
        try {
            holder.vo = mValues.get(position);
            holder.mpr.setUseIntrinsicPadding(false);

            //随机获取颜色，从颜色组里面取
            int[] androidColors = context.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[position % 7];

            holder.btnDetail.setBackgroundColor(randomAndroidColor);
            holder.btnHoleList.setBackgroundColor(randomAndroidColor);
            holder.btnReport.setBackgroundColor(randomAndroidColor);
            holder.btnEdit.setBackgroundColor(randomAndroidColor);
            holder.btnUpload.setBackgroundColor(randomAndroidColor);
            holder.btnDelete.setBackgroundColor(randomAndroidColor);
            holder.ivwInfo_rl.setBackgroundColor(randomAndroidColor);
            holder.hole_line.setBackgroundColor(randomAndroidColor);

//            holder.mpr.setShowTrack(false);
            //状态
            final int state = Integer.parseInt(holder.vo.getState());
            final int locationState = Integer.parseInt(holder.vo.getLocationState());

            int uploadedCount = holder.vo.getUploadedCount();          //已上传
            int notUploadCount = holder.vo.getNotUploadCount();         //未上传
            int count = uploadedCount + notUploadCount;         // 总条数
            if (uploadedCount != 0) {
                int progress = Integer.valueOf(uploadedCount * 100 / count);
                holder.mpr.setProgress(progress);
                if (progress == 100) {
                    holder.btnUpload.setEnabled(false);
                } else {
                    holder.btnUpload.setEnabled(true);
                }
                holder.btnUpload.setText("上传(" + progress + "%)");
            } else {//上传数==0
                holder.mpr.setProgress(0);
                holder.btnUpload.setText("上传");
                holder.btnUpload.setEnabled(notUploadCount != 0);
            }
            holder.mpr.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));

            if (locationState == 1) {
                holder.tvwState.setText("未开始");
                holder.tvwState.setTextColor(context.getResources().getColor(R.color.md_material_gray_800));
            } else if (locationState == 0 && uploadedCount == 0) {
                holder.tvwState.setText("未上传");
                holder.tvwState.setTextColor(context.getResources().getColor(R.color.met_primaryColor));
            } else if (locationState == 0 && uploadedCount < count) {
                holder.tvwState.setText("未传完");
                holder.tvwState.setTextColor(context.getResources().getColor(R.color.met_primaryColor));
            } else if (locationState == 0 && uploadedCount == count) {
                holder.tvwState.setText("已上传");
                holder.tvwState.setTextColor(context.getResources().getColor(R.color.met_primaryColor));
            }

            //isRelated存放的是关联获取的holeID
            if (holder.vo.getRelateID() != null && !"".equals(holder.vo.getRelateID())) {
                holder.tvIsRelated.setText("已关联");
                holder.tvwCode.setText(holder.vo.getRelateCode() + "(" + holder.vo.getCode() + ")");
                holder.tvIsRelated.setTextColor(context.getResources().getColor(R.color.met_primaryColor));
            } else {
                holder.tvIsRelated.setText("未关联");
                holder.tvwCode.setText(holder.vo.getCode());
                holder.tvIsRelated.setTextColor(context.getResources().getColor(R.color.md_material_gray_800));
            }
            holder.tvwType.setText(holder.vo.getType());
            if (null != holder.vo.getCurrentDepth() && !"null".equals(holder.vo.getCurrentDepth())) {
                holder.tvwCurrentDepth.setText("深度:" + holder.vo.getCurrentDepth() + "m");
            } else {
                holder.tvwCurrentDepth.setText("深度:0m");
            }

            if (holder.vo.getUserID() == null || holder.vo.getUserID().equals("")) {
                holder.hole_item_rec.setText("");
            } else {
                if (!holder.vo.getUserID().equals(Common.getUserIDBySP(context))) {
                    holder.btnUpload.setVisibility(View.GONE);
                    holder.btnEdit.setVisibility(View.GONE);
                    holder.btnHoleList.setVisibility(View.GONE);
                    holder.hole_item_rec.setText("获取的,只查看");
                } else {
                    holder.hole_item_rec.setText("获取的,可编辑");
                }
            }

            holder.tvwUpdateTime.setText(holder.vo.getUpdateTime());
            holder.tvwRecordsCount.setText(holder.vo.getRecordsCount());
            //记录列表
            holder.ivwInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.infoClick(position);
                }
            });

            //详情
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.detailClick(position);
                }
            });

            //记录列表
            holder.btnHoleList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.holeListClick(position);
                }
            });
            //记录浏览
            holder.btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.reportClick(position);
                }
            });
            //编辑
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.editClick(position);
                }
            });
            //上传
            holder.btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.uploadClick(position);

                }
            });
            // 删除
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.deleteClick(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void infoClick(int position);

        void detailClick(int position);

        void holeListClick(int position);

        void reportClick(int position);

        void editClick(int position);

        void uploadClick(int position);

        void deleteClick(int position);
    }


    @Override
    public View getExpandToggleButton(View parent) {
        return parent.findViewById(R.id.lltItem);
    }

    @Override
    public View getExpandableView(View parent) {
        return parent.findViewById(R.id.expandable);
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Hole vo;

        public final View mView;
        public final TextView tvwCode;
        public final TextView tvwType;
        public final TextView tvwCurrentDepth;
        public final TextView tvwUpdateTime;
        public final TextView tvwRecordsCount;
        public final Button ivwInfo;
        public final MaterialProgressBar mpr;

        public final Button btnDetail;

        public final Button btnHoleList;
        public final Button btnReport;
        public final Button btnEdit;
        public final Button btnUpload;
        public final Button btnDelete;

        public final TextView tvwState;
        public final TextView tvIsRelated;
        public final RelativeLayout ivwInfo_rl;
        public final View hole_line;
        public final TextView hole_item_rec;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mpr = (MaterialProgressBar) view.findViewById(R.id.mpr);
            tvwCode = (TextView) view.findViewById(R.id.tvwCode);
            tvwType = (TextView) view.findViewById(R.id.tvwType);
            tvwCurrentDepth = (TextView) view.findViewById(R.id.tvwCurrentDepth);
            tvwUpdateTime = (TextView) view.findViewById(R.id.tvwUpdateTime);
            tvwRecordsCount = (TextView) view.findViewById(R.id.tvwRecordsCount);
            ivwInfo = (Button) view.findViewById(R.id.ivwInfo);
            ivwInfo_rl = (RelativeLayout) view.findViewById(R.id.ivwInfo_rl);

            btnDetail = (Button) view.findViewById(R.id.btnDetail);
            btnHoleList = (Button) view.findViewById(R.id.btnHoleList);
            btnReport = (Button) view.findViewById(R.id.btnReport);
            btnEdit = (Button) view.findViewById(R.id.btnEdit);
            btnUpload = (Button) view.findViewById(R.id.btnUpload);
            btnDelete = (Button) view.findViewById(R.id.btnDelete);
            hole_line = view.findViewById(R.id.hole_line);

            tvwState = (TextView) view.findViewById(R.id.tvwState);
            tvIsRelated = (TextView) view.findViewById(R.id.tvIsRelated);

            hole_item_rec = (TextView) view.findViewById(R.id.hole_item_rec);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + tvwCode.getText();
        }
    }

}
