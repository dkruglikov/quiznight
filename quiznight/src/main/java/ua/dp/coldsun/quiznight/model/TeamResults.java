package ua.dp.coldsun.quiznight.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

public class TeamResults extends JavaScriptObject {
	
	public static final Comparator<TeamResults> COMPARATOR_TOTAL_BEST = new Comparator<TeamResults>() {
		
		@Override
		public int compare(TeamResults o1, TeamResults o2) {
			return Float.compare(o1.getTotalBest(), o2.getTotalBest());
		}
	};
	private static final Comparator<TeamResults>[] COMPARATORS_DAY = new Comparator[TeamResults.DAYS_COUNT];
	private static final byte DAYS_COUNT = 10;
	private static final byte DAYS_WORST_COUNT = 2;
	private static final Float MIN_RESULT = 0F;
	private static final Float MAX_RESULT = 68F;
	
	public static List<TeamResults> valueOf(String s) {
		String[] teamsResults = s.split("\n");
		List<TeamResults> c = new ArrayList<>(teamsResults.length);
		for (String teamResults : teamsResults) {
			c.add(JsonUtils.safeEval(teamResults));
		}
		return c;
	}
	
	public static Comparator<TeamResults> getComparatorForDay(byte index) {
		if (COMPARATORS_DAY[index] == null) {
			COMPARATORS_DAY[index] = new Comparator<TeamResults>() {
				
				@Override
				public int compare(TeamResults o1, TeamResults o2) {
					return Float.compare(o1.getResults()[index], o2.getResults()[index]);
				}
			};
		}
		return COMPARATORS_DAY[index];
	}
	
	protected TeamResults() {
	}
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native float[] getResults() /*-{
		return this.results;
	}-*/;
	
	public final float getTotal() {
		float[] results = getResults();
		float total = 0;
		for (byte i = 0; i < results.length; i++) {
			total += results[i];
		}
		return total;
	}
	
	public final float getTotalBest() {
		List<Float> results = new ArrayList<>(DAYS_COUNT);
		float[] playedResults = getResults();
		int playedDays = playedResults.length;
		for (int i = 0; i < playedDays; i++) {
			results.add(playedResults[i]);
		}
		results.addAll(Collections.nCopies(DAYS_COUNT - playedDays, MIN_RESULT));
		Collections.sort(results);
		float totalBest = 0;
		for (byte i = DAYS_WORST_COUNT; i < results.size(); i++) {
			totalBest += results.get(i);
		}
		return totalBest;
	}
	
	public final float getTotalPossibleBest() {
		List<Float> results = new ArrayList<>(DAYS_COUNT);
		float[] playedResults = getResults();
		int playedDays = playedResults.length;
		for (int i = 0; i < playedDays; i++) {
			results.add(playedResults[i]);
		}
		results.addAll(Collections.nCopies(DAYS_COUNT - playedDays, MAX_RESULT));
		Collections.sort(results);
		float totalBest = 0;
		for (byte i = DAYS_WORST_COUNT; i < results.size(); i++) {
			totalBest += results.get(i);
		}
		return totalBest;
	}
	
	public final float getCountableMinimum() {
		float[] results = getResults();
		if (results.length == 0) {
			return 0;
		}
		List<Float> resultsList = new ArrayList<>(results.length);
		for (float result : results) {
			resultsList.add(result);
		}
		Collections.sort(resultsList);
		return resultsList.get(Math.min(resultsList.size() - 1, DAYS_WORST_COUNT));
	}
}
