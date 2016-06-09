package treebo.taskaaqib.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import treebo.taskaaqib.R;
import treebo.taskaaqib.adapter.BGColorAdapter;
import treebo.taskaaqib.database.DBHelper;
import treebo.taskaaqib.model.Note;
import treebo.taskaaqib.util.Constants;

public class NoteActivity extends AppCompatActivity implements BGColorAdapter.BGColorCallbacks {

    public static final String PARAM_NOTE = "PARAM_NOTE";

    private boolean isOld, isModified;
    private Note mNote;
    private EditText mTitle, mBody;
    private RecyclerView mRecyclerView;
    private View mContentView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setPopupTheme(R.style.MyOverFlowTheme);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            if (getIntent().hasExtra(PARAM_NOTE)) {
                mNote = getIntent().getParcelableExtra(PARAM_NOTE);
                isOld = true;
            }
        }
        if (isOld) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        initViews();
        setupViews();
        setupListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isOld) {
            menu.findItem(R.id.action_delete).setVisible(true);
        } else {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_delete:
                if (DBHelper.removeNote(this, mNote)) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            case R.id.action_discard:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return true;
    }

    private void initViews() {
        mTitle = (EditText) findViewById(R.id.tv_title);
        mBody = (EditText) findViewById(R.id.tv_body);
        mRecyclerView = (RecyclerView) findViewById(R.id.bg_color_list);
        mContentView = findViewById(R.id.content_layout);
    }

    private void setupViews() {
        if (mNote != null) {
            mTitle.setText(mNote.getHeading());
            mBody.setText(mNote.getBody());
        }
        mContentView.setBackgroundColor(Color.parseColor(mNote != null ? mNote.getBgColorHex() : Constants.hexWhite));
        mTitle.setTextColor(Color.parseColor(mNote != null ? mNote.getTextColorHex() : Constants.hexBlack));
        mBody.setTextColor(Color.parseColor(mNote != null ? mNote.getTextColorHex() : Constants.hexBlack));
        if (mNote != null && !mNote.getBgColorHex().equalsIgnoreCase(Constants.hexWhite)) {
            mTitle.setHintTextColor(Color.parseColor(Constants.hexHintWhitish));
            mBody.setHintTextColor(Color.parseColor(Constants.hexHintWhitish));
            mToolbar.setBackgroundColor(Color.parseColor(mNote.getBgColorHex()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(Color.parseColor(mNote.getBgColorHex()));
            }
        }
        mRecyclerView.addItemDecoration(new HorizontalSpace(getResources().getDimensionPixelSize(R.dimen.margin_large)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new BGColorAdapter(this));
    }

    private void setupListeners() {
        mTitle.addTextChangedListener(textWatcher);
        mBody.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isModified = true;
            mTitle.removeTextChangedListener(this);
            mBody.removeTextChangedListener(this);
        }
    };

    @Override
    public void onColorSelected(String color) {
        if (mNote == null) {
            mNote = new Note();
        }
        if (!mNote.getBgColorHex().equalsIgnoreCase(color)) {
            isModified = true;
            mContentView.setBackgroundColor(Color.parseColor(color));
            mNote.setBgColorHex(color);
            if (color.equalsIgnoreCase(Constants.hexWhite)) {
                mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navigationBarColor));
                }
            } else {
                mToolbar.setBackgroundColor(Color.parseColor(color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(Color.parseColor(color));
                }
            }
            if (color.equalsIgnoreCase(Constants.hexWhite)) {
                mTitle.setTextColor(Color.parseColor(Constants.hexBlack));
                mBody.setTextColor(Color.parseColor(Constants.hexBlack));
                mTitle.setHintTextColor(Color.parseColor(Constants.hexHint));
                mBody.setHintTextColor(Color.parseColor(Constants.hexHint));
                mNote.setTextColorHex(Constants.hexBlack);
            } else {
                mTitle.setTextColor(Color.parseColor(Constants.hexWhite));
                mBody.setTextColor(Color.parseColor(Constants.hexWhite));
                mTitle.setHintTextColor(Color.parseColor(Constants.hexHintWhitish));
                mBody.setHintTextColor(Color.parseColor(Constants.hexHintWhitish));
                mNote.setTextColorHex(Constants.hexWhite);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isOld) {
            if (isModified) {
                if (TextUtils.isEmpty(mTitle.getText()) && TextUtils.isEmpty(mBody.getText())) {
                    if (DBHelper.removeNote(this, mNote)) {
                        setResult(RESULT_OK);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                } else {
                    mNote.setHeading(mTitle.getText().toString());
                    mNote.setBody(mBody.getText().toString());
                    if (DBHelper.insertOrUpdateNote(this, mNote)) {
                        setResult(RESULT_OK);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                }
            } else {
                setResult(RESULT_CANCELED);
            }
        } else {
            if (!TextUtils.isEmpty(mTitle.getText()) || !TextUtils.isEmpty(mBody.getText())) {
                if (mNote == null) {
                    mNote = new Note();
                }
                mNote.setHeading(mTitle.getText().toString());
                mNote.setBody(mBody.getText().toString());
                if (DBHelper.insertOrUpdateNote(this, mNote)) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
            } else {
                setResult(RESULT_CANCELED);
            }
        }
        finish();
    }

    class HorizontalSpace extends RecyclerView.ItemDecoration {

        private final int mSpace;

        public HorizontalSpace(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = mSpace;
                outRect.right = mSpace;
            } else {
                outRect.right = mSpace;
            }
        }
    }
}
