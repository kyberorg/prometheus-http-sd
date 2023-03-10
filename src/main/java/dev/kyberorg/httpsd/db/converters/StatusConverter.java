package dev.kyberorg.httpsd.db.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import dev.kyberorg.httpsd.db.models.Record;

/**
 * Converts {@link Record.Status} to {@link Boolean} or vise-versa.
 */
public class StatusConverter implements Converter<Record.Status, Boolean> {
    @Override
    public Result<Boolean> convertToModel(Record.Status status, ValueContext valueContext) {
        return Result.ok(status.isActiveRecord());
    }

    @Override
    public Record.Status convertToPresentation(Boolean bool, ValueContext valueContext) {
        return Record.Status.fromBoolean(bool);
    }
}
