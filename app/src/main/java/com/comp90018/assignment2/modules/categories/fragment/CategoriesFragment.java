package com.comp90018.assignment2.modules.categories.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.recyclerview.widget.RecyclerView;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.modules.categories.adapter.CategoryLeftAdapter;
import com.comp90018.assignment2.modules.categories.adapter.CategoryRightAdapter;

public class CategoriesFragment extends BaseFragment {

    private ListView ct_left;
    private RecyclerView ct_right;
    private CategoryLeftAdapter leftAdapter;
    boolean isFirst = true;
    private final String[] categories = new String[]{"小裙子", "上衣", "下装", "外套", "配件", "包包", "装扮", "居家宅品"};
    private final String[] subcategories = new String[]{"sub小裙子", "sub上衣", "sub下装", "sub外套", "sub配件", "sub包包", "sub装扮", "sub居家宅品"};

    @Override
    public View inflateView() {
        /*
         * 当你实际写出这个fragment的layout文件后，用View view = View.inflate(mContext, R.layout.fragment_XXX, null); 绑定
         * 然后该各fragment的各种view可以通过view.findViewById()来绑定
         * 然后return view.
         * */
        View view = View.inflate(activityContext, R.layout.fragment_categories, null);
        ct_left = (ListView) view.findViewById(R.id.ct_left);
        ct_right = (RecyclerView) view.findViewById(R.id.ct_right);
        return view;
    }

    @Override
    public void loadData() {
        /* 实际上，这个方法会从网上请求数据，然后你要把数据在这个方法里装到对应的view里 */
        // TODO: set data
        leftAdapter = new CategoryLeftAdapter(activityContext, categories);
        ct_left.setAdapter(leftAdapter);
        initListener(leftAdapter);
        CategoryRightAdapter rightAdapter = new CategoryRightAdapter(activityContext, subcategories);
        ct_right.setAdapter(rightAdapter);
    }

    private void initListener(final CategoryLeftAdapter adapter) {
        ct_left.setOnItemClickListener((parent, view, position, id) -> {
            adapter.changeSelected(position);//刷新
            if (position != 0) {
                isFirst = false;
            }
            leftAdapter.notifyDataSetChanged();
        });

        ct_left.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
