import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CourseArray {
	private Course elements[];
	private int period;
	
	public CourseArray(int numOfCourses, int numOfSlots) {
		period = numOfSlots;
		elements = new Course[numOfCourses];
		for (int i = 1; i < elements.length; i++) 
			elements[i] = new Course();
	}
	
	public void readClashes(String filename) {
		try {
			BufferedReader file = new BufferedReader(new FileReader(filename));
			StringTokenizer line = new StringTokenizer(file.readLine());
			int count = line.countTokens(), i, j, k;
			int index[];
			while (count > 0) {
				if (count > 1) {
					index = new int[count];
					i = 0;
					while (line.hasMoreTokens()) {
						index[i] = Integer.parseInt(line.nextToken());
						i++;
					}

					for (i = 0; i < index.length; i++)
						for (j = 0; j < index.length; j++)
							if (j != i)
							{
								k = 0;
								while (k < elements[index[i]].clashesWith.size() && elements[index[i]].clashesWith.elementAt(k) != elements[index[j]])
									k++;
								if (k == elements[index[i]].clashesWith.size())
									elements[index[i]].addClash(elements[index[j]]);
							}
				}
				line = new StringTokenizer(file.readLine());
				count = line.countTokens();
			}
			file.close();
		}
		catch (Exception e) {
		}
	}

	public Course[] getElements(){
		return elements;
	}
	public int length() {
		return elements.length;
	}
	
	public int status(int index) {
		return elements[index].clashSize();
	}
	
	public int slot(int index) {
		return elements[index].mySlot;
	}
	
	public void setSlot(int index, int newSlot) {
		elements[index].mySlot = newSlot;
	}
	
	public int maxClashSize(int index) {
		return elements[index] == null || elements[index].clashesWith.isEmpty() ? 0 : elements[index].clashesWith.size();
	}
	
	public int clashesLeft() {
		int result = 0;
		for (int i = 1; i < elements.length; i++)
			result += elements[i].clashSize();
		
		return result;
	}
	
	public void iterate(int shifts) {
		for (int index = 1; index < elements.length; index++) {
			elements[index].setForce();
			for (int move = 1; move <= shifts && elements[index].force != 0; move++) { 
				elements[index].setForce();
				elements[index].shift(period);
			}
		}
	}
	
	public void printResult() {
		for (int i = 1; i < elements.length; i++)
			System.out.println(i + "\t" + elements[i].mySlot);
	}

	public int[] slotStatus(int slot){
		int[] result = new int[2];
		for(int i = 1; i < elements.length; i++){
			if(elements[i].mySlot == slot){
				result[0]++;
				result[1] += elements[i].clashSize();
			}
		}
		return result;
	}

	public void printSlotStatus(){
		int status[] = null;
		for(int slot = 0; slot < period; slot++){
			status = slotStatus(slot);
			System.out.println(slot + "\t" + status[0] + "\t" + status[1]);
		}
	}

	public int[] getTimeSlot(int index){
		int[] pattern = new int[elements.length];
		for(int i = 0; i < pattern.length; i++){
			pattern[i] = slot(i) == index ? 1 : -1;
		}
		return pattern;
	}

	public List<int[]> getClashedTimeSlots() {
		List<int[]> clashedTimeSlots = new ArrayList<int[]>();
		for (int slot = 0; slot < period; slot++) {
			int[] status = slotStatus(slot);
			int clashCount = status[1];
			if(clashCount > 0){
				clashedTimeSlots.add(getTimeSlot(slot));
			}
		}
		return clashedTimeSlots;
	}

	public List<int[]> getClashFreeTimeSlots(int minNumOfCourses){
		List<int[]> clashFreeTimeSlot = new ArrayList<int[]>();
		for(int slot = 0; slot < period; slot++){
			int[] status = slotStatus(slot);
			int clashCount = status[1];
			int courseCount = status[0];
			if(courseCount > minNumOfCourses && clashCount == 0){
				clashFreeTimeSlot.add(getTimeSlot(slot));
			}
		}
		return clashFreeTimeSlot;
	}
}
