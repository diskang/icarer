package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.R;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Simple view holder for a single text view.
 */
class ItemSectionViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextView;

    ItemSectionViewHolder(View view) {
        super(view);

        mTextView = (TextView) view.findViewById(R.id.items_head_text);
    }

    public void bindItem(String text) {
        mTextView.setText(text);
    }

    @Override
    public String toString() {
        return mTextView.getText().toString();
    }
}
