package it.fb5.imgshare.imgshare.entity;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class Image {

    private long id;
    private User user;
    private ZonedDateTime uploadTimestamp;
    private byte[] data;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_image")
    @SequenceGenerator(name = "gen_image", sequenceName = "seq_image", allocationSize = 1)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ZonedDateTime getUploadTimestamp() {
        return this.uploadTimestamp;
    }

    public void setUploadTimestamp(ZonedDateTime uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] getData() {
        byte[] dataCopy = new byte[this.data.length];

        System.arraycopy(this.data, 0, dataCopy, 0, this.data.length);

        return dataCopy;
    }

    public void setData(byte[] data) {
        if (data == null) {
            return;
        }

        byte[] dataCopy = new byte[data.length];

        System.arraycopy(data, 0, dataCopy, 0, data.length);

        this.data = dataCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Image image = (Image) o;

        return id == image.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
