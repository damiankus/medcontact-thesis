package com.medcontact.data.validation;

import com.medcontact.data.model.ScheduleTimeSlot;

public class ScheduleTimeSlotValidator extends DataValidatorHelper<ScheduleTimeSlot> {
	
	@Override
	public ValidationResult validate(ScheduleTimeSlot scheduleTimeSlot) {
		ValidationResult result = new ValidationResult();
		
		if (scheduleTimeSlot.getStartTime().getTime() >=
				scheduleTimeSlot.getEndTime().getTime()) {
			
			result.addError("Consultation time slot end time before start time");
			
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}
