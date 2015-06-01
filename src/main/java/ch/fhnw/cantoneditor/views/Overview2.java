package ch.fhnw.cantoneditor.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.gpl.jsplitbutton.JSplitButton;
import org.gpl.jsplitbutton.SplitButtonActionListener;

import ch.fhnw.cantoneditor.datautils.CsvReader;
import ch.fhnw.cantoneditor.datautils.DataStorage;
import ch.fhnw.cantoneditor.datautils.Searcher;
import ch.fhnw.cantoneditor.libs.GridBagManager;
import ch.fhnw.cantoneditor.model.Canton;
import ch.fhnw.cantoneditor.model.CantonTableModel;
import ch.fhnw.command.CommandController;
import ch.fhnw.command.Executable;
import ch.fhnw.observation.ComputedValue;
import ch.fhnw.observation.ObservableList;
import ch.fhnw.observation.SwingObservables;
import ch.fhnw.observation.ValueSubscribable;
import ch.fhnw.oop.led.Led;
import ch.fhnw.oop.splitflap.GlobalTimer;
import ch.fhnw.oop.splitflap.SplitFlap;

public class Overview2 {
    private TranslationManager tm = TranslationManager.getInstance();
    private List<Canton> allCantons;
    private ObservableList<Canton> filteredCantons;

    private int searchCount = 0;

    private void searchCompleted(Collection<Canton> cantons, int searchIndex) {
        if (searchIndex == searchCount) {
            filteredCantons.reset(cantons);
        }
    }

    public void show() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        JFrame frame = new JFrame(tm.translate("OverviewTitle"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                try {
                    DataStorage.save();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
                System.exit(0);
            }
        });

