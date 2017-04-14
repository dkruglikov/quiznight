package ua.dp.coldsun.quiznight.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

import ua.dp.coldsun.quiznight.model.TeamResults;

public class RankCell extends NumberCell {

	private static final String[] COLORS = {"IndianRed", "Silver", "Gold"};
	private static final Templates TEMPLATES = GWT.create(Templates.class);
	private final Number[] bestResults;
	private final byte day;
	
	public RankCell(Collection<TeamResults> teamResults, byte day) {
		this.day = day;
		bestResults = new Number[COLORS.length];
		Collection<TeamResults> sortedResults = new TreeSet<>(Collections.reverseOrder(TeamResults.getComparatorForDay(day)));
		sortedResults.addAll(teamResults);
		Iterator<TeamResults> iterator = sortedResults.iterator();
		for (int i = bestResults.length - 1; i >= 0 && iterator.hasNext(); i--) {
			bestResults[i] = iterator.next().getResults()[day];
		}
	}
	
	@Override
	public void render(Context context, Number value, SafeHtmlBuilder sb) {
		int index = Arrays.binarySearch(bestResults, value);
		if (index >= 0) {
			SafeHtml safeHtml = SimpleSafeHtmlRenderer.getInstance().render(NumberFormat.getDecimalFormat().format(value));
			sb.append(TEMPLATES.cell(SafeStylesUtils.forTrustedBackgroundColor(COLORS[index]), safeHtml));
		} else {
			super.render(context, value, sb);
		}
	}
	
	interface Templates extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
		SafeHtml cell(SafeStyles styles, SafeHtml value);
	}
}
