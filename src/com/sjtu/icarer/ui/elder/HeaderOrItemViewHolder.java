package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.view.superslim.LayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HeaderOrItemViewHolder {

        private final RecyclerView mRecyclerView;


        public HeaderOrItemViewHolder(View view) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        }

        public void initViews(LayoutManager lm) {
            mRecyclerView.setLayoutManager(lm);
        }

        public void scrollToPosition(int position) {
            mRecyclerView.scrollToPosition(position);
        }

        public void setAdapter(RecyclerView.Adapter<?> adapter) {
            mRecyclerView.setAdapter(adapter);
        }

        public void smoothScrollToPosition(int position) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }