package me.wcy.music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class HistorySearch implements Serializable {
    private static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @NotNull
    @Property(nameInDb = "name")
    private String name;

    @Generated(hash = 1580392407)
    public HistorySearch(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 38246671)
    public HistorySearch() {
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
