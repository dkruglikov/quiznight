package ua.dp.coldsun.quiznight.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Assets extends ClientBundle {
	
	Assets INSTANCE = GWT.create(Assets.class);
	
	@Source(value = "ua/dp/coldsun/quiznight/data/results.txt")
	TextResource getResults();
}
