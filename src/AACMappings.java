import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & Sarah Deschamps
 *
 */
public class AACMappings implements AACPage {

	private AACCategory current;
	private AssociativeArray<String,AACCategory> mappings;
	private AACCategory def;

	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 * @throws NullKeyException 
	 * @throws Exception 
	 */
	public AACMappings(String filename) throws IOException {
		this.def = new AACCategory("");
		this.current = this.def;
		this.mappings = new AssociativeArray<String, AACCategory>();
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String ln = br.readLine();
		while (ln != null) {
			String[] words = ln.split(" ");
			if (words.length <= 1) {
				ln = br.readLine();
				continue;
			} // if
			if (ln.charAt(0) == '>') {
				this.current.addItem(words[0].substring(1), 
														 ln.substring(words[0].length() + 1));
			} else {
				try {
					this.current = new AACCategory(ln.substring(words[0].length() + 1));
					this.mappings.set(words[0], this.current);
				} catch (NullKeyException e) {
					// do nothing
				} // try-catch
			} // if-else
			ln = br.readLine();
		} // while
		this.current = this.def;
		br.close();
	} // AACMappings(String)
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 */
	public String select(String imageLoc) throws NoSuchElementException {
		if (this.current.hasImage(imageLoc)) {
			return this.current.select(imageLoc);
		} else {
			try {
				if (this.current.equals(this.mappings.get(imageLoc))) {
					throw new NoSuchElementException();
				} // if
				this.current = this.mappings.get(imageLoc);
				return "";
			} catch (Exception e) {
				throw new NoSuchElementException();
			} // try-catch
		} // if-else
	} // select(String)
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		if (this.current.getCategory().equals("")) {
			return this.mappings.getKeys();
		} // if
		return current.getImageLocs();
	} // getImageLocs()
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.current = this.def;
	} // reset()
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		String[] categories = this.mappings.getKeys();
		PrintWriter p;
		try {
			p = new PrintWriter(filename);
			for (int i = 0; i < categories.length; i++) {
				try {
					p.println(categories[i] + " " + this.mappings.get(categories[i]));
					String[] listings = this.mappings.get(categories[i]).getImageLocs();
					for (int j = 0; j < listings.length; j++) {
						p.println(">" + listings[j] + " " + this.mappings.get(categories[i]).select(listings[j]));
					} // for
				} catch (KeyNotFoundException e) {
					continue;
				} // try-catch
			} // for
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	} // writeToFile(String)
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		if (this.current.equals(this.def)) {
			try {
				this.mappings.set(imageLoc, new AACCategory(text));
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} // try-catch
		} // if
		current.addItem(imageLoc, text);
	} // addItem(String, String)


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return current.getCategory();
	} // getCategory()


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		if (this.mappings.hasKey(imageLoc)) {
			return true;
		} // if
		String[] keys = this.mappings.getKeys();
		for (int i = 0; i < keys.length; i++) {
			try {
				AACCategory category = this.mappings.get(keys[i]);
				if (category.hasImage(imageLoc)) {
					return true;
				}
			} catch (KeyNotFoundException e) {
				// move forward in the loop
			} // try-catch
		} // for 
		return false;
	} // hasImage(String)
} // class AACMappings
