package ui.gui.calendar;

import model.Schedule;
import model.Section;
import model.util.HelperUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// A class representing a calendar
public class CalendarPanel extends JPanel implements ActionListener {
    public static final int MAX_COLS = 5;
    public static final int MAX_ROWS = 22;
    private Schedule schedule;
    private boolean isReadOnly;
    private List<SectionPanel> sectionPanelList = new ArrayList<>();

    // EFFECTS: constructs a new calendar
    public CalendarPanel(Schedule schedule, boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        this.schedule = schedule;
        setWeekdayDisplay();
        setTimeDisplay();
        setBackground();
        if (this.schedule != null) {
            setSections();
        }
    }

    // EFFECTS: sets the background of the calendar by filling it with background blocks
    private void setBackground() {
        for (int i = 1; i <= MAX_COLS; i++) {
            for (int j = 1; j <= MAX_ROWS; j++) {
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.weightx = 0.5;
                c.weighty = 0.5;
                c.gridx = i;
                c.gridy = j;
                this.add(new BackgroundBlock(j == MAX_ROWS, i == MAX_COLS,
                        i % 2 == 0, j % 2 == 0), c);
            }
        }
    }

    // EFFECTS: sets the weekday display
    private void setWeekdayDisplay() {
        this.setBackground(Color.WHITE);
        this.setLayout(new GridBagLayout());
        for (int i = 1; i <= MAX_COLS; i++) {
            Label label = new Label(DayOfWeek.of(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            label.setAlignment(Label.CENTER);
            label.setForeground(Color.DARK_GRAY);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.gridx = i;
            c.gridy = 0;
            this.add(label, c);
        }
    }

    // EFFECTS: sets the time display
    private void setTimeDisplay() {
        for (int i = 0; i < MAX_ROWS / 2; i++) {
            Label label = new Label(HelperUtil.minutesToTime(480 + (i * 60)));
            label.setAlignment(Label.CENTER);
            label.setForeground(Color.DARK_GRAY);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0;
            c.weighty = 0;
            c.gridx = 0;
            c.gridy = i * 2;
            c.gridheight = 2;
            c.ipadx = 10;
            this.add(label, c);
        }
    }

    // EFFECTS: puts the sections into the calendar
    private void setSections() {
        for(SectionPanel sectionPanel: sectionPanelList){
            this.remove(sectionPanel);
        }

        for (int i = 1; i <= MAX_COLS; i++) {
            String weekdayKey = DayOfWeek.of(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            for (Section section : schedule.getSortedSections().get(weekdayKey)) {
                SectionPanel sectionPanel = new SectionPanel(section, isReadOnly);
                sectionPanelList.add(sectionPanel);
                sectionPanel.addActionListener(this);
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.weightx = 0.5;
                c.weighty = 0.5;
                c.gridx = i;
                c.gridy = 1 + (section.getStartTimeInMinutes() - 480) / 30;
                c.gridheight = (section.getEndTimeInMinutes() - section.getStartTimeInMinutes()) / 30;
                c.insets = new Insets(2, 2, 2, 2);
                this.add(sectionPanel, c, 0);
            }
        }
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String sectionId = ((JMenuItem)e.getSource()).getName();
        schedule.getSectionIDs().remove(sectionId);
        schedule.calculateScore();
        setSections();
    }
}
