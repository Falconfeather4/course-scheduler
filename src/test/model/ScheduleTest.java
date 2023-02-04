package model;

import model.util.CourseTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {
    private Schedule defaultSchedule;
    private Section section1;
    private Section section2;

    @BeforeEach
    public void setup() {
        Weight weight1 = new Weight(1, 1, 1, 1);
        List<String> courseIDs1 = new ArrayList<>(Arrays.asList("CPSC 110", "CPSC 121"));
        List<String> sectionIDs1 =  new ArrayList<>();
        defaultSchedule = new Schedule("s1", courseIDs1, sectionIDs1, weight1, new CourseTestData());

        section1 = new Section("Test Section 1", "Test Course", SectionType.LECTURE,
                "1:00", "3:00", Arrays.asList("Mon, Wed, Fri"), 1, new ArrayList<>());
        section2 = new Section("Test Section 2", "Test Course", SectionType.LECTURE,
                "1:00", "3:00", Arrays.asList("Mon, Wed, Fri"), 1, new ArrayList<>());
    }

    @Test
    void testCloneSameValue() {
        defaultSchedule.setScore(4);
        Schedule s1Clone = defaultSchedule.makeCopy();
        assertEquals(defaultSchedule.getName(), s1Clone.getName());
        assertEquals(defaultSchedule.getCourseIDs(), s1Clone.getCourseIDs());
        assertEquals(defaultSchedule.getSectionIDs(), s1Clone.getSectionIDs());
        assertEquals(defaultSchedule.getWeight().getBalanceWeight(), s1Clone.getWeight().getBalanceWeight());
        assertEquals(defaultSchedule.getWeight().getCompactWeight(), s1Clone.getWeight().getCompactWeight());
        assertEquals(defaultSchedule.getWeight().getPreferredEndTime(), s1Clone.getWeight().getPreferredEndTime());
        assertEquals(defaultSchedule.getWeight().getPreferredStartTime(), s1Clone.getWeight().getPreferredStartTime());
        assertEquals(defaultSchedule.getScore(), s1Clone.getScore());
    }

    @Test
    void testCloneDifferentObject() {
        defaultSchedule.setScore(10);
        Schedule s1Clone = defaultSchedule.makeCopy();
        assertNotEquals(defaultSchedule, s1Clone);

        s1Clone.getCourseIDs().add("200");
        // defaultSchedule.setSectionIDs(Arrays.asList("400"));
        s1Clone.getSectionIDs().add("100");
        Weight weight2 = new Weight(2,2,2,2);
        defaultSchedule.setWeight(weight2);

        assertNotEquals(defaultSchedule.getCourseIDs(), s1Clone.getCourseIDs());
        assertNotEquals(defaultSchedule.getSectionIDs(), s1Clone.getSectionIDs());
        assertNotEquals(defaultSchedule.getWeight(), s1Clone.getWeight());

    }

    @Test
    void tryAddSection() {
        Section testS1 = defaultSchedule.getCourseData().getSection("Test Section 1");
        Section testS2 = defaultSchedule.getCourseData().getSection("Test Section 2");
        Section testS3 = defaultSchedule.getCourseData().getSection("Test Section 3");
        ArrayList<String> startingSectionIDs = new ArrayList<>();
        startingSectionIDs.add("Test Section 1");
        defaultSchedule.setSectionIDs(startingSectionIDs);
        assertTrue(defaultSchedule.tryAddSection(testS2));
        assertEquals("Test Section 2", defaultSchedule.getSectionIDs().get(1));
        defaultSchedule.getSectionIDs().remove(1);
        assertFalse(defaultSchedule.tryAddSection(testS3));
        assertEquals(1, defaultSchedule.getSectionIDs().size());
    }

    @Test
    void testToString() {
        defaultSchedule.setSectionIDs(Arrays.asList("CPSC 110", "CPSC 121"));
        assertEquals("Schedule{sectionIDs=[CPSC 110, CPSC 121]}", defaultSchedule.toString());
    }
}