package ua.dp.coldsun.quiznight.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

public class TeamResults extends JavaScriptObject {
	
	public static List<TeamResults> valueOf(String s) {
		String[] teamsResults = s.split("\n");
		List<TeamResults> c = new ArrayList<>(teamsResults.length);
		for (String teamResults : teamsResults) {
			c.add(JsonUtils.safeEval(teamResults));
		}
		return c;
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
}
