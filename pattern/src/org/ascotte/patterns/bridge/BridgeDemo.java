package org.ascotte.patterns.bridge;

public class BridgeDemo {

	public static void main(String[] args) {
		
		Movie movie = new Movie();
		movie.setClassification("Action");
		movie.setTitle("John Wick");
		movie.setRunTime("2:15");
		movie.setYear("2014");
		
		Book book = new Book();
		book.setClassification("Polar");
		book.setTitle("Sans retour");
		book.setAuthor("John Malkovich");
		
		Formatter standardFormatter = new StandardFormatter();
		Formatter htmlFormatter = new HtmlFormatter();
		
		Printer moviePrinter = new MoviePrinter(movie);
		Printer bookPrinter = new BookPrinter(book);
		
		System.out.println(moviePrinter.print(standardFormatter));
		System.out.println(moviePrinter.print(htmlFormatter));
		System.out.println(bookPrinter.print(htmlFormatter));
	}

}
