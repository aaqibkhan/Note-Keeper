package treebo.taskaaqib.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.realm.RealmResults;
import treebo.taskaaqib.R;
import treebo.taskaaqib.adapter.NotesAdapter;
import treebo.taskaaqib.database.DBHelper;
import treebo.taskaaqib.model.Note;

/**
 * The main screen
 */
public class MainActivity extends AppCompatActivity implements NotesAdapter.NotesListener {

    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;

    // Request code for Note activity
    private static final int REQUEST_CODE_NOTE = 123;

    // Constants denoting the sort field
    private static final int SORT_BY_TITLE = 2;
    private static final int SORT_BY_CREATION_DATE = 1;
    private static final int SORT_BY_MODIFIED_DATE = 0;

    // String constant to persist sort field variable 'mSortBy'
    private static final String PARAM_SORT = "PARAM_SORT";

    // Persisted variable holding the chosen sort field
    private int mSortBy;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(PARAM_SORT, mSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get sort field from savedInstanceState, if available
        if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_SORT)) {
            mSortBy = savedInstanceState.getInt(PARAM_SORT);
        }

        initViews();
        setupListeners();
        setupData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            onSortActionClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a popup-menu to select sort field, which are,
     * Sort by title,
     * Sort by creation date and
     * Sort by last modified date
     */
    private void onSortActionClicked() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_sort));
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
        switch (mSortBy) {
            default:
            case SORT_BY_TITLE:
                popup.getMenu().findItem(R.id.by_title).setChecked(true);
                break;
            case SORT_BY_CREATION_DATE:
                popup.getMenu().findItem(R.id.by_create_date).setChecked(true);
                break;
            case SORT_BY_MODIFIED_DATE:
                popup.getMenu().findItem(R.id.by_modified_date).setChecked(true);
                break;
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.by_title) {
                    if (!item.isChecked()) {
                        mSortBy = SORT_BY_TITLE;
                        setNotesData();
                    }
                } else if (id == R.id.by_create_date) {
                    if (!item.isChecked()) {
                        mSortBy = SORT_BY_CREATION_DATE;
                        setNotesData();
                    }
                } else if (id == R.id.by_modified_date) {
                    if (!item.isChecked()) {
                        mSortBy = SORT_BY_MODIFIED_DATE;
                        setNotesData();
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * Gets view references
     */
    private void initViews() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.notes_list);
    }

    /**
     * Assigns all listeners
     */
    private void setupListeners() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNoteClicked(null);
            }
        });
    }

    /**
     * Sets up the initial data for the screen
     */
    private void setupData() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        }
        setNotesData();
    }

    /**
     * Gets all the notes from database
     * depending upon the sort field selected
     * and updates the notes list
     */
    private void setNotesData() {
        RealmResults<Note> results;
        if (mSortBy == SORT_BY_TITLE) {
            results = DBHelper.getAllNotesByTitle(this);
        } else if (mSortBy == SORT_BY_CREATION_DATE) {
            results = DBHelper.getAllNotesByCreation(this);
        } else {
            results = DBHelper.getAllNotesByModification(this);
        }
        if (mAdapter == null) {
            mAdapter = new NotesAdapter(MainActivity.this, results, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateData(results);
        }
    }

    /**
     * Invoked when a note is clicked
     * or when a new note has to be made
     * Passes the note instance (if available) to NoteActivity
     *
     * @param note The note, if note was clicked
     */
    @Override
    public void onNoteClicked(Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        if (note != null) {
            intent.putExtra(NoteActivity.PARAM_NOTE, note);
        }
        startActivityForResult(intent, REQUEST_CODE_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Refresh the notes list if any data is changed
        if (requestCode == REQUEST_CODE_NOTE && resultCode == RESULT_OK) {
            setNotesData();
        }
    }

}
