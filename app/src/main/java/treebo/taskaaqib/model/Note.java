package treebo.taskaaqib.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import treebo.taskaaqib.util.Constants;

public class Note extends RealmObject implements Parcelable {

    @PrimaryKey
    private long _id;
    private String heading;
    private String body;
    private String bgColorHex;
    private String textColorHex;
    private Date creationDate;
    private Date lastModificationDate;

    public Note() {
        bgColorHex = Constants.hexWhite;
        textColorHex = Constants.hexBlack;
    }

    public Note(long _id, String heading, String body, String bgColorHex, String textColorHex, Date creationDate, Date lastModificationDate) {
        this._id = _id;
        this.heading = heading;
        this.body = body;
        this.bgColorHex = bgColorHex;
        this.textColorHex = textColorHex;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBgColorHex() {
        return bgColorHex;
    }

    public void setBgColorHex(String bgColorHex) {
        this.bgColorHex = bgColorHex;
    }

    public String getTextColorHex() {
        return textColorHex;
    }

    public void setTextColorHex(String textColorHex) {
        this.textColorHex = textColorHex;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._id);
        dest.writeString(this.heading);
        dest.writeString(this.body);
        dest.writeString(this.bgColorHex);
        dest.writeString(this.textColorHex);
        dest.writeLong(this.creationDate != null ? this.creationDate.getTime() : -1);
        dest.writeLong(this.lastModificationDate != null ? this.lastModificationDate.getTime() : -1);
    }

    protected Note(Parcel in) {
        this._id = in.readLong();
        this.heading = in.readString();
        this.body = in.readString();
        this.bgColorHex = in.readString();
        this.textColorHex = in.readString();
        long tmpCreationDate = in.readLong();
        this.creationDate = tmpCreationDate == -1 ? null : new Date(tmpCreationDate);
        long tmpLastModificationDate = in.readLong();
        this.lastModificationDate = tmpLastModificationDate == -1 ? null : new Date(tmpLastModificationDate);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
