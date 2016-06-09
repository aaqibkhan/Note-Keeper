package treebo.taskaaqib.database;

import android.content.Context;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import treebo.taskaaqib.model.Note;

/**
 * Helper class to interact with Realm database
 */
public class DBHelper {

    private static Realm mRealm;

    /**
     * Get instance of Realm database (notes.realm)
     *
     * @param context Activity or Application context
     * @return Realm instance
     */
    public static Realm getInstance(Context context) {
        if (mRealm == null) {
            RealmConfiguration config = new RealmConfiguration.Builder(context)
                    .name("notes.realm")
                    .build();
            mRealm = Realm.getInstance(config);
        }
        return mRealm;
    }

    /**
     * Inserts or updates a note in the database
     *
     * @param context Activity or Application context
     * @param note    The note to insert or update
     * @return True if insert/update was successful, false otherwise
     */
    public static boolean insertOrUpdateNote(Context context, Note note) {
        if (note != null) {
            Realm realm = getInstance(context);
            realm.beginTransaction();
            Date currentDate = new Date();
            if (note.getCreationDate() == null) {
                note.setCreationDate(currentDate);
                note.set_id(currentDate.getTime());
            }
            note.setLastModificationDate(currentDate);
            realm.copyToRealmOrUpdate(note);
            realm.commitTransaction();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove a note from the database
     *
     * @param context Activity or Application context
     * @param note    The note to remove
     * @return True if removal was successful, false otherwise
     */
    public static boolean removeNote(Context context, Note note) {
        if (note != null) {
            try {
                Realm realm = getInstance(context);
                realm.beginTransaction();
                realm.where(Note.class)
                        .equalTo("_id", note.get_id())
                        .findFirst()
                        .deleteFromRealm();
                realm.commitTransaction();
                return true;
            } catch (IllegalStateException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets all notes from database, sorted by 'lastModificationDate'
     * in DESCENDING order
     *
     * @param context Activity or Application context
     * @return RealmResults<Note> containing all notes from database
     */
    public static RealmResults<Note> getAllNotesByModification(Context context) {
        return getInstance(context).where(Note.class)
                .findAllSorted("lastModificationDate", Sort.DESCENDING);
    }

    /**
     * Gets all notes from database, sorted by 'creationDate'
     * in DESCENDING order
     *
     * @param context Activity or Application context
     * @return RealmResults<Note> containing all notes from database
     */
    public static RealmResults<Note> getAllNotesByCreation(Context context) {
        return getInstance(context).where(Note.class)
                .findAllSorted("creationDate", Sort.DESCENDING);
    }

    /**
     * Gets all notes from database, sorted by 'heading'
     * in ASCENDING order
     *
     * @param context Activity or Application context
     * @return RealmResults<Note> containing all notes from database
     */
    public static RealmResults<Note> getAllNotesByTitle(Context context) {
        return getInstance(context).where(Note.class)
                .findAllSorted("heading", Sort.ASCENDING);
    }

}