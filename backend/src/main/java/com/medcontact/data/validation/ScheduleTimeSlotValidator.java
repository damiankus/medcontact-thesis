package com.medcontact.data.validation;

import com.medcontact.data.model.ScheduleTimeSlot;

public class ScheduleTimeSlotValidator extends DataValidatorHelper<ScheduleTimeSlot> {
	
	@Override
	public boolean validate(ScheduleTimeSlot scheduleTimeSlot) {
		return (scheduleTimeSlot.getStartTime().getTime() < 
					scheduleTimeSlot.getEndTime().getTime());
	}
}
