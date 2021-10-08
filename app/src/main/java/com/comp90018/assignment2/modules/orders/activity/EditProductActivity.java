package com.comp90018.assignment2.modules.orders.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.config.GlideEngine;
import com.comp90018.assignment2.databinding.ActivityEditProductBinding;
import com.comp90018.assignment2.dto.CategoryDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.orders.adapter.CategoryArrayAdapter;
import com.comp90018.assignment2.modules.orders.adapter.ExistingPictureAdapter;
import com.comp90018.assignment2.modules.orders.adapter.PictureCollectionAdapter;
import com.comp90018.assignment2.modules.orders.adapter.SubCategoryArrayAdapter;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
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
    private ImageView pf_add;
    private RecyclerView pf_collection;
    private CategoryDTO selectedCategory;
    private SubCategoryDTO selectedSubCategory;
    private FirebaseStorage firebaseStorage;
//    private Integer qualityCode;
private ArrayList<String> images = new ArrayList<>();
    private List<LocalMedia> currentSelectLists;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        firebaseStorage = FirebaseStorage.getInstance();
        binding.price.setText(productDTO.getPrice().toString());
        binding.brand.setText(productDTO.getBrand());
        binding.description.setText(productDTO.getDescription());
        db = FirebaseFirestore.getInstance();
        pf_collection = view.findViewById(R.id.pf_collection);
        pf_add = view.findViewById(R.id.pf_add);
        pf_add.setOnClickListener(v -> PictureSelector.create(EditProductActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(4)
                .minSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .isPreviewImage(true)
                .isCompress(true)
                .withAspectRatio(1, 1)
                .forResult(Constants.REQUEST_CODE_A));

        // show categories
        ProgressDialog progressDialog = new ProgressDialog(EditProductActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        db.collection(Constants.CATEGORIES_COLLECTION)
                .document(productDTO.getCategory_ref().getId())
                .get().addOnCompleteListener(cate_task -> {
            if (cate_task.isSuccessful()) {
                selectedCategory = cate_task.getResult().toObject(CategoryDTO.class);
                selectedCategory.setCategory_id(productDTO.getCategory_ref().getId());
            } else {
                Log.d(TAG, "Error getting documents: ", cate_task.getException());
            }
        });

        db.collection(Constants.SUB_CATEGORIES_COLLECTION)
                .document(productDTO.getSub_category_ref().getId())
                .get().addOnCompleteListener(subCate_task -> {
            if (subCate_task.isSuccessful()) {
                selectedSubCategory = subCate_task.getResult().toObject(SubCategoryDTO.class);
            } else {
                Log.d(TAG, "Error getting documents: ", subCate_task.getException());
            }
        });

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
                            }
                            if (category.getName().equals(selectedCategory.getName())) {
                                subcategorySpinner = findViewById(R.id.subcategory);
                                subcategorySpinner.setAdapter(new SubCategoryArrayAdapter(this,
                                        android.R.layout.simple_spinner_item,
                                        subcategories,
                                        selectedSubCategory.getName()));
                            }
                            progressDialog.dismiss();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
                    categoryBundles.put(category, subcategories);
                }
                categorySpinner = findViewById(R.id.category);
                categorySpinner.setAdapter(new CategoryArrayAdapter(this, android.R.layout.simple_spinner_item, categories, selectedCategory.getName()));
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        selectedCategory = (CategoryDTO) parent.getSelectedItem();
                        List<SubCategoryDTO> subCategoryDTOList = categoryBundles.get(selectedCategory);
                        subcategorySpinner = findViewById(R.id.subcategory);
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

        // show status
