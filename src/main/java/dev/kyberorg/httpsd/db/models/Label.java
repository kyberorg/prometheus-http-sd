package dev.kyberorg.httpsd.db.models;

import dev.kyberorg.httpsd.db.models.base.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Key=Value combination, stored in the Database. Technically, it is mapping of {@link LabelKey} and {@link LabelValue}.
 */
@Getter
@Setter
@Entity
@Table(name = "labels")
public class Label extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "label_key_id", nullable = false)
    private LabelKey labelKey;

    @ManyToOne(optional = false)
    @JoinColumn(name = "label_value_id", nullable = false)
    private LabelValue labelValue;

    @ManyToMany(mappedBy = "labels")
    private Set<Record> records = new LinkedHashSet<>();

    @Override
    public String toString() {
        String key = StringUtils.isNotBlank(labelKey.getValue()) ? labelKey.getValue() : "null";
        String value = StringUtils.isNotBlank(labelValue.getValue()) ? labelValue.getValue() : "null";
        return key + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Label label = (Label) o;
        return getId() != null && Objects.equals(getId(), label.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean isNewLabel() {
        return getId() == null;
    }
}