package com.flexical.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AvailabilityCalculator {

    public static class TimeSlot {
        private String weekdayId;
        private int clientId;
        private String vendorId;
        private String resourceId;
        private LocalTime startTime;
        private LocalTime endTime;

        public TimeSlot(String weekdayId, int clientId, String vendorId, String resourceId, LocalTime startTime, LocalTime endTime) {
            this.weekdayId = weekdayId;
            this.clientId = clientId;
            this.vendorId = vendorId;
            this.resourceId = resourceId;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getWeekdayId() {
            return weekdayId;
        }

        public void setWeekdayId(String weekdayId) {
            this.weekdayId = weekdayId;
        }

        public int getClientId() {
            return clientId;
        }

        public void setClientId(int clientId) {
            this.clientId = clientId;
        }

        public String getVendorId() {
            return vendorId;
        }

        public void setVendorId(String vendorId) {
            this.vendorId = vendorId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }
    }

    public static List<TimeSlot> reduceAvailableTime(List<TimeSlot> availableTimeSlots, List<TimeSlot> bookedTimeSlots) {
        List<TimeSlot> reducedTimeSlots = new ArrayList<>();

        for (TimeSlot availableTimeSlot : availableTimeSlots) {
            List<TimeSlot> bookedSlotsForWeekday = bookedTimeSlots.stream()
                    .filter(slot -> slot.getWeekdayId().equals(availableTimeSlot.getWeekdayId()))
                    .collect(Collectors.toList());

            LocalTime startTime = availableTimeSlot.getStartTime();

            for (TimeSlot bookedTimeSlot : bookedSlotsForWeekday) {
                if (startTime.isBefore(bookedTimeSlot.getStartTime())) {
                    TimeSlot reducedSlot = new TimeSlot(availableTimeSlot.getWeekdayId(), availableTimeSlot.getClientId(), availableTimeSlot.getVendorId(), availableTimeSlot.getResourceId(), startTime, bookedTimeSlot.getStartTime());
                    reducedTimeSlots.add(reducedSlot);
                }

                startTime = bookedTimeSlot.getEndTime();
            }

            if (startTime.isBefore(availableTimeSlot.getEndTime())) {
                TimeSlot reducedSlot = new TimeSlot(availableTimeSlot.getWeekdayId(), availableTimeSlot.getClientId(), availableTimeSlot.getVendorId(), availableTimeSlot.getResourceId(), startTime, availableTimeSlot.getEndTime());
                reducedTimeSlots.add(reducedSlot);
            }
        }

        return reducedTimeSlots;
    }

    public static JSONArray availabilityCalculator(JSONArray jsonArray) {
        // Sample data for available time slots
    	JSONArray resultArray = new JSONArray();
        List<TimeSlot> availableTimeSlots = new ArrayList<>();
        List<TimeSlot> bookedTimeSlots = new ArrayList<>();
    	for(int i=0; i<jsonArray.size(); i++) {
    		JSONObject obj = jsonArray.getJSONObject(i);
    		if(obj.getString("status").equalsIgnoreCase("available")) {
    	        availableTimeSlots.add(new TimeSlot(obj.getString("weekdayId"), obj.getInt("clientId"), obj.getString("vendorId"), obj.getString("resourceId"), LocalTime.parse(obj.getString("startTime")), LocalTime.parse(obj.getString("endTime"))));
    		}else {
    			bookedTimeSlots.add(new TimeSlot(obj.getString("weekdayId"), obj.getInt("clientId"), obj.getString("vendorId"), obj.getString("resourceId"), LocalTime.parse(obj.getString("startTime")), LocalTime.parse(obj.getString("endTime"))));
    		}
    	}

        // Reduce the available time slots based on booked time slots
        List<TimeSlot> reducedTimeSlots = reduceAvailableTime(availableTimeSlots, bookedTimeSlots);

        // Print the reduced time slots
        for (TimeSlot timeSlot : reducedTimeSlots) {
            JSONObject resultObj = new JSONObject();
            resultObj.put("clientId", timeSlot.getClientId());
            resultObj.put("vendorId", timeSlot.getVendorId());
            resultObj.put("resourceId", timeSlot.getResourceId());
            resultObj.put("weekdayId", timeSlot.getWeekdayId());
            resultObj.put("availableStartTime", timeSlot.getStartTime().toString());
            resultObj.put("availableEndTime", timeSlot.getEndTime().toString());
            resultArray.add(resultObj);
        }
		return resultArray;
    }
}
