package validators;

import java.time.LocalDate;
import java.util.ArrayList;

import com.developer.silverheavens.entities.Booking;
import com.developer.silverheavens.entities.Rate;
import com.developer.silverheavens.entities.User;

public class CustomValidator {
	
	//validate new Rate
	public static ValidatorResult validateNewRate(Rate newRate) {
		//fields
		boolean testPassed=true;
		ArrayList<String> errorMsgs = new ArrayList<>();
		
		//nights should be > 0
		if(newRate.getNights()<=0) {
			errorMsgs.add("NIGHTS Should be a posivive mumber.");
			testPassed = false;
		}
		
		//dates should not be null
		if(newRate.getStayDateFrom()==null || newRate.getStayDateTo()==null) {
			errorMsgs.add("STAY_DATE_FROM and STAY_DATE_TO should not be NULL.");
			testPassed = false;
		}
		
		//from date should be after today
		if(newRate.getStayDateFrom().isBefore(LocalDate.now())) {
			errorMsgs.add("STAY_FROM_DATE("+newRate.getStayDateFrom()+") should be a future date. ");
			testPassed = false;
		}
		
		//from date should be before to date
		if(newRate.getStayDateFrom()!=null && newRate.getStayDateTo()!=null &&  newRate.getStayDateFrom().isAfter(newRate.getStayDateTo())) {
			errorMsgs.add("STAY_FROM_DATE("+newRate.getStayDateFrom()+") should be before STAY_TO_DATE "+(newRate.getStayDateTo())+". ");
			testPassed = false;
		}
		
		//from date should be before to date
//		if(newRate.getClosedDate()!=null) {
//			errorMsgs.add("CLOSED_DATE should not be provided by the user. ");
//			testPassed = false;
//		}
		
		//value should be positive
		if(newRate.getValue()<=0) {	
			errorMsgs.add("VALUE Should be a posivive number.");
			testPassed = false;
		}
		
		return new ValidatorResult(testPassed, errorMsgs);
		
	}
	
	
	//validate booking
	public static ValidatorResult validateNewBooking(Booking newBooking) {
		boolean testPassed=true;
		ArrayList<String> errorMsgs = new ArrayList<>();
		
		//nights should be > 0
		if(newBooking.getNights()<=0) {
			errorMsgs.add("NIGHTS Should be a posivive mumber.");
			testPassed = false;
		}
		
		//nights should be > 0
		long days = newBooking.getStayDateTo().toEpochDay()-newBooking.getStayDateFrom().toEpochDay();
		if(days%newBooking.getNights()!=0) {
			errorMsgs.add("total nights stay Should be a in multiple of "+newBooking.getNights());
			testPassed = false;
		}
		
		//from date should be after today
		if(newBooking.getStayDateFrom().isBefore(LocalDate.now())) {
			errorMsgs.add("STAY_FROM_DATE("+newBooking.getStayDateFrom()+") should be a future date. ");
			testPassed = false;
		}
		
		//from date should be before to date
		if(newBooking.getStayDateFrom()!=null && newBooking.getStayDateTo()!=null &&  !newBooking.getStayDateTo().isAfter(newBooking.getStayDateFrom())) {
			errorMsgs.add("STAY_DATE_FROM("+newBooking.getStayDateFrom()+") should be before STAY_DATE_TO("+(newBooking.getStayDateTo())+"). ");
			testPassed = false;
		}
		
		return new ValidatorResult(testPassed, errorMsgs);
	}
	
	public static ValidatorResult validateNewUser(User user) {
		//fields
		boolean testPassed=true;
		ArrayList<String> errorMsgs = new ArrayList<>();
		
		//blank user name
		if(user.getUsername().length()==0) {
			errorMsgs.add("USERNAME cannot be blank.");
			testPassed = false;
		}
		
		//format user name
		if(!user.getUsername().matches("^(.+)@(\\S+)$")) {
			errorMsgs.add("USERNAME is not in correct format (user@mail.com).");
			testPassed = false;
		}
		
		//blank pass
		if(user.getPassword().length()==0) {
			errorMsgs.add("PASSWORD cannot be blank.");
			testPassed = false;
		}
				
		//pass not strong
		if(!checkPassword(user.getPassword())) {
			errorMsgs.add("Password must contain Symbol,Upper case letter, Lower case letter and digit.");
			testPassed = false;
		}
		
		return new ValidatorResult(testPassed, errorMsgs);
		
	}
	
	private static boolean checkPassword(String pass) {
		boolean uppercaseFlag = false;
		boolean digitFlag = false;
		boolean lowercaseFlag = false;
		boolean symbolFlag = false;
		for(int i=0;i<pass.length();i++) {
			char ch = pass.charAt(i);
			System.out.println(ch);
			System.out.println(Character.isUpperCase(ch));
			System.out.println(Character.isDigit(ch));
			System.out.println(Character.isLowerCase(ch));
			System.out.println(!Character.isLetterOrDigit(ch));
			if(!uppercaseFlag && Character.isUpperCase(ch)) {
				uppercaseFlag = true;
			}
			if(!digitFlag && Character.isDigit(ch)) {
				digitFlag = true;
			}
			if(!lowercaseFlag && Character.isLowerCase(ch)) {
				lowercaseFlag = true;
			}
			if(!symbolFlag && !Character.isLetterOrDigit(ch)) {
				symbolFlag = true;
			}
		}
		System.out.println(uppercaseFlag +"-"+ digitFlag +"-"+ lowercaseFlag +"-"+ symbolFlag);
		return uppercaseFlag && digitFlag && lowercaseFlag && symbolFlag;
	}
}
