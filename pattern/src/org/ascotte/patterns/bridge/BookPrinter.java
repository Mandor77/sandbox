package org.ascotte.patterns.bridge;

import java.util.ArrayList;
import java.util.List;

public class BookPrinter extends Printer {

	Book book;
	
	public BookPrinter(Book book) {
		this.book = book;
	}
	
	@Override
	protected String getHeader() {
		return this.book.getClassification();
	}

	@Override
	protected List<Detail> getDetails() {
		List<Detail> details = new ArrayList<>();
		details.add(new Detail("Title", this.book.getTitle()));
		details.add(new Detail("Author", this.book.getAuthor()));
		return details;
	}

}
