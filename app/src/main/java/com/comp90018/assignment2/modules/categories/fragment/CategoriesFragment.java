package com.comp90018.assignment2.modules.categories.fragment;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.dto.CategoryDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;
import com.comp90018.assignment2.modules.categories.adapter.CategoryLeftAdapter;
import com.comp90018.assignment2.modules.categories.adapter.CategoryRightAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends BaseFragment {
    FirebaseFirestore db;
    private ListView ct_left;
    private RecyclerView ct_right;
    private CategoryLeftAdapter leftAdapter;
    boolean isFirst = true;
    private final static String TAG = "CategoriesFragment";
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
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.CATEGORIES_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<CategoryDTO> categories = new ArrayList<>();
                for (QueryDocumentSnapshot document: task.getResult()) {
                    categories.add(document.toObject(CategoryDTO.class));
                }
                leftAdapter = new CategoryLeftAdapter(activityContext, categories);
                ct_left.setAdapter(leftAdapter);
                initListener(leftAdapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        db.collection(Constants.SUB_CATEGORIES_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<SubCategoryDTO> subcategories = new ArrayList<>();
                for (QueryDocumentSnapshot document: task.getResult()) {
                    subcategories.add(document.toObject(SubCategoryDTO.class));
                    CategoryRightAdapter rightAdapter = new CategoryRightAdapter(activityContext, subcategories);
                    ct_right.setAdapter(rightAdapter);
                }
                CategoryRightAdapter rightAdapter = new CategoryRightAdapter(activityContext, subcategories);
                ct_right.setAdapter(rightAdapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
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
