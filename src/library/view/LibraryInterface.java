package library.view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class LibraryInterface extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private AddBookPanel abp;
	private BrowseLibraryPanel blp;
	private JTabbedPane jtb;
	private String filler;

	public LibraryInterface(String title){
		super(title);
		
		jtb = new JTabbedPane();
		abp = new AddBookPanel();
		blp = new BrowseLibraryPanel();
		
		filler = "      "; // 6 spaces
		jtb.addTab(filler + filler + " Add Book " + filler + filler, abp);
		jtb.addTab(filler + " Browse Library " + filler, blp);
		
		add(jtb);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(400, 460);
		setResizable(false);
	}
	
	public AddBookPanel getAddBookPanel(){
		return abp;
	}
	
	public BrowseLibraryPanel getBrowseLibraryPanel(){
		return blp;
	}
	
	public JTabbedPane getTabbedPane(){
		return jtb;
	}
	
	public String getFiller(){
		return filler;
	}
	
}