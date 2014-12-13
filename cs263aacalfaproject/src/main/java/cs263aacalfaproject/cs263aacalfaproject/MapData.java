package cs263aacalfaproject.cs263aacalfaproject;

import com.google.appengine.api.images.Image;

/**
 * Holds a map image information
 * @author Andre Abreu Calfa
 *
 */
public class MapData {
	private Long size;
	private String filename;
	private int width;
	private int height;
	private Image.Format format;

	public MapData() {
	}

	/**
	 * Represents all map image information 
	 * @param name Map name.
	 * @param siz Map file size.
	 * @param w Map image width.
	 * @param h Map image height.
	 * @param fmt Map image format.
	 */
	public MapData(String name, Long siz, int w, int h, Image.Format fmt) {
		this.filename = name;
		this.size = siz;
		this.width = w;
		this.height = h;
		this.format = fmt;
	}

	/**
	 * Returns the map name.
	 * @return
	 */
	public String getName() {
		return filename;
	}

	/**
	 * Returns the map file size.
	 * @return
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * Returns the map image width
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the map image height.
	 * @return
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the map image format.
	 * @return
	 */
	public Image.Format getFormat() {
		return format;
	}

	/**
	 * Sets the map name.
	 * @param newname New map name.
	 * @return
	 */
	public String setName(String newname) {
		filename = newname;
		return filename;
	}

	/**
	 * Sets the map file size.
	 * @param newsiz New file size.
	 * @return
	 */
	public Long setSize(Long newsiz) {
		size = newsiz;
		return size;
	}

	/**
	 * Sets the map image width.
	 * @param val New width.
	 * @return
	 */
	public int setWidth(int val) {
		width = val;
		return width;
	}
	
	/**
	 * Sets the map image height.
	 * @param val New height.
	 * @return
	 */
	public int setHeight(int val) {
		height = val;
		return height;
	}
	
	/**
	 * Sets the map image format.
	 * @param fmt New format.
	 * @return
	 */
	public Image.Format setFormat(Image.Format fmt) {
		format = fmt;
		return format;
	}
}
