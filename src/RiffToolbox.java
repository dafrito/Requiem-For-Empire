import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class RiffToolbox {
	public final static double DOUBLE_MIN = 1 * Math.pow(10, -10);
	private static Random m_random;
	private static boolean m_toggleDebugSpew;

	public static boolean areEqual(Point point, double a, double b) {
		if (point instanceof Point_Spherical) {
			return areEqual(Point.System.SPHERICAL, a, b);
		}
		return areEqual(Point.System.EUCLIDEAN, a, b);
	}

	public static boolean areEqual(Point.System point, double a, double b) {
		switch (point) {
		case SPHERICAL:
			if ((areEqual(Point.System.EUCLIDEAN, a, 180.0d) || areEqual(Point.System.EUCLIDEAN, a, 0.0d)) && (areEqual(Point.System.EUCLIDEAN, b, 180.0d) || areEqual(Point.System.EUCLIDEAN, b, 0.0d))) {
				return true;
			}
			return (Math.abs(a - b) < RiffToolbox.DOUBLE_MIN);
		default:
			return (Math.abs(a - b) < RiffToolbox.DOUBLE_MIN);
		}
	}

	/******************************************************************************************/
	/************************ MATH FUNCTIONS **************************************************/
	// Converts degress to radians.
	public static double convertDegreesToRadians(double degrees) {
		return degrees * Math.PI / 180;
	}

	// Helper Function.
	public static String displayList(Collection list) {
		return displayList(list, "item", "items");
	}

	// Helper Function.
	public static String displayList(Collection list, String singular) {
		return displayList(list, singular, new String(singular + "s"));
	}

	public static String displayList(Collection list, String singular, String plural) {
		return displayList(list, singular, plural, 0);
	}

	// Takes a list and returns a string containing its contents in a readable form.
	public static String displayList(Collection list, String singular, String plural, int nestedVal) {
		String string = new String();
		if (list == null || list.isEmpty()) {
			string += "\n" + tab(nestedVal) + "This list is empty.";
			return string;
		} else if (list.size() == 1) {
			string += "\n" + tab(nestedVal) + "This list contains one " + singular;
		} else {
			string += "\n" + tab(nestedVal) + "This list contains " + list.size() + " " + plural;
		}
		Iterator iter = list.iterator();
		int value = 1;
		while (iter.hasNext()) {
			Object obj = iter.next();
			string += "\n" + value + ". " + tab(nestedVal);
			value++;
			if (obj instanceof Collection) {
				string += "Nested list:" + displayList((Collection) obj, singular, plural, nestedVal + 1);
			} else {
				string += obj;
			}
		}
		return string;
	}

	public static String displayList(Object[] array) {
		String string = new String();
		if (array.length == 0) {
			string += "\nThis list is empty.";
			return string;
		} else if (array.length == 1) {
			string += "\nThis list contains one item";
		} else {
			string += "\nThis list contains " + array.length + " items";
		}
		for (int i = 0; i < array.length; i++) {
			string += "\n" + array[i];
		}
		return string;
	}

	public static double getDistance(double ax, double ay, double bx, double by) {
		return Math.sqrt(Math.pow(bx - ax, 2) + Math.pow(by - ay, 2));
	}

	// 2-dimensional distance formula for Points (Fallback distance formula when testing Sphere vs. Euclidean)
	public static double getDistance(Point firstPoint, Point secondPoint) {
		return getDistance(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
	}

	// 3-dimensional distance formula for Point_Euclideans
	public static double getDistance(Point_Euclidean firstPoint, Point_Euclidean secondPoint) {
		//( (x-a)2 + (y - b)2 + (z - c)2 )1/2
		double testDouble = Math.sqrt(Math.pow((secondPoint.getX() - firstPoint.getX()), 2) + Math.pow((secondPoint.getY() - firstPoint.getY()), 2) + Math.pow((secondPoint.getZ() - firstPoint.getZ()), 2));
		return testDouble;
	}

	// Spherical distance formula for Point_Sphericals
	public static double getDistance(Terrestrial terrestrial, Point_Spherical firstPoint, Point_Spherical secondPoint) {
		double firstLat = Math.toRadians(firstPoint.getLatitudeDegrees());
		double secondLat = Math.toRadians(secondPoint.getLatitudeDegrees());
		double firstLong = Math.toRadians(firstPoint.getLongitudeDegrees());
		double secondLong = Math.toRadians(secondPoint.getLongitudeDegrees());
		double temp = Math.pow(Math.sin((secondLat - firstLat) / 2), 2) + Math.cos(firstLat) * Math.cos(secondLat) * Math.pow(Math.sin((secondLong - firstLong) / 2), 2);
		return terrestrial.getRadius() * 2 * Math.asin(Math.min(1, Math.sqrt(temp)));
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		if (ext == null) {
			ext = new String("");
		}
		return ext;
	}

	/******************************************************************************************/
	/************************ FILE I/0 ********************************************************/
	// Gets a single line from the provided stream. Returns null if at the end of the file.
	public static String getLineFromStream(FileReader stream) {
		if (stream == null) {
			return null;
		}
		String string = new String();
		try {
			while (true) {
				int inputNum = stream.read();
				if (inputNum == -1) {
					if (string.length() == 0) {
						return null;
					} else {
						return string;
					}
				}
				char inputChar = (char) inputNum;
				if (Character.isISOControl(inputChar)) {
					if (inputChar == '\n') {
						return string;
					}
					continue;
				}
				string += inputChar;
			}
		} catch (IOException e) {
			Debugger.printException(e);
			return null;
		}
	}

	// Returns a static Random class.
	public static Random getRandom() {
		if (RiffToolbox.m_random == null) {
			RiffToolbox.m_random = new Random();
		}
		return RiffToolbox.m_random;
	}

	public static boolean isGreaterThan(double greater, double less) {
		return (greater - less > RiffToolbox.DOUBLE_MIN);
	}

	public static boolean isGreaterThanOrEqualTo(Object point, double greater, double less) {
		if (point instanceof Point_Spherical) {
			return isGreaterThanOrEqualTo(Point.System.SPHERICAL, greater, less);
		}
		return isGreaterThanOrEqualTo(Point.System.EUCLIDEAN, greater, less);
	}

	public static boolean isGreaterThanOrEqualTo(Point.System point, double greater, double less) {
		return (isGreaterThan(greater, less) || areEqual(point, greater, less));
	}

	public static boolean isLessThan(double less, double greater) {
		return (less - greater < -RiffToolbox.DOUBLE_MIN);
	}

	public static boolean isLessThanOrEqualTo(Object point, double greater, double less) {
		if (point instanceof Point_Spherical) {
			return isLessThanOrEqualTo(Point.System.SPHERICAL, greater, less);
		}
		return isLessThanOrEqualTo(Point.System.EUCLIDEAN, greater, less);
	}

	public static boolean isLessThanOrEqualTo(Point.System point, double less, double greater) {
		return (isLessThan(less, greater) || areEqual(point, less, greater));
	}

	// Returns a string with a ASCII border around the baseString. The border consists of the character provided by printChar.
	public static String printBorder(String baseString, String printChar) {
		String string = new String();
		for (int i = baseString.length() + 2; i > 0; i--) {
			string += printChar;
		}
		string += "\n " + baseString + "\n";
		for (int i = baseString.length() + 2; i > 0; i--) {
			string += printChar;
		}
		string += "\n";
		return string;
	}

	// Prints a symbol n times.
	public static String printLine(String symbol, int times) {
		String string = new String();
		for (int i = 0; i < times; i++) {
			string += symbol;
		}
		string += "\n";
		return string;
	}

	// Takes a string and creates a new string with a underline beneath the string provided. The underline is formed by a string of the printChar the length of the baseString.
	public static String printUnderline(String baseString, String printChar) {
		String string = new String(baseString + "\n");
		for (int i = baseString.length(); i > 0; i--) {
			string += printChar;
		}
		string += "\n";
		return string;
	}

	/******************************************************************************************/
	/************************ STRING FUNCTIONS ************************************************/
	public static String tab(int val) {
		return tab(val, "   ");
	}

	public static String tab(int val, String tab) {
		String string = new String();
		for (int i = 0; i < val; i++) {
			string += tab;
		}
		return string;
	}
}
