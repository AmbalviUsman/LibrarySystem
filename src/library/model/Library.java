package library.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Library extends Object implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Book> collection;

	public Library() {
		collection = new ArrayList<Book>();
	}

	public void addBook(Book book) {
		collection.add(book);
	}

	public Book getBookByName(String name) {
		Book v = null;
		Iterator<Book> i = collection.iterator();
		while (i.hasNext()) {
			v = i.next();
			if (v.getTitle().toLowerCase().contentEquals(name.toLowerCase())) {
				return v;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		String total = "\n";
		/*
		 * for (int i=0; i<collection.size(); i++){ Book b = collection.get(i);
		 * total = total + b.toString();
		 * 
		 * }
		 */
		Iterator<Book> i = collection.iterator();
		while (i.hasNext()) {
			Book b = (Book) i.next();
			total = total + b.toString();
		}
		return total;
	}

	public boolean doesISBNAlreadyExist(int isbn) {
		Iterator<Book> i = collection.iterator();
		while (i.hasNext()) {
			if (i.next().getIsbn() == isbn) {
				return true;
			}
		}
		return false;
	}

	public String[][] toStringVector() {
		String[][] total = new String[collection.size()][5];

		for (int i = 0; i < collection.size(); i++) {
			total[i][0] = collection.get(i).getTitle();
			total[i][1] = collection.get(i).getAuthor();
			total[i][2] = collection.get(i).getPrice();
			total[i][3] = collection.get(i).getISBN();
		}
		
		return total;

	}

	public Book getBookByISBN(String isbn) {
		for (int i = 0; i < collection.size(); i++) {
			if(collection.get(i).getISBN().contentEquals(isbn)){
				return collection.get(i);
			}
		}
		return null;
	}

}