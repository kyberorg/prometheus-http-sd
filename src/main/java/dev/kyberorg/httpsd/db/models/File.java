package dev.kyberorg.httpsd.db.models;

import dev.kyberorg.httpsd.db.models.base.BaseModel;
import dev.kyberorg.httpsd.json.JsonGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Table with JSON filenames.
 * When Prometheus visits {@link #fileName}.json, {@link JsonGenerator} searches for {@link Record}s,
 * bound wih given {@link File} are creates valid JSON.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "files")
public class File extends BaseModel {
    @Column(name = "filename", nullable = false, unique = true)
    private String fileName;
}