        allCantons = DataStorage.getAllCantons();
        filteredCantons = new ObservableList<>(allCantons);
        CantonTableModel tableModel = new CantonTableModel(filteredCantons);
        JTable table = new JTable(tableModel);
        table.setSelectionModel(tableModel.getSelectionModel());
        table.setMinimumSize(new Dimension(400, 400));
        JScrollPane scroller = new JScrollPane(table);

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroller,
                new CantonEditPanel().getComponent(frame));
        CsvReader.class.getResourceAsStream("/Communes.txt");

        JPanel rootPane = new JPanel(new BorderLayout());
        rootPane.add(initButtonPanel(), BorderLayout.PAGE_START);

        rootPane.add(splitter, BorderLayout.CENTER);
        // rootPane.add(new CantonEditPanel().getComponent(frame), BorderLayout.LINE_END);

        JPanel pageEndPanel = new JPanel(new BorderLayout());
        pageEndPanel.add(getLedPanel(), BorderLayout.PAGE_START);
        pageEndPanel.add(initInhabitantsAndAreaDisplay(), BorderLayout.PAGE_END);

        rootPane.add(pageEndPanel, BorderLayout.PAGE_END);

        frame.add(rootPane);
        frame.pack();
        CantonHandler.setCurrentCanton(allCantons.get(0));
        frame.setVisible(true);

    }

    private JPanel initButtonPanel() {

        PlaceholderTextField tfSearch = new PlaceholderTextField();
        tfSearch.setPlaceholder(tm.translate("Search", "Search") + "...");
        tfSearch.setPreferredSize(new Dimension(100, 30));
        ValueSubscribable<String> searchText = SwingObservables.getFromTextField(tfSearch, 200);
        searchText.addPropertyChangeListener(l -> {
            Searcher<Canton> search = new Searcher<Canton>((String) l.getNewValue(), allCantons);
            searchCount++;
            search.setOnFinish(of -> {
                SwingUtilities.invokeLater(() -> {
                    searchCompleted(search.getResult(), searchCount);
                });
            });
            Thread th = new Thread(search);
            th.start();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(tfSearch);
        buttonPanel.add(getUndoButton());
        buttonPanel.add(getRedoButton());
        return buttonPanel;
    }

    private JPanel getLedPanel() {
        List<Canton> cantons = DataStorage.getAllCantons();
        JPanel panel = new JPanel();
        // panel.setSize(panel.getWidth(), 5);
        for (Canton cnt : cantons) {
            Canton old = cnt.copyToNew();

            ComputedValue<Boolean> hasChanged = new ComputedValue<>(() -> {
                return !cnt.getName().equals(old.getName()) || !cnt.getCapital().equals(old.getCapital())
                        || !cnt.getShortCut().equals(old.getShortCut())
                        || !(cnt.getNrInhabitants() == old.getNrInhabitants()) || !(cnt.getArea() == old.getArea())
                        || !(cnt.getCommunes().equals(old.getCommunes()));
            });
            Led flapper = new Led();
            CantonHandler.getCurrentCantonObservable().addPropertyChangeListener(e -> {
                if (CantonHandler.getCurrentCanton() != null) {
                    flapper.setOn(CantonHandler.getCurrentCanton() == cnt);
                }
            });
            flapper.init(30, 30);
            flapper.setSize(30, 30);
            hasChanged.bindTo((vl) -> {
                flapper.setColor(vl.booleanValue() ? Color.GREEN : Color.RED);
            });
            panel.add(flapper);
        }
        return panel;
    }

    /**
     * Creates the lower part of the Frame, which contains the flap display to show the number of
     * citizen and the area
     */
    private JPanel initInhabitantsAndAreaDisplay() {

        JPanel inhabPanel = new JPanel();
        inhabPanel.setMinimumSize(new Dimension(400, 200));
        GridBagManager localGbm = new GridBagManager(inhabPanel);
        ComputedValue<Integer> inhabitantsHandler = new ComputedValue<>(() -> {
            int inhabs = 0;
            for (Canton c : allCantons) {
                inhabs += c.getNrInhabitants();
            }
            return inhabs;
        });
        ComputedValue<Double> areaHandler = new ComputedValue<>(() -> {
            double area = 0;
            for (Canton c : allCantons) {
                area += c.getArea();
            }
            return area;
        });

        int x = 0;
        int y = 0;
        SplitFlap[] inhabitantFlaps = new SplitFlap[10];
        SplitFlap[] areaFlaps = new SplitFlap[10];
        localGbm.setWeightX(1.0).setX(x++).setY(y).setComp(new JLabel(""));

        localGbm.setWeightX(0).setX(x).setY(y++).setComp(initSplitFlapPanel(inhabitantFlaps));
        localGbm.setWeightX(0).setX(x).setY(y++).setComp(initSplitFlapPanel(areaFlaps));

        inhabitantsHandler.bindTo(t -> {
            updateFlapText(t, inhabitantFlaps);
        });
        areaHandler.bindTo(t -> {
            updateFlapText((t == null) ? null : t.intValue(), areaFlaps);
        });

        return inhabPanel;
    }

    private JPanel initSplitFlapPanel(final SplitFlap[] flaps) {
        JPanel panel = new JPanel();
        GridBagManager gbm = new GridBagManager(panel);
        int x = 0;
        for (int i = 0; i < flaps.length; i++) {
            flaps[i] = new SplitFlap();
            flaps[i].setBounds(new Rectangle(20, 20));
            flaps[i].setSize(new Dimension(20, 20));
            flaps[i].setSelection(SplitFlap.NUMERIC);
            gbm.setX(x++).setY(0).setComp(flaps[i]);
        }

        GlobalTimer.INSTANCE.startTimer();
        return panel;
    }

    private void updateFlapText(final Integer flapValue, final SplitFlap[] flaps) {
        String flapText;
        if (flapValue == null) {
            flapText = "          ";
        } else {
            flapText = NumberFormat.getIntegerInstance().format(flapValue).replace(',', '\'');
        }
        if (flapText.length() < 10)
            flapText = new String(new char[10 - flapText.length()]).replace("\0", " ") + flapText;
        for (int i = 0; i < 10; i++) {
            flaps[i].setText(flapText.toUpperCase().substring(i, i + 1));
        }
    }

    private static JComponent getUndoRedoButton(String translationKey, ObservableList<Executable> commands,
            Supplier<Boolean> execute) {
        // Not a nice hack, but works :)
        JSplitButton undoButton = new JSplitButton(TranslationManager.getInstance().translate(translationKey) + "   ");
        JPopupMenu popupMenu = new JPopupMenu();
        commands.bindTo(undos -> {
            popupMenu.removeAll();

            for (int last = undos.size() - 1; last >= 0; last--) {
                Executable exe = undos.get(last);
                JMenuItem item = new JMenuItem(exe.toString());

                item.addActionListener(l -> {
                    int index = undos.size() - undos.indexOf(item) - 1;
                    for (int i = 1; i <= index; i++) {
                        execute.get();
                    }
                });
                popupMenu.add(item);
            }
        });
        undoButton.setPopupMenu(popupMenu);

        new ComputedValue<Boolean>(() -> {
            return commands.iterator().hasNext();
        }).bindTo(undoButton::setEnabled);

        undoButton.addSplitButtonActionListener(new SplitButtonActionListener() {

            @Override
            public void splitButtonClicked(ActionEvent e, JComponent originalSource) {
                if (originalSource != null && originalSource.getClass() != JSplitButton.class) {
                    System.out.println(originalSource);
                    System.out.println(e);
                }
            }

            @Override
            public void buttonClicked(ActionEvent e) {
                execute.get();
            }
        });

        return undoButton;

    }

    public static JComponent getUndoButton() {
        // Not a nice hack, but works :)
        return getUndoRedoButton("Undo", CommandController.getDefault().getDoneCommands(),
                CommandController.getDefault()::undo);
    }

    public static JComponent getRedoButton() {
        // Not a nice hack, but works :)
        return getUndoRedoButton("Redo", CommandController.getDefault().getRedoCommands(),
                CommandController.getDefault()::redo);
    }
}
