package ua.dp.coldsun.quiznight.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;

import ua.dp.coldsun.quiznight.model.TeamResults;

public class StandingsView implements EntryPoint {

	@Override
	public void onModuleLoad() {
		String textData = Assets.INSTANCE.getResults().getText();
		List<TeamResults> data = TeamResults.valueOf(textData);
		Collections.sort(data, Collections.reverseOrder(TeamResults.COMPARATOR_TOTAL_BEST));
		ListDataProvider<TeamResults> dataProvider = new ListDataProvider<>(data);
		CellTable<TeamResults> table = new CellTable<>();
		dataProvider.addDataDisplay(table);
		ColumnSortEvent.ListHandler<TeamResults> sortHandler = new ColumnSortEvent.ListHandler<>(dataProvider.getList());
		table.addColumn(createRankColumn(), "Rank");
		Column<TeamResults, String> nameColumn = createNameColumn();
		table.addColumn(nameColumn, "Team");
		if (!data.isEmpty()) {
			int daysPlayed = data.get(0).getResults().length;
			for (byte i = 0; i < daysPlayed; i++) {
				Column<TeamResults, Number> resultColumn = createResultColumn(dataProvider.getList(), i);
				table.addColumn(resultColumn, String.valueOf(i + 1));
				sortHandler.setComparator(resultColumn, TeamResults.getComparatorForDay(i));
			}
		}
		Column<TeamResults, Number> totalColumn = createTotalColumn();
		table.addColumn(totalColumn, "Total");
		sortHandler.setComparator(totalColumn, new Comparator<TeamResults>() {
			
			@Override
			public int compare(TeamResults o1, TeamResults o2) {
				return Float.compare(o1.getTotal(), o2.getTotal());
			}
		});
		Column<TeamResults, Number> totalBestColumn = createTotalBestColumn();
		table.addColumn(totalBestColumn, "Total Best");
		sortHandler.setComparator(totalBestColumn, TeamResults.COMPARATOR_TOTAL_BEST);
		Column<TeamResults, Number> totalPossibleBestColumn = createTotalPossibleBestColumn();
		table.addColumn(totalPossibleBestColumn, "Total Possible Best");
		sortHandler.setComparator(totalPossibleBestColumn, new Comparator<TeamResults>() {
			
			@Override
			public int compare(TeamResults o1, TeamResults o2) {
				return Float.compare(o1.getTotalPossibleBest(), o2.getTotalPossibleBest());
			}
		});
		table.addColumnSortHandler(sortHandler);
		table.getColumnSortList().push(totalBestColumn);
		RootPanel.get("standings").add(table);
	}

	private Column<TeamResults, Number> createRankColumn() {
		return new Column<TeamResults, Number>(new NumberCell() {

			@Override
			public void render(Cell.Context context, Number value, SafeHtmlBuilder sb) {
				int row = context.getIndex();
				super.render(context, row + 1, sb);
			}
		}) {

			@Override
			public Number getValue(TeamResults object) {
				return null;
			}
		};
	}

	private Column<TeamResults, String> createNameColumn() {
		Column<TeamResults, String> column = new TextColumn<TeamResults>() {

			@Override
			public String getValue(TeamResults object) {
				return object.getName();
			}
		};
		return column;
	}

	private Column<TeamResults, Number> createResultColumn(Collection<TeamResults> teamResults, byte index) {
		Column<TeamResults, Number> column = new Column<TeamResults, Number>(new RankCell(teamResults, index)) {

			@Override
			public Number getValue(TeamResults object) {
				return object.getResults()[index];
			}
		};
		column.setSortable(true);
		column.setDefaultSortAscending(false);
		return column;
	}

	private Column<TeamResults, Number> createTotalColumn() {
		Column<TeamResults, Number> column = new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				return object.getTotal();
			}
		};
		column.setSortable(true);
		column.setDefaultSortAscending(false);
		return column;
	}
	
	private Column<TeamResults, Number> createTotalBestColumn() {
		Column<TeamResults, Number> column = new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				return object.getTotalBest();
			}
		};
		column.setSortable(true);
		column.setDefaultSortAscending(false);
		return column;
	}
	
	private Column<TeamResults, Number> createTotalPossibleBestColumn() {
		Column<TeamResults, Number> column = new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				return object.getTotalPossibleBest();
			}
		};
		column.setSortable(true);
		column.setDefaultSortAscending(false);
		return column;
	}
}
