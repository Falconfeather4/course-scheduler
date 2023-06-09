package model.util;

import model.Schedule;
import model.Section;

import java.util.*;

// SCHEDULER GUIDE
// Each course can have one or multiple sections. A root section is any lecture sections, and each root section in the
// course data holds references to all of its possible antirequisites, such as labs or tutorials etc. If you look in the
// sections.json file, you can see that the first section is the lecture PHYS 117 101, which has a list of
// antirequisites which are all the tutroials that correspond to PHYS 117 101.

// This scheduler works by first taking in an empty Schedule, which has a list of courses. Using the courses, it does a
// backtracking search to find all the possible root section combinations. It then takes in this list of schedules
// (which at this moment only contains root sections) and then performs another backtracking search to schedule
// all the antirequisites, and then returns the list of schedules

// The backtracking search algorithm is the one we learned in CPSC 110, except it uses a loop (in the scheduleCourses()
// method) to simulate the recursion. (I tried using recursion but because Java doesn't have tail recursion
// optimization, the program kept crashing).




// Contains methods for course scheduling.
public class Scheduler {

    private Scheduler() {
    }

    // REQUIRES: schedule is either empty (not yet finished nor given a score) or contains only root sections.
    // EFFECTS: makes a list of all possible schedules from a starting schedule, calculates their scores, and then
    //          returns a list of schedules sorted byt their score from high to low
    public static List<Schedule> scheduleAndCalculateScore(Schedule schedule) {
        List<Schedule> result = scheduleCourses(schedule);
        for (Schedule s: result) {
            s.calculateScore();
        }

        Collections.sort(result, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule o1, Schedule o2) {
                return Float.compare(o2.getScore(), o1.getScore());
            }
        });
        return result;
    }

    // EFFECTS: Given an empty schedule, returns a list of all possible finished schedules. A finished schedule is a
    //          schedule that has one root section ID in its sectionIDs field corresponding to each courseID, as well
    //          as one sectionID from every corresponding antirequisite. If the resulting list is empty, it means that
    //          it was not possible to schedule the given courses
    private static List<Schedule> scheduleCourses(Schedule schedule0) {
        List<Schedule> result0 = new ArrayList<>();
        scheduleRootSections(schedule0, new LinkedList<>(), schedule0.getCourseIDs(), new LinkedList<>(), result0);

        List<Schedule> result = new ArrayList<>();
        for (Schedule schedule : result0) {
            List<Schedule> tempResult = new ArrayList<>();
            List<List<String>> sectionIDsSet = schedule.getAllAntirequisits();
            LinkedList<Schedule> scheduleWorkList = new LinkedList<>();
            LinkedList<List<List<String>>> sectionIDsWorkList = new LinkedList<>();
            int sectionIdsSize = sectionIDsSet.size();
            scheduleSecondSections(schedule, scheduleWorkList,
                    sectionIDsSet, sectionIDsWorkList, sectionIdsSize, tempResult);
            while (!scheduleWorkList.isEmpty()) {
                scheduleSecondSections(
                        scheduleWorkList.pollFirst(),
                        scheduleWorkList,
                        sectionIDsWorkList.pollFirst(),
                        sectionIDsWorkList,
                        sectionIdsSize,
                        tempResult);
            }
            result.addAll(tempResult);
        }
        return result;
    }

    // REQUIRES: scheduleWorkList is empty, courseIDs is a copy of schedule.getCourseIDs(), courseIDsWorkList is empty,
    //           resultSoFar is empty.
    // MODIFIES: resultSoFar
    // EFFECTS: Schedules root sections, puts a list of schedules where the root sections of the course IDs
    //          for each schedule has been scheduled into resultSoFar
    private static void scheduleRootSections(
            Schedule schedule,
            LinkedList<Schedule> scheduleWorkList,
            List<String> courseIDs, // get course IDs from schedule.
            LinkedList<List<String>> courseIDsWorkList, // tandem worklist for courseIDs
            List<Schedule> resultSoFar) {

        if (schedule.getSectionIDs().size() == schedule.getCourseIDs().size()) {
            resultSoFar.add(schedule);
        } else {
            List<Schedule> nextSchedules = Scheduler.makeNextSchedules(schedule, courseIDs.get(0));
            scheduleWorkList.addAll(nextSchedules);
            courseIDsWorkList.addAll(Collections.nCopies(nextSchedules.size(), courseIDs.subList(1, courseIDs.size())));
        }
        if (!scheduleWorkList.isEmpty()) {
            scheduleRootSections(
                    scheduleWorkList.pollFirst(),
                    scheduleWorkList,
                    courseIDsWorkList.pollFirst(),
                    courseIDsWorkList,
                    resultSoFar);
        }
    }

    // REQUIRES: scheduleWorkList is empty, sectionIDs is a list of list of section IDs for the antirequisites of each
    //           root section already in the schedule, sectionIDsWorkList is empty, sectionIDsSize is the size of
    //           sectionIDsSet, resultSoFar is empty.
    // MODIFIES: resultSoFar
    // EFFECTS: Schedules antirequisite sections, puts a list of completed schedules into resultSoFar
    private static void scheduleSecondSections(
            Schedule schedule,
            LinkedList<Schedule> scheduleWorkList,
            List<List<String>> sectionIDsSet, // list of list of section IDs. ex. List(allLabs1, allLabs2)
            LinkedList<List<List<String>>> sectionIDsWorkList,// tandem worklist for sectionIDsSet
            int sectionIDsSize,
            List<Schedule> resultSoFar) {

        if (schedule.getSectionIDs().size() == schedule.getCourseIDs().size() + sectionIDsSize) {
            resultSoFar.add(schedule);
        } else {
            List<Schedule> nextSchedules = Scheduler.makeNextSchedulesWithAntirequsite(schedule, sectionIDsSet.get(0));
            scheduleWorkList.addAll(nextSchedules);
            sectionIDsWorkList.addAll(Collections.nCopies(nextSchedules.size(),
                    sectionIDsSet.subList(1, sectionIDsSet.size())));
        }

    }


    // EFFECTS: makes the next schedules (next layer of search tree) by using each section from the section
    // ids and making one copy of the new schedule for every section.
    private static List<Schedule> makeNextSchedules(Schedule schedule0, String courseID) {
        List<Schedule> result = new ArrayList<>();
        List<Section> sections = schedule0.getCourseData().getRootSections(courseID, schedule0.getTerm()); // contains
        // no sections when BIOL 111 term 2 is used
        for (Section section : sections) {
            Schedule clonedSchedule = schedule0.makeCopy();
            if (clonedSchedule.tryAddSection(section)) {
                result.add(clonedSchedule);
            }
        }
        return result;
    }


    // EFFECTS: makes the next schedules (next layer of search tree) by using each section from the section
    // ids and making one copy of the new schedule for every section. This method has the same functionality as the
    // method above, but accepts different parameters.
    private static List<Schedule> makeNextSchedulesWithAntirequsite(Schedule schedule, List<String> sectionIDs) {
        List<Schedule> result = new ArrayList<>();
        for (String sectionID : sectionIDs) {
            Schedule clonedSchedule = schedule.makeCopy();
            Section section = schedule.getCourseData().getSection(sectionID);
            if (clonedSchedule.tryAddSection(section)) {
                result.add(clonedSchedule);
            }
        }
        return result;
    }


}
