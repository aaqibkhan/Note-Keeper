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

/**
 * The note screen, where user can view, edit or delete it
 */
public class NoteActivity extends AppCompatActivity implements BGColorAdapter.BGColorCallbacks {

    // String constant to receive Note instance
    public static final String PARAM_NOTE = "PARAM_NOTE";
    // String constant to persist note color variable 'mColorNote'
    private static final String PARAM_COLOR = "PARAM_COLOR";

    // Persisted variable holding the chosen note color
    private String mColorNote;

    private boolean isOld, isModified;
    private Note mNote;
    private EditText mTitle, mBody;
    private RecyclerView mRecyclerView;
    private View mContentView;
    private Toolbar mToolbar;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PARAM_COLOR, mColorNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setPopupTheme(R.style.MyOverFlowTheme);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
            Get note instance (if available)
            A note instance denotes that user is editing a note,
            else is making a new note
        */
        if (getIntent() != null) {
            if (getIntent().hasExtra(PARAM_NOTE)) {
                mNote = getIntent().getParcelableExtra(PARAM_NOTE);
                isOld = true;
            }
        }

        // Get color field from savedInstanceState, if available
        if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_COLOR)) {
            mColorNote = savedInstanceState.getString(PARAM_COLOR);
        } else {
            mColorNote = mNote != null ? mNote.getBgColorHex() : Constants.hexWhite;
        }

        // If editing a note, then do not show keyboard initially
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
        /*
            If user is making a new note, then
            do not show delete action, else show it
         */
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
                // Handle toolbar back button press
                onBackPressed();
                break;
            case R.id.action_delete:
                /*
                    Remove the note from database
                    If removal successful, then notify main screen
                    that data changed else nothing changed
                 */
                if (DBHelper.removeNote(this, mNote)) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            case R.id.action_discard:
                /*
                    Do not make any changes to the note, i.e,
                    do not save it in database and
                    notify main screen that nothing changed
                 */
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return true;
    }

    /**
     * Get view references
     */
    private void initViews() {
        mTitle = (EditText) findViewById(R.id.tv_title);
        mBody = (EditText) findViewById(R.id.tv_body);
        mRecyclerView = (RecyclerView) findViewById(R.id.bg_color_list);
        mContentView = findViewById(R.id.content_layout);
    }

    /**
     * Set the initial data for the screen
     */
    private void setupViews() {
        // If editing a note, assign the title and body
        if (mNote != null) {
            mTitle.setText(mNote.getHeading());
            mBody.setText(mNote.getBody());
        }

        // Initialize the color of all views
        setColor(mColorNote, true);

        // Setup recycler view for background colors
        mRecyclerView.addItemDecoration(new HorizontalSpace(getResources().getDimensionPixelSize(R.dimen.margin_large)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new BGColorAdapter(this));
    }

    /**
     * Assigns all listeners
     */
    private void setupListeners() {
        mTitle.addTextChangedListener(textWatcher);
        mBody.addTextChangedListener(textWatcher);
    }

    /**
     * Checks if text was modified or not,
     * to later decide if to update
     * last modified time or not
     */
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

    /**
     * Invoked when a color is selected
     *
     * @param color The color in hex format (#RRGGBB)
     */
    @Override
    public void onColorSelected(String color) {
        setColor(color, false);
    }

    /**
     * Sets the color on the note object and
     * updates all related view's color
     *
     * @param color The color in hex format (#RRGGBB)
     * @param force If true, will set color even if it matches the existing color in Note object
     */
    public void setColor(String color, boolean force) {
        mColorNote = color;
        if (mNote == null) {
            mNote = new Note();
        }
        // Make color change if its a different color from the existing one
        if (!mNote.getBgColorHex().equalsIgnoreCase(color) || force) {
            // Set modified to true if 'force' is false
            if (!force) {
                isModified = true;
            }

            // Change note background to selected color and update the note instance
            mContentView.setBackgroundColor(Color.parseColor(color));
            mNote.setBgColorHex(color);

            if (color.equalsIgnoreCase(Constants.hexWhite)) {
                /*
                    If selected background color is white then
                    set default color for toolbar and navigationBar
                    navigationBar color is only set for LOLLIPOP and above devices
                 */
                mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navigationBarColor));
                }
            } else {
                /*
                    If selected background color NOT white then
                    set selected color for toolbar and navigationBar
                    navigationBar color is only set for LOLLIPOP and above devices
                 */
                mToolbar.setBackgroundColor(Color.parseColor(color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(Color.parseColor(color));
                }
            }
            if (color.equalsIgnoreCase(Constants.hexWhite)) {
                /*
                    If selected background color is white then
                    set black and blackish color for text and text hint respectively
                 */
                mTitle.setTextColor(Color.parseColor(Constants.hexBlack));
                mBody.setTextColor(Color.parseColor(Constants.hexBlack));
                mTitle.setHintTextColor(Color.parseColor(Constants.hexHint));
                mBody.setHintTextColor(Color.parseColor(Constants.hexHint));
                mNote.setTextColorHex(Constants.hexBlack);
            } else {
                /*
                    If selected background color NOT white then
                    set white and whitish color for text and text hint respectively
                 */
                mTitle.setTextColor(Color.parseColor(Constants.hexWhite));
                mBody.setTextColor(Color.parseColor(Constants.hexWhite));
                mTitle.setHintTextColor(Color.parseColor(Constants.hexHintWhitish));
                mBody.setHintTextColor(Color.parseColor(Constants.hexHintWhitish));
                mNote.setTextColorHex(Constants.hexWhite);
            }
        }
    }

    /**
     * Decides whether to insert or update note in database or not
     * Toolbar back button has been redirected here.
     * <p/>
     * If user is making a new note, check if title or body is
     * non-empty, if yes then create a new note in database.
     * <p/>
     * If user is editing a note, check if text or color has been modified or not,
     * if yes then save the changes in database.
     * <p/>
     * If modification has resulted in empty title and empty body then
     * remove the note from the database
     */
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

    /**
     * ItemDecorator to add horizontal space in background colors list
     */
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
