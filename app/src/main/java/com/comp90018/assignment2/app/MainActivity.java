package com.comp90018.assignment2.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.app.categories.fragment.CategoriesFragment;
import com.comp90018.assignment2.app.home.fragment.HomeFragment;
import com.comp90018.assignment2.app.messages.fragment.MessagesFragment;
import com.comp90018.assignment2.app.users.me.fragment.MeFragment;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/**
 * main activity of the app
 * @author xiaotian
 */
public class MainActivity extends AppCompatActivity {

    /** view binding to replace butter knife, see android documents */
    private ActivityMainBinding binding;

    /** holds all fragments: home, categories, messages and me */
    private ArrayList<BaseFragment> fragments;

    /** used for picking fragments */
    private int position;

    /** the Fragment that is shown before */
    private Fragment prevFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(R.layout.activity_main);

        // load fragments
        loadFragments();

        // set up bottom buttons' event listeners
        setClickListener();
    }



    /**
     * put fragments in activity
     */
    private void loadFragments() {
        // used to store all fragments
        fragments = new ArrayList<>();

        fragments.add(new HomeFragment());
        fragments.add(new CategoriesFragment());
        fragments.add(new MessagesFragment());
        fragments.add(new MeFragment());
    }

    /**
     * set up click events for changing fragment
     */
    private void setClickListener() {

        // check home first
        binding.radioGroupMain.check(R.id.button_main_home);

        // behavior when select different buttons
        binding.radioGroupMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            // Reminder: position's sequence matches the fragments arrayList
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button_main_home:
                        position = 0;
                        break;

                    case R.id.button_main_categories:
                        position = 1;
                        break;

                    case R.id.button_main_messages:
                        position = 2;
                        break;
                    case R.id.button_main_me:
                        position = 3;
                        break;

                    case R.id.button_main_publish:
                        // start publish activity
                        return;
                    default:
                        position = 0;
                        break;
                }

                // got position, then change fragments
                BaseFragment newFragment = pickFragment(position);

                // change Fragment
                changeFragment(prevFragment, newFragment);
            }
        });
    }

    /** pick fragment of a specific position from fragments arraylist */
    private BaseFragment pickFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            return fragments.get(position);
        }

        return null;
    }

    /**
     * switch fragment
     * @param fromFragment original fragment
     * @param newFragment new picked fragment
     */
    private void changeFragment(Fragment fromFragment, BaseFragment newFragment) {

        // make sure picked different fragment
        if (prevFragment != newFragment) {
            // refresh the previous one
            prevFragment = newFragment;

            if (newFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                /*
                * check whether the new fragment is currently added to its activity or not.
                */
                if(!newFragment.isAdded()) {
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.add(R.id.frameLayout_main, newFragment).commit();

                } else {
                    // if the new Fragment has been added to the transaction, just show it!
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }

                    transaction.show(newFragment).commit();
                }
            }
        }
    }
}