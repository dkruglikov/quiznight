package ua.dp.coldsun.quiznight.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import ua.dp.coldsun.quiznight.model.TeamResults;

public class StandingsView implements EntryPoint {
	
	private final Column<TeamResults, Number> COLUMN_TOTAL_DIFF = createTotalDiffColumn();
	private final Column<TeamResults, Number> COLUMN_TOTAL_BEST_DIFF = createTotalBestDiffColumn();
	private final Column<TeamResults, Number> COLUMN_TOTAL_BEST_TO_WORST = createTotalBestToWorstColumn();
	private final Comparator<TeamResults> COMPARATOR_BEST_TO_WORST = createBestToWorstComparator();
	private TeamResults selection;
	
	@Override
	public void onModuleLoad() {
		String textData = Assets.INSTANCE.getResults().getText();
		List<TeamResults> data = TeamResults.valueOf(textData);
		Collections.sort(data, Collections.reverseOrder(TeamResults.COMPARATOR_TOTAL_BEST));
		ListDataProvider<TeamResults> dataProvider = new ListDataProvider<>(data);
		AbstractCellTable<TeamResults> table = new CellTable<>();
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
		table.addColumn(totalBestColumn, "Best");
		sortHandler.setComparator(totalBestColumn, TeamResults.COMPARATOR_TOTAL_BEST);
		Column<TeamResults, Number> totalPossibleBestColumn = createTotalPossibleBestColumn();
		table.addColumn(totalPossibleBestColumn, "Possible Best");
		sortHandler.setComparator(totalPossibleBestColumn, new Comparator<TeamResults>() {
			
			@Override
			public int compare(TeamResults o1, TeamResults o2) {
				return Float.compare(o1.getTotalPossibleBest(), o2.getTotalPossibleBest());
			}
		});
		sortHandler.setComparator(COLUMN_TOTAL_BEST_TO_WORST, COMPARATOR_BEST_TO_WORST);
		table.addColumn(createCountableMinColumn(), "Count. Min.");
		table.addColumnSortHandler(sortHandler);
		table.getColumnSortList().push(totalBestColumn);
		table.setSelectionModel(new SingleSelectionModel<>(), new DefaultSelectionEventManager<TeamResults>(null) {
			
			@Override
			protected void handleSelectionEvent(CellPreviewEvent<TeamResults> event, DefaultSelectionEventManager.SelectAction action, SelectionModel<? super TeamResults> selectionModel) {
				TeamResults oldSelection = null;
				if (selectionModel instanceof SingleSelectionModel) {
					oldSelection = ((SingleSelectionModel<TeamResults>) selectionModel).getSelectedObject();
				}
				super.handleSelectionEvent(event, action, selectionModel);
				if (selectionModel instanceof SingleSelectionModel) {
					selection = ((SingleSelectionModel<TeamResults>) selectionModel).getSelectedObject();
				}
				if (selection != oldSelection) {
					if (selection == null) {
						table.removeColumn(COLUMN_TOTAL_DIFF);
						table.removeColumn(COLUMN_TOTAL_BEST_DIFF);
						table.removeColumn(COLUMN_TOTAL_BEST_TO_WORST);
					} else if (oldSelection == null) {
						table.addColumn(COLUMN_TOTAL_DIFF, "Diff");
						table.addColumn(COLUMN_TOTAL_BEST_DIFF, "Diff (Best)");
						table.addColumn(COLUMN_TOTAL_BEST_TO_WORST, "Best to Worst");
					} else if (isBestToWorstSelected(table)) {
						ColumnSortEvent.fire(table, table.getColumnSortList());
					} else {
						table.redraw();
					}
				}
			}
		});
		table.setVisibleRange(0, data.size());
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
	
	private Column<TeamResults, Number> createCountableMinColumn() {
		Column<TeamResults, Number> column = new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				return object.getCountableMinimum();
			}
		};
		return column;
	}
	
	private Column<TeamResults, Number> createTotalDiffColumn() {
		return new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				if (selection == null) {
					return 0;
				}
				return selection.getTotal() - object.getTotal();
			}
		};
	}
	
	private Column<TeamResults, Number> createTotalBestDiffColumn() {
		return new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				if (selection == null) {
					return 0;
				}
				return selection.getTotalBest()- object.getTotalBest();
			}
		};
	}
	
	private Column<TeamResults, Number> createTotalBestToWorstColumn() {
		Column<TeamResults, Number> column = new Column<TeamResults, Number>(new NumberCell()) {

			@Override
			public Number getValue(TeamResults object) {
				return object == selection ? object.getTotalPossibleBest() : object.getTotalBest();
			}
		};
		column.setSortable(true);
		column.setDefaultSortAscending(false);
		return column;
	}
	
	private Comparator<TeamResults> createBestToWorstComparator() {
		return new Comparator<TeamResults>() {

			@Override
			public int compare(TeamResults o1, TeamResults o2) {
				float v1 = o1 == selection ? o1.getTotalPossibleBest() : o1.getTotalBest();
				float v2 = o2 == selection ? o2.getTotalPossibleBest() : o2.getTotalBest();
				return Float.compare(v1, v2);
			}
		};
	}
	
	private boolean isBestToWorstSelected(AbstractCellTable<TeamResults> table) {
		for (int i = 0; i < table.getColumnSortList().size(); i++) {
			if (table.getColumnSortList().get(i).getColumn() == COLUMN_TOTAL_BEST_TO_WORST) {
				return true;
			}
		}
		return false;
	}
}
