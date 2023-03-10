package dev.kyberorg.httpsd.db.models;

import dev.kyberorg.httpsd.db.models.base.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link Record} if combination of {@link Target}s and {@link Label}s, bound to certain {@link File}.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "records")
public class Record extends BaseModel {
    @Column(name = "name", unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    @Column(name = "active")
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "records_targets",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "targets_id"))
    @ToString.Exclude
    private Set<Target> targets = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "records_labels",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "labels_id"))
    @ToString.Exclude
    private Set<Label> labels = new LinkedHashSet<>();

    public Status getStatus() {
        return Status.fromBoolean(active);
    }

    public boolean isNewRecord() {
        return getName() == null;
    }

    /**
     * {@link Record} status. {@link #ACTIVE} means, that it should be included to generated JSON,
     * {@link Record} with {@link #DISABLED} status won;t be included to any JSON.
     * This allows temporary disable {@link Record} without deleting/abounding it from {@link File}.
     */
    public enum Status {
        ACTIVE("Active", "#2dd085", true), DISABLED("Disabled", "#f54993", false);

        @Getter private final String text;
        @Getter private final String color;
        @Getter private final boolean activeRecord;
        Status(final String text, final String color, final boolean active) {
            this.text = text;
            this.color = color;
            this.activeRecord = active;
        }

        /**
         * Creates {@link Status} from {@link Boolean}.
         *
         * @param active activity flag.
         * @return {@link Status#ACTIVE} if {@link #active} is {@code true} or {@link Status#DISABLED} if not.
         */
        public static Status fromBoolean(boolean active) {
            return active ? ACTIVE : DISABLED;
        }

    }
}