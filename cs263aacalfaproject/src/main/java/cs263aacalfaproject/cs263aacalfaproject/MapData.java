package cs263aacalfaproject.cs263aacalfaproject;

import com.google.appengine.api.images.Image;

public class MapData {
	private Long size;
	private String filename;
	private int width;
	private int height;
	private Image.Format format;

	public MapData() {
	}

	public MapData(String name, Long siz, int w, int h, Image.Format fmt) {
		this.filename = name;
		this.size = siz;
		this.width = w;
		this.height = h;
		this.format = fmt;
	}

	public String getName() {
		return filename;
	}

	public Long getSize() {
		return size;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Image.Format getFormat() {
		return format;
	}

	public String setName(String newname) {
		filename = newname;
		return filename;
	}

	public Long setSize(Long newsiz) {
		size = newsiz;
		return size;
	}

	public int setWidth(int val) {
		width = val;
		return width;
	}
	
	public int setHeight(int val) {
		height = val;
		return height;
	}
	
	public Image.Format setFormat(Image.Format fmt) {
		format = fmt;
		return format;
	}
}
