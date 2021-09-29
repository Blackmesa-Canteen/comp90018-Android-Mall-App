package com.comp90018.assignment2.modules.publish.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPublishProductBinding;
import com.comp90018.assignment2.dto.CategoryDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;
import com.comp90018.assignment2.modules.publish.fragment.PictureGalleryFragment;
import com.comp90018.assignment2.modules.users.me.activity.EditProfileActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luck.picture.lib.entity.LocalMedia;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ziyuan Xu
 */
public class PublishProductActivity extends AppCompatActivity{

    private final static String TAG = "PublishProductActivity";
    FirebaseFirestore db;
    private ActivityPublishProductBinding binding;
    private PictureGalleryFragment pictureGalleryFragment;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private List<CategoryDTO> categories = new ArrayList <>();
    private Map<CategoryDTO, List<SubCategoryDTO>> categoryBundles = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);

        // init view binding
        binding = ActivityPublishProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // load
        List<LocalMedia> currentSelectLists = loadPictureGalleryFragment();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

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
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        CategoryDTO selectedCategory = (CategoryDTO) parent.getSelectedItem();
                        List<SubCategoryDTO> subCategoryDTOList = categoryBundles.get(selectedCategory);
                        ArrayAdapter<SubCategoryDTO> subCategoryAdapter = new ArrayAdapter<>(PublishProductActivity.this, android.R.layout.simple_spinner_item, subCategoryDTOList);
                        subcategorySpinner.setAdapter(subCategoryAdapter);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        // setup submit
        binding.btnPublish.setOnClickListener(v -> {
            String price = binding.price.getText().toString();
            String brand = binding.brand.getText().toString();
            String description = binding.description.getText().toString();
            String quality = binding.quality.getSelectedItem().toString();

            if (price.length() == 0 || brand.length() == 0 || description.length() == 0 || quality.length() == 0) {
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

            ArrayList<String> images = new ArrayList<>();
            if (currentSelectLists.size() >=1) {
                progressDialog.setTitle("Updating product pictures...");
                progressDialog.setMessage("Please wait");
                progressDialog.setCancelable(true);
                progressDialog.show();
                for (LocalMedia picture: currentSelectLists) {
                    Log.d(TAG, "img path: " + picture.getRealPath());
                    File imageFile = new File(picture.getRealPath());
                    Uri fileUri = Uri.fromFile(imageFile);
                    StorageReference storageRef = firebaseStorage.getReference();
                    StorageReference newPublishProductReference = storageRef.child("public/products/" + "description" + fileUri.getLastPathSegment());//images.add(newPublishProductReference);
                    UploadTask uploadTask = newPublishProductReference.putFile(fileUri);
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PublishProductActivity.this, "Avatar updated Successfully", Toast.LENGTH_SHORT).show();
                            // get image url for storage
                            String imageUrl = Constants.STORAGE_ROOT_PATH + taskSnapshot.getStorage().getPath();
                            Log.d(TAG, "## Stored path is " + imageUrl);
                            // update avatar
                            images.add(imageUrl);
                        }
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(PublishProductActivity.this, "Image Uploaded failed.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
                }
            } else {
                new AlertDialog.Builder(PublishProductActivity.this).setMessage("One picture required").setPositiveButton("ok", null).show();
                return;
            }

            progressDialog.setTitle("Publishing");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            // TODO-1: ADD geo
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
                    .category_ref(null)
                    .sub_category_ref(null)
                    .view_number(0)
                    .status(0)
                    .favorite_number(0)
                    .geo_hash(null)
                    .lat(null)
                    .lng(null)
                    .location_text(null)
                    .build();
            publish(newProductDTO);
        });
    }

    public List<LocalMedia> loadPictureGalleryFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        pictureGalleryFragment =  new PictureGalleryFragment();
        fragmentManager.beginTransaction()
                .add(R.id.picture_gallery, pictureGalleryFragment)
                .commit();
        return pictureGalleryFragment.getSelectLists();
    }
    public void publish(ProductDTO productDTO){
       db.collection(Constants.PRODUCT_COLLECTION).add(productDTO);
        // TODO-2: after publish successfully, go to published page
    }

}
