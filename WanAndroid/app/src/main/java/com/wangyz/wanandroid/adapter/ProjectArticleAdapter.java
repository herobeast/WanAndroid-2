package com.wangyz.wanandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wangyz.wanandroid.ConstantValue;
import com.wangyz.wanandroid.R;
import com.wangyz.wanandroid.bean.db.Article;
import com.wangyz.wanandroid.bean.event.Event;
import com.wangyz.wanandroid.util.LoginUtil;
import com.wangyz.wanandroid.view.ArticleActivity;
import com.wangyz.wanandroid.view.LoginActivity;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wangyz
 * @time 2019/1/18 9:22
 * @description WxArticleAdapter
 */
public class ProjectArticleAdapter extends RecyclerView.Adapter<ProjectArticleAdapter.ViewHolder> {

    private Context mContext;

    private List<Article> mList = new ArrayList<>();

    private boolean mNightMode;

    public void setList(List<Article> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public ProjectArticleAdapter(Context context, List<Article> list) {
        mContext = context;
        mList.addAll(list);
        mNightMode = SPUtils.getInstance(ConstantValue.CONFIG_SETTINGS).getBoolean(ConstantValue.KEY_NIGHT_MODE, false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Article model = mList.get(i);
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.load_failed);
        Glide.with(mContext).load(model.pic).apply(requestOptions).into(viewHolder.pic);
        viewHolder.title.setText(StringEscapeUtils.unescapeHtml4(model.title));
        viewHolder.des.setText(StringEscapeUtils.unescapeHtml4(model.des));
        viewHolder.author.setText(StringEscapeUtils.unescapeHtml4(model.author));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(model.time);
        viewHolder.time.setText(sdf.format(date));
        if (!LoginUtil.isLogin()) {
            viewHolder.collect.setSelected(false);
        } else {
            viewHolder.collect.setSelected(model.collect);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ArticleActivity.class);
            intent.putExtra(ConstantValue.KEY_LINK, model.link);
            intent.putExtra(ConstantValue.KEY_TITLE, model.title);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });

        viewHolder.collect.setOnClickListener(v -> {
            if (!LoginUtil.isLogin()) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                Event event = new Event();
                event.target = Event.TARGET_PROJECT;
                event.type = model.collect ? Event.TYPE_UNCOLLECT : Event.TYPE_COLLECT;
                event.data = model.articleId + ";" + model.projectType;
                EventBus.getDefault().post(event);
            }

        });

        if (mNightMode) {
            viewHolder.cardView.setBackgroundColor(mContext.getResources().getColor(mNightMode ? R.color.card_night_bg : R.color.card_bg));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.item_project_list_pic)
        ImageView pic;
        @BindView(R.id.item_project_list_title)
        TextView title;
        @BindView(R.id.item_project_list_des)
        TextView des;
        @BindView(R.id.item_project_list_time)
        TextView time;
        @BindView(R.id.item_project_list_author)
        TextView author;
        @BindView(R.id.item_project_list_collect)
        ImageView collect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
