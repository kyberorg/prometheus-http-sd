package dev.kyberorg.httpsd.db.models;

import dev.kyberorg.httpsd.db.models.base.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link Label} keys.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "label_keys")
public class LabelKey extends BaseModel {

    @NotBlank(message = "Label key value cannot be blank")
    @NotNull(message = "Label key value cannot be null")
    @Column(name = "value", nullable = false, unique = true)
    private String value;
}