package com.comp90018.assignment2.modules.search.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.comp90018.assignment2.databinding.ActivitySearchProductBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.modules.search.view.callback.GoBackCallBack;
import com.comp90018.assignment2.modules.search.view.callback.InputCallBack;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 *
 * search page: fuzzy search product name
 *
 * @author xiaotian
 */
public class SearchProductActivity extends AppCompatActivity {

    private ActivitySearchProductBinding binding;

    FirebaseFirestore db;
    private final static String TAG = "SearchProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up view binding
        // init view binding
        binding = ActivitySearchProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // init db
        db = FirebaseFirestore.getInstance();

        // bind search view listener
        binding.searchView.setOnClickSearch(new InputCallBack() {
            @Override
            public void SearchAciton(String searchWord) {

                // show process dialog
                ProgressDialog progressDialog=new ProgressDialog(SearchProductActivity.this);
                progressDialog.setTitle("Searching");
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                // do fuzzy search
                /*
                 * The character \uf8ff used in the query is a very high code point in the Unicode range
                 * (it is a Private Usage Area [PUA] code). Because it is after most regular
                 * characters in Unicode, the query matches all values that start with queryText.
                 */
                db.collection(Constants.PRODUCT_COLLECTION)
                        .orderBy("description")
                        .startAt(searchWord)
                        .endAt(searchWord + "\uf8ff")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    /* ALERT!! Use ArrayList here!!! */
                                    ArrayList<ProductDTO> productDTOList = new ArrayList<>();

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // add query results to list
                                        productDTOList.add(document.toObject(ProductDTO.class));
                                    }

                                    // start result activity
                                    Intent goToSearchResultIntent = new Intent(SearchProductActivity.this, SearchResultActivity.class);
                                    goToSearchResultIntent.putParcelableArrayListExtra("productDTOList", productDTOList);

                                    progressDialog.dismiss();
                                    startActivity(goToSearchResultIntent);
                                } else {
                                    progressDialog.dismiss();
                                    new AlertDialog.Builder(SearchProductActivity.this).setMessage("Search failed, please try again later.").setPositiveButton("ok", null).show();
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }
        });

        binding.searchView.setOnClickBack(new GoBackCallBack() {
            @Override
            public void BackAciton() {
                // finish this page
                finish();
            }
        });
    }
}