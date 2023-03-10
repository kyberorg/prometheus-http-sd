package dev.kyberorg.httpsd.ui.pages.home;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.LitRenderer;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.db.models.Target;
import dev.kyberorg.httpsd.services.RecordService;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

public class RecordGrid extends Grid<Record> {

    public RecordGrid() {
        setSizeFull();

        //record name
        addColumn(RecordService::getRecordName).setHeader("Record name")
                .setFlexGrow(5).setSortable(true).setKey("record-name");

        //targets in record, separated by commas
        addColumn(this::formatTargets).setHeader("Targets").setFlexGrow(12)
                .setKey("record-targets");

        //labels in records, separated by commas
        addColumn(this::formatLabels).setHeader("Labels").setFlexGrow(12)
                .setKey("record-labels");

        //file
        addColumn(this::formatFileName).setHeader("File")
                .setSortable(true)
                .setFlexGrow(7).setKey("record-file");

        //status
        final String statusTemplate =
                "<vaadin-icon icon=\"vaadin:circle\" style=\"color: ${item.color};\"></vaadin-icon> ${item.text}";
        addColumn(LitRenderer.<Record>of(statusTemplate)
                .withProperty("color", record -> record.getStatus().getColor())
                .withProperty("text", record -> record.getStatus().getText()))
                .setHeader("Status")
                .setComparator(Comparator
                        .comparing(Record::getStatus))
                .setFlexGrow(5).setKey("record-status");




        // If the browser window size changes, check if all columns fit on
        // screen
        // (e.g. switching from portrait to landscape mode)
        UI.getCurrent().getPage().addBrowserWindowResizeListener(
                e -> setColumnVisibility(e.getWidth()));

    }

    public void refresh() {
        getDataProvider().refreshAll();
        this.setItems(RecordService.get().getAllRecords());
    }

    private String formatFileName(final Record record) {
        if (record == null || record.getFile() == null) return "Record is not included to any file";
        if (StringUtils.isBlank(record.getFile().getFileName())) return "EMPTY.json";
        return record.getFile().getFileName().toLowerCase() + ".json";
    }

    private String formatTargets(final Record record) {
        if (record == null || record.getTargets() == null || record.getTargets().size() == 0) {
            return "No targets";
        }
        return record.getTargets().stream().sorted(Comparator.comparing(Target::getId))
                .map(Target::getValue).collect(Collectors.joining(", "));
    }

    private String formatLabels(final Record record) {
        if (record == null || record.getLabels() == null || record.getLabels().size() == 0) {
            return "No labels";
        }
        return record.getLabels().stream().sorted(Comparator.comparing(Label::getId))
                .map(Label::toString).collect(Collectors.joining(", "));
    }

    private void setColumnVisibility(final int width) {
        if (width > 800) {
            //big screen (desktops, laptops etc.)
            getColumnByKey("record-name").setVisible(true);
            getColumnByKey("record-targets").setVisible(true);
            getColumnByKey("record-labels").setVisible(true);
            getColumnByKey("record-file").setVisible(true);
            getColumnByKey("record-status").setVisible(true);
        } else if (width > 550) {
            //middle screen - tables, phones in landscape layout
            getColumnByKey("record-name").setVisible(false);
            getColumnByKey("record-targets").setVisible(true);
            getColumnByKey("record-labels").setVisible(false);
            getColumnByKey("record-file").setVisible(true);
            getColumnByKey("record-status").setVisible(true);
        } else {
            //small screen - phones in portrait layout
            getColumnByKey("record-name").setVisible(false);
            getColumnByKey("record-targets").setVisible(true);
            getColumnByKey("record-labels").setVisible(false);
            getColumnByKey("record-file").setVisible(true);
            getColumnByKey("record-status").setVisible(true);
        }
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // fetch browser width
        UI.getCurrent().getInternals().setExtendedClientDetails(null);
        UI.getCurrent().getPage().retrieveExtendedClientDetails(e -> setColumnVisibility(e.getBodyClientWidth()));
    }

}
