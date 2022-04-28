package nz.ac.canterbury.seng302.identityprovider.database;

import com.google.protobuf.ByteString;

import javax.persistence.*;

@Entity
public class PhotoModel {
    @Id
    private int id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private ByteString userPhoto;

    protected PhotoModel () {};

    public PhotoModel (int userId, ByteString rawImage) {
        this.id = userId;
        this.userPhoto = rawImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ByteString getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(ByteString userPhoto) {
        this.userPhoto = userPhoto;
    }
}
