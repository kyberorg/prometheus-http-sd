package dev.kyberorg.httpsd.ui.pages.home;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import dev.kyberorg.httpsd.auth.AccessControl;
import dev.kyberorg.httpsd.auth.AccessControlFactory;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.ui.MainLayout;
import dev.kyberorg.httpsd.ui.pages.login.LoginPage;
import jakarta.annotation.PostConstruct;

/**
 * Main application page. Contains Grid with all Records, filter and create/edit button.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Home Page")
public class HomePage extends HorizontalLayout implements BeforeEnterObserver, HasUrlParameter<String> {
    private final HomePageLogic pageLogic = new HomePageLogic(this);

    private RecordGrid grid;
    private RecordForm form;

    private final RecordDataProvider dataProvider = new RecordDataProvider();

    @PostConstruct
    public void init() {
        pageInit();
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        //Redirect to login page if auth enabled, but nobody logged in
        AccessControl accessControl = AccessControlFactory.get().getAccessControl();
        if (accessControl.isAuthEnabled() && !accessControl.isUserSignedIn()) {
            event.forwardTo(LoginPage.class);
        }
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        pageLogic.enter(parameter);
    }

    private void pageInit() {
        // Sets the width and the height of HomePage to "100%".
        setSizeFull();

        final HorizontalLayout topLayout = createTopBar();
        grid = new RecordGrid();
        grid.setItems(dataProvider);

        // Allows user to select a single row in the grid.
        grid.asSingleSelect().addValueChangeListener(
                event -> pageLogic.rowSelected(event.getValue()));
        form = new RecordForm(pageLogic);

        final VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);
    }

    /**
     * Deselects the selected row in the grid.
     */
    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    /**
     * Selects a row
     *
     * @param row {@link Record} bound to given row.
     */
    public void selectRow(final Record row) {
        grid.getSelectionModel().select(row);
    }

    /**
     * Updates a record in the records table.
     *
     * @param record non-empty {@link Record} to update.
     * @throws IllegalArgumentException when record if {@code null}
     */
    public void updateRecord(final Record record) {
        if (record == null) throw new IllegalArgumentException("record cannot be null");
        dataProvider.save(record);
    }

    /**
     * Removes a record from records table.
     *
     * @param record non-empty {@link Record} to update.
     * @throws IllegalArgumentException when record if {@code null}
     */
    public void removeProduct(final Record record) {
        if (record == null) throw new IllegalArgumentException("record cannot be null");
        dataProvider.delete(record);
    }

    /**
     * Displays user a form to edit a Record.
     *
     * @param record {@link Record} to put it to {@link RecordForm}.
     */
    public void editRecord(final Record record) {
        showForm(record != null);
        form.editRecord(record);
    }

    /**
     * Shows and hides the new product form
     *
     * @param show should form be shown or not.
     */
    public void showForm(final boolean show) {
        form.setVisible(show);
        form.setEnabled(show);
    }

    /**
     * Updates {@link Record}s in {@link RecordGrid}.
     */
    public void refreshGrid() {
        grid.refresh();
    }

    private HorizontalLayout createTopBar() {
        TextField filter = new TextField();
        filter.setPlaceholder("Filter name, targets, labels or statuses");
        // Apply the filter to grid's data provider. TextField value is never
        filter.addValueChangeListener(
                event -> dataProvider.setFilter(event.getValue()));
        // A shortcut to focus on the textField by pressing ctrl + F
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        Button newRecord = new Button("New record");
        // Setting theme variant of new production button to LUMO_PRIMARY that
        // changes its background color to blue and its text color to white
        newRecord.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newRecord.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newRecord.addClickListener(click -> pageLogic.newRecord());
        // A shortcut to click the new record button by pressing ALT + N
        newRecord.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        final HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newRecord);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }


}
