package ui.gui.calendar;

import model.Section;

import javax.swing.*;
import java.awt.*;

// A class representing a section panel (the green block on the calendar)
public class SectionPanel extends JPanel {
    private Section section;
    private JLabel label;

    // EFFECTS: constructs a panel that displays the name, building, and term of the section
    public SectionPanel(Section section) {
        this.section = section;
        this.setBackground(new Color(0xdff0d8));
        setBorder(BorderFactory.createMatteBorder(1, 10, 1, 1, new Color(0x808080)));
        label = new JLabel();
        label.setText("<html><a href=\"link\" target=\"_blank\">" + section.getSectionID() + "</a></html>");
        label.setForeground(new Color(0x0000));
        this.add(label);
    }
}
