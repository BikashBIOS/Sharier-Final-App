package com.example.shareer;

import android.view.View;

public interface ItemClickListener {

    /**
     * This is a Item click interface used in adapter
     * @param view
     * @param position
     * @param isLongClick
     */
    void onClick(View view, int position, boolean isLongClick);

}
