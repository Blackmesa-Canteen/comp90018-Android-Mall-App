package com.comp90018.assignment2.modules.publish.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.comp90018.assignment2.databinding.ActivityPublishProductBinding;
import com.comp90018.assignment2.dto.CategoryDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;

import com.comp90018.assignment2.modules.location.utils.LocationHelper;
import com.comp90018.assignment2.modules.publish.adapter.PictureCollectionAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
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
 * @author Ziyuan Xu
 */
public class PublishProductActivity extends AppCompatActivity {
    private final static String TAG = "PublishProductActivity";
    private ActivityPublishProductBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private Spinner categorySpinner;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Spinner subcategorySpinner;
    private List<CategoryDTO> categories = new ArrayList<>();
    private Map<CategoryDTO, List<SubCategoryDTO>> categoryBundles = new HashMap<>();
    private CategoryDTO selectedCategory;
    private SubCategoryDTO selectedSubCategory;
    private List<LocalMedia> currentSelectLists;
    private ImageView pf_add;
    private RecyclerView pf_collection;
    private ArrayList<String> images = new ArrayList<>();
    private Double lng;
    private Double lat;
    private String hash;
    private String location_text;

    @SuppressLint("MissingPermission")
    // TODO: MissingPermission? exception
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);

        // init view binding
        binding = ActivityPublishProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        // attach to layout file
        setContentView(view);
        pf_collection = view.findViewById(R.id.pf_collection);
        pf_add = view.findViewById(R.id.pf_add);
        pf_add.setOnClickListener(v -> PictureSelector.create(PublishProductActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(4)
                .minSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .isPreviewImage(true)
                .isCompress(true)
                .forResult(Constants.REQUEST_CODE_A));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        // show process dialog
        ProgressDialog progressDialog = new ProgressDialog(PublishProductActivity.this);
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
                            }
                            progressDialog.dismiss();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
                    categoryBundles.put(category, subcategories);
                }
                categorySpinner = (Spinner) findViewById(R.id.category);
                subcategorySpinner = (Spinner) findViewById(R.id.subcategory);
                ArrayAdapter<CategoryDTO> dataAdapter = new ArrayAdapter<>(PublishProductActivity.this, android.R.layout.simple_spinner_item, categories);
                categorySpinner.setAdapter(dataAdapter);
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    // TODO: fix category bugs
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        selectedCategory = (CategoryDTO) parent.getSelectedItem();
                        List<SubCategoryDTO> subCategoryDTOList = categoryBundles.get(selectedCategory);
                        ArrayAdapter<SubCategoryDTO> subCategoryAdapter = new ArrayAdapter<>(PublishProductActivity.this, android.R.layout.simple_spinner_item, subCategoryDTOList);
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

        binding.upload.setOnClickListener(v1 -> {
            if (currentSelectLists == null || currentSelectLists.get(0) == null) {
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("select at least one picture to upload").setPositiveButton("ok", null).show();
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
                        Toast.makeText(PublishProductActivity.this, "Image updated Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(PublishProductActivity.this, "Image Uploaded failed.", Toast.LENGTH_SHORT).show();
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
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("Please enter all information.").setPositiveButton("ok", null).show();
                return;
            }

            String priceRegex = "(\\-?\\d+\\.?\\d{0,2})";
            if (!price.matches(priceRegex)) {
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("Please input correct price.").setPositiveButton("ok", null).show();
                return;
            }

            if (brand.length() >= 20) {
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("Brand: At most 20 characters").setPositiveButton("ok", null).show();
                return;
            }

            if (description.length() >= 80) {
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("Description: At most 80 characters").setPositiveButton("ok", null).show();
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
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("One uploaded picture required").setPositiveButton("ok", null).show();
                return;
            } else {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                DocumentReference userReference = db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid());
                ProductDTO newProductDTO = ProductDTO.builder()
                        .owner_ref(userReference)
                        .price(Double.parseDouble(price))
                        .brand(brand)
                        .quality(qualityCode)
                        .description(description)
                        .publish_time(Timestamp.now())
                        .image_address(images)
                        .category_ref(db.document("categories/" + selectedCategory.getCategory_id()))
                        .sub_category_ref(db.document("sub_categories/" + selectedSubCategory.getSubcategory_id()))
                        .view_number(0)
                        .status(0)
                        .favorite_number(0)
                        .currency(0)
                        .status(0)
                        .geo_hash(hash)
                        .lat(lat)
                        .lng(lng)
                        .star_number(0.0)
                        .location_coordinate(new GeoPoint(lat, lng))
                        .location_text(location_text)
                        .build();
                publish(newProductDTO);
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
                PictureCollectionAdapter pictureCollectionAdapter = new PictureCollectionAdapter(this,
                        currentSelectLists);
                pf_collection.setAdapter(pictureCollectionAdapter);
                GridLayoutManager manager = new GridLayoutManager(this, 2);
                pf_collection.setLayoutManager(manager);
                break;
            default:
                break;
        }
    }
    private void publish(ProductDTO productDTO){
        ProgressDialog publishProgressDialog = new ProgressDialog(PublishProductActivity.this);
        publishProgressDialog.setTitle("Publish...");
        publishProgressDialog.setMessage("Please wait");
        db.collection(Constants.PRODUCT_COLLECTION)
               .add(productDTO)
               .addOnSuccessListener(documentReference -> {
                   Log.d(TAG, "Product created with ID: "+ documentReference.getId());
                       db.collection(Constants.PRODUCT_COLLECTION)
                               .document(documentReference.getId())
                               .update("id", documentReference.getId());
                      // finish the activity
                      publishProgressDialog.dismiss();
                      finish();
                   // TODO(goto): after publish successfully, go to product detailed page
               }).addOnFailureListener(e -> {
                      publishProgressDialog.dismiss();
                      Log.w(TAG, "Create product:failed", e);
                      new AlertDialog.Builder(PublishProductActivity.this)
                              .setTitle("Sorry")
                              .setMessage("Database network issue, please try again later")
                              .setPositiveButton("Ok", null).show();
               });
    }
    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            lng =  loc.getLongitude();
            lat = loc.getLatitude();
            hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
            LocationHelper.getTextAddressWithCoordinate(
                    lat, lng, locationBean -> location_text = locationBean.getTextAddress()
            );
            // TODO: 处理失败情况，设置默认值
        }}
}
