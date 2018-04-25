package library.controller;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import library.model.Book;
import library.model.Library;
import library.model.VIM;
import library.view.AddBookPanel;
import library.view.BrowseLibraryPanel;
import library.view.LibraryInterface;
import library.view.LoadScreen;
import library.view.MyTableModel;

public class LibrarySystem implements ChangeListener, ActionListener {

	private LibraryInterface screen;
	private AddBookPanel abp;
	private BrowseLibraryPanel blp;
	private LoadScreen ls;

	private JFileChooser chooser;
	private FileFilter filter, filter2;
	private int resultCode;
	private File vimFile, saveFile, libFile;

	private Library lib;
	private Book book;
	private List<VIM> vimCache;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectInputStream in;

	private String[][] dataBook, dataFile;

	private String fileName;

	private boolean exit;

	private String[] validFileTypes = { ".wav", ".mp3", ".avi", ".mp4", ".png",
			".jpeg" };
	private String validFileTypeReminder;

	public LibrarySystem() {
		initEventAttributes();

		screen = new LibraryInterface("Library System");
		abp = screen.getAddBookPanel();
		blp = screen.getBrowseLibraryPanel();

		screen.getTabbedPane().addChangeListener(this);
		abp.addActionListener(this);
		blp.addActionListener(this);

		ls = new LoadScreen("Welcome!");
		ls.addActionListener(this);
		ls.setVisible(true);

	}