//        qualityCode = productDTO.getQuality();
        switch (productDTO.getQuality()) {
            case Constants.HEAVILY_USED:
                binding.quality.setSelection(0);
                break;
            case Constants.WELL_USED:
                binding.quality.setSelection(1);
                break;
            case Constants.AVERAGE_CONDITION:
                binding.quality.setSelection(2);
                break;
            case Constants.SLIGHTLY_USED:
                binding.quality.setSelection(3);
                break;
            case Constants.EXCELLENT:
                binding.quality.setSelection(4);
                break;
            default:
                binding.quality.setSelection(2);
                break;
        }
        images = productDTO.getImage_address();
        ExistingPictureAdapter pictureCollectionAdapter = new ExistingPictureAdapter(this, images);
        binding.pfCollection.setAdapter(pictureCollectionAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        binding.pfCollection.setLayoutManager(manager);

        binding.upload.setOnClickListener(v1 -> {
            if (currentSelectLists == null || currentSelectLists.get(0) == null) {
                new AlertDialog.Builder(EditProductActivity.this).setMessage("Reselect pictures and upload").setPositiveButton("ok", null).show();
                return;
            } else {
                progressDialog.setTitle("Updating product pictures...");
                progressDialog.setMessage("Please wait");
                progressDialog.setCancelable(true);
                progressDialog.show();
                images.clear();
                for (LocalMedia picture: currentSelectLists) {
                    Log.d(TAG, "img path: " + picture.getRealPath());
                    File imageFile = new File(picture.getRealPath());
                    Uri fileUri = Uri.fromFile(imageFile);
                    StorageReference storageRef = firebaseStorage.getReference();
                    StorageReference newPublishProductReference = storageRef.child("public/products/" + fileUri.getLastPathSegment());
                    // Register observers to listen for when the download is done or if it fails
                    newPublishProductReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                        // get image url for storage
                        String imageUrl = Constants.STORAGE_ROOT_PATH + taskSnapshot.getStorage().getPath();
                        Log.d(TAG, "## Stored path is " + imageUrl);
                        // update image
                        images.add(imageUrl);
                        progressDialog.dismiss();
                        Toast.makeText(EditProductActivity.this, "Image updated Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(EditProductActivity.this, "Image Uploaded failed.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    });
                }
            }
        });

        binding.btnPublish.setOnClickListener(v2 -> {
            String price = binding.price.getText().toString();
            String brand = binding.brand.getText().toString();
            String description = binding.description.getText().toString();
            String quality = binding.quality.getSelectedItem().toString();

            if (price.length() == 0 || brand.length() == 0 || description.length() == 0 || quality.length() == 0 || selectedCategory == null || selectedSubCategory == null) {
                new AlertDialog.Builder(EditProductActivity.this).setMessage("Please enter all information.").setPositiveButton("ok", null).show();
                return;
            }

            String priceRegex = "(-?\\d+\\.?\\d{0,2})";
            if (!price.matches(priceRegex)) {
                new AlertDialog.Builder(EditProductActivity.this).setMessage("Please input correct price.").setPositiveButton("ok", null).show();
                return;
            }

            if (brand.length() >= 20) {
                new AlertDialog.Builder(EditProductActivity.this).setMessage("Brand: At most 20 characters").setPositiveButton("ok", null).show();
                return;
            }

            if (description.length() >= 80) {
                new AlertDialog.Builder(EditProductActivity.this).setMessage("Description: At most 80 characters").setPositiveButton("ok", null).show();
                return;
            }

            Integer qualityCode;
            switch(quality) {
                case "HEAVILY_USED":
                    qualityCode = Constants.HEAVILY_USED;
                    break;
                case "WELL_USED":
                    qualityCode = Constants.WELL_USED;
                    break;
                case "AVERAGE_CONDITION":
                    qualityCode = Constants.AVERAGE_CONDITION;
                    break;
                case "SLIGHTLY_USED":
                    qualityCode = Constants.SLIGHTLY_USED;
                    break;
                case "EXCELLENT":
                    qualityCode = Constants.EXCELLENT;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + quality);
            }

            // update images
            if (images == null || images.size() == 0) {
                new AlertDialog.Builder(EditProductActivity.this).setMessage("One uploaded picture required").setPositiveButton("ok", null).show();
                return;
            } else {
                ProgressDialog publishProgressDialog = new ProgressDialog(EditProductActivity.this);
                publishProgressDialog.setTitle("Publish...");
                publishProgressDialog.setMessage("Please wait");
                db.collection(Constants.PRODUCT_COLLECTION)
                        .document(productDTO.getId())
                        .update(
                                "price", Double.parseDouble(price),
                                "brand", brand,
                                "quality", qualityCode,
                                "description", description,
                                "publish_time", Timestamp.now(),
                                "image_address", images,
                                "category_ref", db.document("categories/" + selectedCategory.getCategory_id()),
                                "sub_category_ref", db.document("sub_categories/" + selectedSubCategory.getSubcategory_id())
                        ).addOnSuccessListener(aVoid -> {
                    productDTO.getOwner_ref().get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserDTO userDTO = task.getResult().toObject(UserDTO.class);
                            db.collection(Constants.PRODUCT_COLLECTION)
                                    .document(productDTO.getId()).get().addOnCompleteListener(product_task -> {
                                if (product_task.isSuccessful()) {
                                    ProductDTO newProductDTO = product_task.getResult().toObject(ProductDTO.class);
                                    publishProgressDialog.dismiss();
                                    finish();
                                    Intent publishProductIntent = new Intent(EditProductActivity.this, ProductDetailActivity.class);
                                    publishProductIntent.putExtra("productDTO", newProductDTO);
                                    publishProductIntent.putExtra("userDTO", userDTO);
                                    startActivity(publishProductIntent);
                                } else {
                                    Log.w(TAG, "db connection failed");
                                }
                            });
                        } else {
                            Log.w(TAG, "db connection failed");
                        }
                    });
                }).addOnFailureListener(e -> {
                    publishProgressDialog.dismiss();
                    Log.w(TAG, "Create product:failed", e);
                    new AlertDialog.Builder(EditProductActivity.this)
                            .setTitle("Sorry")
                            .setMessage("Database network issue, please try again later")
                            .setPositiveButton("Ok", null).show();
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE_A:
                currentSelectLists = PictureSelector.obtainMultipleResult(data);
                binding.pfCollection.setAdapter(new PictureCollectionAdapter(this, currentSelectLists));
                GridLayoutManager manager = new GridLayoutManager(this, 2);
                pf_collection.setLayoutManager(manager);
                break;
            default:
                break;
        }
    }
}
