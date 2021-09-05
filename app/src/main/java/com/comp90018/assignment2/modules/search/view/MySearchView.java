package com.comp90018.assignment2.modules.search.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.modules.search.db.GoBackCallBack;
import com.comp90018.assignment2.modules.search.db.InputCallBack;
import com.comp90018.assignment2.modules.search.db.SearchHistoryDbHelper;

/**
 * @author xiaotian
 *
 * modified based on opensource project Carson-Ho/Search_Layout
 * https://github.com/Carson-Ho/Search_Layout
 */
public class MySearchView extends LinearLayout {

    private final Context context;

    /** db: store history*/
    private SearchHistoryDbHelper helper ;
    private SQLiteDatabase db;

    /** views */
    private EditText editSearch;
    private TextView btnClearHistory;
    private LinearLayout searchBlock;
    private ImageView searchBack;

    /**
     * adapter to show history
     */
    private SearchListView listView;
    private BaseAdapter adapter;

    /**
     * call backs
     */
    private InputCallBack inputCallBack;
    private GoBackCallBack goBackCallBack;

    /**
     * attributes can be used by user
     * Search font property Settings: size & color & hint
     * Search box Settings: Height & color
     */
    private Float textSizeSearch;
    private int textColorSearch;
    private String textHintSearch;
    private int searchBlockHeight;
    private int searchBlockColor;

    public MySearchView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MySearchView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initCustomerAttributes(context, attributeSet);
        init();
    }

    public MySearchView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
        initCustomerAttributes(context, attributeSet);
        init();
    }

    /**
     * Initialize custom attributes
     * read from xml, and set some default value
     */
    private void initCustomerAttributes(Context context, AttributeSet attributeSet) {

        // attributes array
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MySearchView);

        // dp size of words in the search edit
        textSizeSearch = typedArray.getDimension(R.styleable.MySearchView_textSizeSearch, 20);

        // color ofwords in the edit
        int defaultColor = ContextCompat.getColor(context, R.color.gray);
        textColorSearch = typedArray.getColor(R.styleable.MySearchView_textColorSearch, defaultColor);

        // hint
        textHintSearch = typedArray.getString(R.styleable.MySearchView_textHintSearch);

        // height, default 150dp
        searchBlockHeight = typedArray.getInteger(R.styleable.MySearchView_searchBlockHeight, 150);

        // backgound color of the search
        int defaultColor2 = ContextCompat.getColor(context, R.color.white);
        searchBlockColor = typedArray.getColor(R.styleable.MySearchView_searchBlockColor, defaultColor2);

        // Recycles the TypedArray, to be re-used by a later caller.
        typedArray.recycle();
    }


    /**
     * init this view
     */
    private void init(){

        bindView();

        helper = new SearchHistoryDbHelper(context);

        // query all history when first enter the search
        queryData("");

        // clear history
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearAllHistory();
                // refresh display
                queryData("");
            }
        });

        /*
         * listen for soft-keyboard's search btn
         */
        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    if (inputCallBack != null){
                        inputCallBack.SearchAciton(editSearch.getText().toString());
                    }

                    if (editSearch.getText().length() == 0 || editSearch.getText().length() > 50) {
                        Toast.makeText(context, "please input correct query.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    // TODO: inplement search logic
                    Toast.makeText(context, "需要搜索的是" + editSearch.getText(), Toast.LENGTH_SHORT).show();

                    // check history existence
                    boolean hasData = hasData(editSearch.getText().toString().trim());

                    // if had history，will not save；
                    if (!hasData) {
                        insertData(editSearch.getText().toString().trim());
                        queryData("");
                    }
                }
                return false;
            }
        });


        /**
         * if change occurs in the edit
         */
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // if changed, call this method
            @Override
            public void afterTextChanged(Editable s) {
                // if edit is empty, show all history
                String tempName = editSearch.getText().toString();
                queryData(tempName);

            }
        });


        /**
         * if user click on items in history...
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // autofill the text
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String name = textView.getText().toString();
                editSearch.setText(name);
            }
        });

        /**
         * if user click go back button
         */
        searchBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((goBackCallBack != null)){
                    goBackCallBack.BackAciton();
                }

                // TODO: search page return logic
                Toast.makeText(context, "返回到上一页", Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * bind view
     */
    private void bindView(){

        LayoutInflater.from(context).inflate(R.layout.layout_search,this);

        editSearch = (EditText) findViewById(R.id.edit_search);
        editSearch.setTextSize(textSizeSearch);
        editSearch.setTextColor(textColorSearch);
        editSearch.setHint(textHintSearch);

        searchBlock = (LinearLayout)findViewById(R.id.search_block);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) searchBlock.getLayoutParams();
        searchBlock.setBackgroundColor(searchBlockColor);
        searchBlock.setLayoutParams(params);
        params.height = searchBlockHeight;

        // history list view
        listView = (SearchListView) findViewById(R.id.listView_search);

        btnClearHistory = (TextView) findViewById(R.id.btn_clear_history);
        btnClearHistory.setVisibility(INVISIBLE);

        searchBack = (ImageView) findViewById(R.id.search_back);

    }

    /**
     * query and print history
     */
    private void queryData(String tempName) {

        // fuzzy search for the input
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);

        adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, cursor, new String[] { "name" },
                new int[] { android.R.id.text1 }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(adapter);

        // refresh the adapter
        adapter.notifyDataSetChanged();

        System.out.println(cursor.getCount());

        // if histroy is not null, print clear history btn
        if (tempName.equals("") && cursor.getCount() != 0){
            btnClearHistory.setVisibility(VISIBLE);
        }
        else {
            btnClearHistory.setVisibility(INVISIBLE);
        };

    }

    /**
     * clear the history db
     */
    private void clearAllHistory() {

        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
        btnClearHistory.setVisibility(INVISIBLE);
    }

    /**
     * check records existence
     */
    private boolean hasData(String tempName) {
        // Find id name == tempName from the Record table in the database
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});

        return cursor.moveToNext();
    }

    /**
     * insert a record
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * click search button
     */
    public void setOnClickSearch(InputCallBack inputCallBack){
        this.inputCallBack = inputCallBack;

    }

    /**
     * click back button
     */
    public void setOnClickBack(GoBackCallBack backCallBack){
        this.goBackCallBack = backCallBack;
    }
}
