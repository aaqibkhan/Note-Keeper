package treebo.taskaaqib.database;

import android.content.Context;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import treebo.taskaaqib.model.Note;

public class DBHelper {

    private static Realm mRealm;

    public static Realm getInstance(Context context) {
        if (mRealm == null) {
            RealmConfiguration config = new RealmConfiguration.Builder(context)
                    .name("notes.realm")
                    .build();
            mRealm = Realm.getInstance(config);
        }
        return mRealm;
    }

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

    public static RealmResults<Note> getAllNotesByModification(Context context) {
        return getInstance(context).where(Note.class)
                .findAllSorted("lastModificationDate", Sort.DESCENDING);
    }

    public static RealmResults<Note> getAllNotesByCreation(Context context) {
        return getInstance(context).where(Note.class)
                .findAllSorted("creationDate", Sort.DESCENDING);
    }

    public static RealmResults<Note> getAllNotesByTitle(Context context) {
        return getInstance(context).where(Note.class)
                .findAllSorted("heading", Sort.ASCENDING);
    }

}