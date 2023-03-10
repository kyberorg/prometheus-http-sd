package dev.kyberorg.httpsd.db.models;

import dev.kyberorg.httpsd.db.models.base.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Prometheus Target. Normally it is "host:port" string,
 * but it can be also "<a href="https://domain.tld">URL</a>" (for Blackbox-exporter).
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "targets")
public class Target extends BaseModel {
    @NotBlank(message = "Target value cannot be blank")
    @NotNull(message = "Target value cannot be null")
    @Column(nullable = false)
    private String value;

    @ManyToMany(mappedBy = "targets")
    @ToString.Exclude
    private Set<Record> records = new LinkedHashSet<>();
}