package dev.kyberorg.httpsd.json;

import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.db.models.Target;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Combination of {@link Target}s and {@link Label}s in Prometheus-required format.
 */
@Data
public class StaticConfig {

    private List<String> targets = new ArrayList<>();
    private Map<String, String> labels = new HashMap<>();

    /**
     * Creates {@link StaticConfig} from {@link Record} object.
     *
     * @param record not-empty {@link Record} object.
     * @return new {@link StaticConfig} object or empty {@link StaticConfig} if {@link Record} is {@code null}.
     */
    public static StaticConfig create(final Record record) {
        if (record == null) return new StaticConfig();
        StaticConfig staticConfig = new StaticConfig();
        record.getTargets().stream().map(Target::getValue).forEachOrdered(staticConfig.targets::add);
        record.getLabels().forEach(label ->
                staticConfig.labels.put(label.getLabelKey().getValue(), label.getLabelValue().getValue()));
        return staticConfig;
    }
}
