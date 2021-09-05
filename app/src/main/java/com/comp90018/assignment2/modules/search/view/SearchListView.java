package com.comp90018.assignment2.modules.search.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author xiaotian
 *
 * based on opensource project Carson-Ho/Search_Layout
 * https://github.com/Carson-Ho/Search_Layout
 */
public class SearchListView extends ListView {
    public SearchListView(Context context) {
        super(context);
    }

    public SearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // achieve the effect of ScrollView adaptation
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
