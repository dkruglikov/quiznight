package ua.dp.coldsun.quiznight.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DefaultHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;

import ua.dp.coldsun.quiznight.model.TeamResults;


public class StandingsView implements EntryPoint {
	
	@Override
	public void onModuleLoad() {
		String textData = Assets.INSTANCE.getResults().getText();
		List<TeamResults> data = TeamResults.valueOf(textData);
		ListDataProvider<TeamResults> dataProvider = new ListDataProvider<>(data);
		CellTable<TeamResults> table = new CellTable<>();
		table.addColumn(new TextColumn<TeamResults>() {
			
			@Override
			public String getValue(TeamResults object) {
				return object.getName();
			}
		}, "Team");
		if (!data.isEmpty()) {
			int daysPlayed = data.get(0).getResults().length;
			for (int i = 0; i < daysPlayed; i++) {
				final int index = i;
				table.addColumn(new Column<TeamResults, Number>(new NumberCell()) {
					
					@Override
					public Number getValue(TeamResults object) {
						return object.getResults()[index];
					}
				}, String.valueOf(i));
			}
		}
		table.addColumn(new Column<TeamResults, Number>(new NumberCell()) {
			
			@Override
			public Number getValue(TeamResults object) {
				return object.getTotal();
			}
		}, "Total");
		dataProvider.addDataDisplay(table);
		RootPanel.get("standings").add(table);
	}
}
