package edu.oregonstate.mist.coursesapi.core

import com.fasterxml.jackson.annotation.JsonFormat
import edu.oregonstate.mist.api.jsonapi.MetaObject

import java.time.LocalDate
import java.time.LocalTime

class ClassSchedule {
    String academicYear
    String academicYearDescription
    String courseReferenceNumber
    String courseSubject
    String courseSubjectDescription
    String courseNumber
    String courseTitle
    String sectionNumber
    String term
    String termDescription
    String scheduleDescription
    String scheduleType
    Integer creditHours
    Integer waitlistAvailable
    Integer waitlistCapacity
    Integer waitlistCount
    List<Faculty> faculty

}

class ClassSchedules {
    List<ClassSchedule> classSchedules
    MetaObject metaObject
}

class Faculty {
    String osuID
    String name
    String email
    Boolean primary
}

class MeetingTime {
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate beginDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalTime beginTime
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate endDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalTime endTime
    String room
    String building
    String buildingDescription
    String campus
    BigDecimal hoursPerWeek
    Integer creditHourSession
    String scheduleType
    List<String> weeklySchedule

    static fromBackendMeetingTime(BackendMeetingTime backendMeetingTime) {
        backendMeetingTime.with {
            new MeetingTime(
                    beginDate: startDate,
                    beginTime: beginTime,
                    endDate: endDate,
                    endTime: endTime,
                    room: room,
                    building: building,
                    buildingDescription: buildingDescription,
                    campus: campusDescription,
                    hoursPerWeek: hoursWeek,
                    creditHourSession: creditHourSession,
                    scheduleType: meetingScheduleType,
                    weeklySchedule: parseWeeklySchedule(backendMeetingTime)
            )
        }
    }

    private static List<String> parseWeeklySchedule(BackendMeetingTime backendMeetingTime) {
        List<String> weeklySchedule = []

        backendMeetingTime.with {
            if (monday) {
                weeklySchedule.add("M")
            }
            if (tuesday) {
                weeklySchedule.add("T")
            }
            if (wednesday) {
                weeklySchedule.add("W")
            }
            if (thursday) {
                weeklySchedule.add("Th")
            }
            if (friday) {
                weeklySchedule.add("F")
            }
            if (saturday) {
                weeklySchedule.add("Sa")
            }
            if (sunday) {
                weeklySchedule.add("Su")
            }
        }

        weeklySchedule
    }
}