	private void initEventAttributes() {
		chooser = new JFileChooser();
		filter = new FileNameExtensionFilter("Video/Image/Music Files", "wav",
				"mp3", "avi", "mp4", "png", "jpeg");
		filter2 = new FileNameExtensionFilter("Library Files", "ser");
		chooser.addChoosableFileFilter(filter);
		chooser.addChoosableFileFilter(filter2);

		lib = new Library();

		exit = false;

		vimCache = new ArrayList<VIM>();
		vimFile = null;
		saveFile = null;
		libFile = null;
		validFileTypeReminder = "Valid file types end with .wav, .mp3, .avi, .mp4, .png, .jpeg!";
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == abp.getButtonBrowse()) {
			openChooserAndSetTheVIMFile();
		} else if (event.getSource() == abp.getButtonAddFile()) {
			addVimFileTovimCache();
		} else if (event.getSource() == abp.getButtonAddBook()) {
			addVimFilesInvimCacheToBookAndAddBookToLibrary();
			reloadDataBook();
			reloadDataFile();
		} else if (event.getSource() == abp.getButtonListAllBooks()) {
			listAllBooksInLibrary();
		} else if (event.getSource() == abp.getButtonSave()) {
			save();
		} else if (event.getSource() == abp.getButtonSaveAndQuit()) {
			saveAndQuit();
		} else if (event.getSource() == blp.getButtonOpenBook()) {
			openBook();
		} else if (event.getSource() == blp.getButtonDeleteBook()) {

		} else if (event.getSource() == blp.getButtonOpenFilek()) {
			openFile();
		} else if (event.getSource() == blp.getButtonDeleteFile()) {

		} else if (event.getSource() == blp.getButtonSave()) {
			save();
		} else if (event.getSource() == blp.getButtonSaveAndQuit()) {
			saveAndQuit();
		} else if (event.getSource() == ls.getButtonLoad()) {
			// Clear he data inside the tables if any
			reloadDataBook();
			reloadDataFile();
			loadLibrary();
			chooser.setFileFilter(filter);
		} else if (event.getSource() == ls.getButtonNew()) {
			ls.dispose();
			screen.setVisible(true);
		} else if (event.getSource() == ls.getButtonExit()) {
			System.exit(0);
		}
	}

	private void openFile() {
		int row, column;
		VIM v;
		String fileName;
		File file;

		row = ((JTable) blp.getFileTable()).getSelectedRow();
		column = ((JTable) blp.getFileTable()).getSelectedColumn();
		fileName = ((JTable) blp.getFileTable()).getValueAt(row, column)
				.toString();

		v = book.getVIMByName(fileName);
		try {
			file = new File(v.getName());
			fos = new FileOutputStream(file);
			fos.write(v.getData());// write this file
			fos.close();
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openBook() {
		int row, column;
		String isbn;

		row = ((JTable) blp.getBookTable()).getSelectedRow();
		column = 3; // ISBN Column
		isbn = ((JTable) blp.getBookTable()).getValueAt(row, column).toString();
		book = lib.getBookByISBN(isbn);

		dataFile = book.toStringVectorFiles();
		reloadDataFile();
	}

	private void loadLibrary() {
		chooser.setFileFilter(filter2);
		resultCode = chooser.showOpenDialog(screen);
		if (resultCode == JFileChooser.APPROVE_OPTION) {
			libFile = chooser.getSelectedFile();
			try {
				fis = new FileInputStream(libFile);
				in = new ObjectInputStream(fis);
				lib = (Library) in.readObject();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			ls.dispose();

			// LOAD THE BOOK
			reloadDataBook();

			screen.setVisible(true);
		}
	}

	private void reloadDataBook() {
		while (((MyTableModel) blp.getBookTable().getModel()).getRowCount() > 0) {
			((MyTableModel) blp.getBookTable().getModel()).removeRow(0);
		}
		dataBook = lib.toStringVector();
		for (int i = 0; i < dataBook.length; i++) {
			((MyTableModel) blp.getBookTable().getModel()).addRow(dataBook[i]);
		}
	}

	private void reloadDataFile() {
		while (((MyTableModel) blp.getFileTable().getModel()).getRowCount() > 0) {
			((MyTableModel) blp.getFileTable().getModel()).removeRow(0);
		}
		if (dataFile != null)
			for (int i = 0; i < dataFile.length; i++) {
				((MyTableModel) blp.getFileTable().getModel()).insertRow(i,
						dataFile[i]);
			}
	}

	private void saveAndQuit() {
		save();
		if (exit)
			System.exit(0);
	}

	private void save() {
		fileName = JOptionPane.showInputDialog(screen,
				"Enter file name to save as...", "Save",
				JOptionPane.INFORMATION_MESSAGE);
		if (fileName != null) {
			if (!fileName.trim().contentEquals("")) {
				FileOutputStream fos = null;
				ObjectOutputStream out = null;
				try {
					saveFile = new File(fileName.trim() + ".ser");
					if (!saveFile.exists()) {
						fos = new FileOutputStream(saveFile);
						out = new ObjectOutputStream(fos);
						out.writeObject(lib);
						fos.close();
						out.close();
						exit = true;
					} else {
						int resultCode = JOptionPane.showConfirmDialog(screen,
								"File name already exist.\nOverwrite it?",
								"Warning", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
						if (resultCode == JOptionPane.YES_OPTION) {
							fos = new FileOutputStream(saveFile);
							out = new ObjectOutputStream(fos);
							out.writeObject(lib);
							fos.close();
							out.close();
							exit = true;
						} else {
							abp.getTextAreaLog().append("\n> Save cancelled.");
							exit = false;
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				abp.getTextAreaLog().append("\n> Save cancelled.");
				exit = false;
			}
		} else {
			abp.getTextAreaLog().append("\n> Save cancelled.");
			exit = false;
		}
	}

	private void listAllBooksInLibrary() {
		abp.getTextAreaLog().setText("> Listing all books in library...");
		abp.getTextAreaLog().append(" " + lib.toString());
	}

	private void addVimFilesInvimCacheToBookAndAddBookToLibrary() {
		boolean ISBNAlreadyExist = false;
		boolean AllFieldsAreFilled = false;
		int isbn = 0;
		double price = 0.0;
		Book b;

		if (!abp.getTextFieldIsbn().getText().trim().contentEquals("")
				&& !abp.getTextFieldTitle().getText().trim().contentEquals("")
				&& !abp.getTextFieldAuthor().getText().trim().contentEquals("")
				&& !abp.getTextFieldPrice().getText().trim().contentEquals("")) {
			AllFieldsAreFilled = true;
		}

		if (AllFieldsAreFilled) {
			try {
				isbn = Integer
						.parseInt(abp.getTextFieldIsbn().getText().trim());
				price = Double.parseDouble(abp.getTextFieldPrice().getText()
						.trim());

				ISBNAlreadyExist = lib.doesISBNAlreadyExist(isbn);
				if (ISBNAlreadyExist) {
					JOptionPane.showMessageDialog(screen, isbn
							+ " already exist!\nPlease use another isbn!");
				} else {
					b = new Book(isbn,
							abp.getTextFieldTitle().getText().trim(), abp
									.getTextFieldAuthor().getText().trim(),
							price);

					for (int i = 0; i < vimCache.size(); i++) {
						b.addVIM(vimCache.get(i));
					}

					lib.addBook(b);
					abp.getTextFieldIsbn().setText("");
					abp.getTextFieldTitle().setText("");
					abp.getTextFieldAuthor().setText("");
					abp.getTextFieldPrice().setText("");
					abp.getTextAreaLog().append(
							"\n> " + b.getTitle()
									+ " has been added to the library!");

					vimCache = new ArrayList<VIM>();
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(screen,
						"Isbn or Price is not a number!");
			}
		} else {
			JOptionPane.showMessageDialog(screen,
					"Please fill out all non-optional fields");
		}
	}

	private void addVimFileTovimCache() {
		if (vimFile != null) {
			for (int i = 0; i < validFileTypes.length; i++) {
				if (abp.getTextFieldFile().getText().trim()
						.endsWith(validFileTypes[i])) {
					byte[] data = new byte[(int) vimFile.length()];
					try {
						fis = new FileInputStream(vimFile);
						fis.read(data);
						fis.close();
					} catch (FileNotFoundException e) {
						JOptionPane
								.showMessageDialog(screen, "File not found!");
					} catch (IOException e) {
						JOptionPane.showMessageDialog(screen,
								"Error reading file!");
					}
					VIM v = new VIM(abp.getTextFieldFile().getText().trim(),
							data);
					vimCache.add(v);
					vimFile = null;
					abp.getTextAreaLog().append(
							"\n> " + abp.getTextFieldFile().getText().trim()
									+ " is ready to be added to book.");
					abp.getTextFieldFile().setText("optional");
					return;
				}
			}
			JOptionPane
					.showMessageDialog(screen,
							"Something went wrong!\nPlease click browse again and choose your file.");

		} else
			JOptionPane.showMessageDialog(screen, abp.getTextFieldFile()
					.getText()
					+ " is not a valid file type!\n"
					+ validFileTypeReminder);
	}

	private void openChooserAndSetTheVIMFile() {
		resultCode = chooser.showOpenDialog(screen);
		if (resultCode == JFileChooser.APPROVE_OPTION) {
			vimFile = chooser.getSelectedFile();
			abp.getTextFieldFile().setText(vimFile.getName());
		}
	}

	@Override
	// Called when tab changes
	public void stateChanged(ChangeEvent arg0) {
		// from add book tab browse library tab
		if (screen.getTabbedPane().getSelectedIndex() == 1)
			screen.setSize(440, 452);
		else
			// from browse library tab to add book tab
			screen.setSize(400, 460);

	}

} // LibrarySystem End here
