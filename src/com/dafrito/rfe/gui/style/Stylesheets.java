package com.dafrito.rfe.gui.style;

import java.awt.Color;
import java.awt.Font;

import com.dafrito.rfe.script.exceptions.Exception_InternalError;

public class Stylesheets {
	public static Color getColor(String colorString) {
		// W3C CSS standard colors: aqua, black, blue, fuchsia, gray, green, lime, maroon, navy, olive, purple, red, silver, teal, white, and yellow
		if (colorString.charAt(0) != '#') {
			if ("aqua".equals(colorString.toLowerCase())) {
				return Color.decode("#00ffff");
			} else if ("black".equals(colorString.toLowerCase())) {
				return Color.decode("#000000");
			} else if ("blue".equals(colorString.toLowerCase())) {
				return Color.decode("#0000ff");
			} else if ("fuchsia".equals(colorString.toLowerCase())) {
				return Color.decode("#ff00ff");
			} else if ("gray".equals(colorString.toLowerCase())) {
				return Color.decode("#808080");
			} else if ("green".equals(colorString.toLowerCase())) {
				return Color.decode("#008000");
			} else if ("lime".equals(colorString.toLowerCase())) {
				return Color.decode("#00ff00");
			} else if ("maroon".equals(colorString.toLowerCase())) {
				return Color.decode("#800000");
			} else if ("navy".equals(colorString.toLowerCase())) {
				return Color.decode("#000080");
			} else if ("olive".equals(colorString.toLowerCase())) {
				return Color.decode("#808000");
			} else if ("purple".equals(colorString.toLowerCase())) {
				return Color.decode("#800080");
			} else if ("red".equals(colorString.toLowerCase())) {
				return Color.decode("#ff0000");
			} else if ("silver".equals(colorString.toLowerCase())) {
				return Color.decode("#c0c0c0");
			} else if ("teal".equals(colorString.toLowerCase())) {
				return Color.decode("#008080");
			} else if ("white".equals(colorString.toLowerCase())) {
				return Color.decode("#ffffff");
			} else if ("yellow".equals(colorString.toLowerCase())) {
				return Color.decode("#ffff00");
			}
			colorString = "#" + colorString;
		}
		if (colorString.length() == 4) {
			colorString = "#" + colorString.charAt(1) + colorString.charAt(1) + colorString.charAt(2) + colorString.charAt(2) + colorString.charAt(3) + colorString.charAt(3);
		}
		try {
			return Color.decode(colorString);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	public static String getColorName(Color color) {
		// W3C CSS standard colors: aqua, black, blue, fuchsia, gray, green, lime, maroon, navy, olive, purple, red, silver, teal, white, and yellow
		if (color.equals(Color.decode("#00ffff"))) {
			return "Aqua";
		} else if (color.equals(Color.decode("#000000"))) {
			return "Black";
		} else if (color.equals(Color.decode("#0000ff"))) {
			return "Blue";
		} else if (color.equals(Color.decode("#ff00ff"))) {
			return "Fuchsia";
		} else if (color.equals(Color.decode("#808080"))) {
			return "Gray";
		} else if (color.equals(Color.decode("#008000"))) {
			return "Green";
		} else if (color.equals(Color.decode("#00ff00"))) {
			return "Lime";
		} else if (color.equals(Color.decode("#800000"))) {
			return "Maroon";
		} else if (color.equals(Color.decode("#000080"))) {
			return "Navy";
		} else if (color.equals(Color.decode("#808000"))) {
			return "Olive";
		} else if (color.equals(Color.decode("#800080"))) {
			return "Purple";
		} else if (color.equals(Color.decode("#ff0000"))) {
			return "Red";
		} else if (color.equals(Color.decode("#c0c0c0"))) {
			return "Silver";
		} else if (color.equals(Color.decode("#008080"))) {
			return "Teal";
		} else if (color.equals(Color.decode("#ffffff"))) {
			return "White";
		} else if (color.equals(Color.decode("#ffff00"))) {
			return "Yellow";
		} else {
			return "Color(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
		}
	}

	public static String getFontStyleName(int style) {
		switch (style) {
		case Font.PLAIN:
			return "Plain";
		}
		throw new Exception_InternalError("Invalid default");
	}
}
