package com.comp90018.assignment2.modules.orders.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.comp90018.assignment2.App;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityEditProductBinding;
import com.comp90018.assignment2.dto.CategoryDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.publish.activity.PublishProductActivity;
import com.comp90018.assignment2.modules.publish.adapter.PictureCollectionAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EditProductActivity activity
 *
 * Need input a productDTO
 *
 * @author Ziyuan Xu
 */

public class EditProductActivity extends AppCompatActivity {
    private static final String TAG = "EditProductActivity";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ActivityEditProductBinding binding;
    private List<CategoryDTO> categories = new ArrayList<>();
    private Map<CategoryDTO, List<SubCategoryDTO>> categoryBundles = new HashMap<>();
    private Spinner subcategorySpinner;
    private Spinner categorySpinner;
    private CategoryDTO selectedCategory;
    private SubCategoryDTO selectedSubCategory;
    private List<LocalMedia> images;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        binding.price.setText(productDTO.getPrice().toString());
        binding.brand.setText(productDTO.getBrand());
        binding.description.setText(productDTO.getDescription());
        db = FirebaseFirestore.getInstance();
        // show process dialog
        ProgressDialog progressDialog = new ProgressDialog(EditProductActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");

        // show loading dialog
        progressDialog.show();
        db.collection(Constants.CATEGORIES_COLLECTION).orderBy("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CategoryDTO category = document.toObject(CategoryDTO.class);
                    category.setCategory_id(document.getId());
                    DocumentReference ref = db.document("categories/" + document.getId());
                    List<SubCategoryDTO> subcategories = new ArrayList<>();
                    categories.add(category);
                    db.collection(Constants.SUB_CATEGORIES_COLLECTION)
                            .whereEqualTo("category_ref", ref)
                            .get().addOnCompleteListener(sub_task -> {
                        if (sub_task.isSuccessful()) {
                            for (QueryDocumentSnapshot sub_document : sub_task.getResult()) {
                                SubCategoryDTO subcategory = sub_document.toObject(SubCategoryDTO.class);
                                subcategory.setSubcategory_id(sub_document.getId());
                                subcategories.add(subcategory);
                                progressDialog.dismiss();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
                    categoryBundles.put(category, subcategories);
                }
                db.collection(Constants.PRODUCT_COLLECTION)
                        .document(productDTO.getCategory_ref().getId())
                        .get().addOnCompleteListener(cate_task -> {
                    if (cate_task.isSuccessful()) {
                        selectedCategory = cate_task.getResult().toObject(CategoryDTO.class);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
                db.collection(Constants.PRODUCT_COLLECTION)
                        .document(productDTO.getSub_category_ref().getId())
                        .get().addOnCompleteListener(subCate_task -> {
                    if (subCate_task.isSuccessful()) {
                        selectedSubCategory = subCate_task.getResult().toObject(SubCategoryDTO.class);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
                categorySpinner = (Spinner) findViewById(R.id.category);
                subcategorySpinner = (Spinner) findViewById(R.id.subcategory);
                ArrayAdapter<CategoryDTO> dataAdapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_spinner_item, categories);
                categorySpinner.setAdapter(dataAdapter);
                // TODO: set category and subcategory
//                int spinnerPosition = categorySpinner.getPositionForView();
//                categorySpinner.setSelection(spinnerPosition);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        selectedCategory = (CategoryDTO) parent.getSelectedItem();
                        List<SubCategoryDTO> subCategoryDTOList = categoryBundles.get(selectedCategory);
                        ArrayAdapter<SubCategoryDTO> subCategoryAdapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_spinner_item, subCategoryDTOList);
                        subcategorySpinner.setAdapter(subCategoryAdapter);
                        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                                selectedSubCategory = (SubCategoryDTO) parent.getSelectedItem();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        switch (productDTO.getStatus()) {
            case Constants.HEAVILY_USED:
                binding.quality.setSelection(1);
                break;
            case Constants.WELL_USED:
                binding.quality.setSelection(2);
                break;
            case Constants.AVERAGE_CONDITION:
                binding.quality.setSelection(3);
                break;
            case Constants.SLIGHTLY_USED:
                binding.quality.setSelection(4);
                break;
            case Constants.EXCELLENT:
                binding.quality.setSelection(5);
                break;
            default:
                binding.quality.setSelection(3);
                break;
        }

        // TODO: getImage_address() -> LocalMedia List
        if (productDTO.getImage_address().size() == 0 ||productDTO.getImage_address() == null){
//            images.add("xxxx");
        } else {
        }
//        PictureCollectionAdapter pictureCollectionAdapter = new PictureCollectionAdapter(this, images);
//        binding.pfCollection.setAdapter(pictureCollectionAdapter);
//        GridLayoutManager manager = new GridLayoutManager(this, 2);
//        binding.pfCollection.setLayoutManager(manager);
        // TODO: copy edit information binding publish
    }
}
