package dev.kyberorg.httpsd.json;

import dev.kyberorg.httpsd.App;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.services.FileService;
import dev.kyberorg.httpsd.services.RecordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates Service Discovery JSON. It will get all {@link Record.Status#ACTIVE} {@link Record}s,
 * transform them to {@link StaticConfig} objects and JSONize them using {@link App#GSON}.
 */
@RequiredArgsConstructor
@ResponseBody
@Controller
public class JsonGenerator {

    private final RecordService recordService;
    private final FileService fileService;

    /**
     * Get all {@linkplain /*.json} requests and generates JSON in response.
     *
     * @param file string with filename requested.
     *
     * @return {@link ResponseEntity} with generated JSON
     * or {@link ResponseEntity#unprocessableEntity()} if filename is empty.
     */
    @GetMapping(value ="/{file}.json", produces = "application/json")
    public ResponseEntity<String> serveJsonFiles(@PathVariable String file) {
        List<StaticConfig> staticConfigs = new ArrayList<>();
        if (StringUtils.isBlank(file)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        boolean fileNotFound = !fileService.isFileExists(file);
        if (fileNotFound) {
            return ResponseEntity.ok(App.GSON.toJson(staticConfigs));
        }

        recordService.getRecordsByFile(file).stream()
                .filter(Record::isActive).map(StaticConfig::create)
                .forEachOrdered(staticConfigs::add);

        return ResponseEntity.ok(App.GSON.toJson(staticConfigs));
    }
}
