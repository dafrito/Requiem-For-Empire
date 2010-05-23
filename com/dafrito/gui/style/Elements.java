package com.dafrito.gui.style;

import java.awt.Color;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.util.RiffJavaToolbox;

public interface Elements {

    // Color elements
    public static class StylesheetBackgroundColorElement extends StylesheetElement implements Nodeable {
        private Color m_color;

        public StylesheetBackgroundColorElement(Color backgroundColor) {
            this.m_color = backgroundColor;
        }

        public Color getColor() {
            return this.m_color;
        }

        @Override
        public String getElementName() {
            return " background-color";
        }

        public boolean nodificate() {
            assert LegacyDebugger.open("Background Color Stylesheet-Element");
            assert LegacyDebugger.addNode("Color: " + RiffJavaToolbox.getColorName(this.m_color));
            assert LegacyDebugger.close();
            return true;
        }
    }

    public static class StylesheetColorElement extends StylesheetElement implements Nodeable {
        private Color m_color;

        public StylesheetColorElement(Color color) {
            this.m_color = color;
        }

        public StylesheetColorElement(String colorString) {
            this.m_color = RiffJavaToolbox.getColor(colorString);
        }

        public Color getColor() {
            return this.m_color;
        }

        @Override
        public String getElementName() {
            return " color";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Color Stylesheet-Element", "Color: " + RiffJavaToolbox.getColorName(this.m_color));
            return true;
        }
    }

    // Font elements
    public static class StylesheetFontElement extends StylesheetElement implements Nodeable {
        private String m_fontName;

        public StylesheetFontElement(String fontName) {
            this.m_fontName = fontName;
        }

        public String getFontName() {
            return this.m_fontName;
        }

        @Override
        public String getElementName() {
            return " font";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Font Stylesheet-Element", "Font Name: " + this.m_fontName);
            return true;
        }
    }

    public static class StylesheetFontSizeElement extends StylesheetElement implements Nodeable {
        private int m_fontSize;

        public StylesheetFontSizeElement(int fontSize) {
            this.m_fontSize = fontSize;
        }

        public int getFontSize() {
            return this.m_fontSize;
        }

        @Override
        public String getElementName() {
            return " font-size";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Font Size Stylesheet-Element", "Font size: " + this.m_fontSize);
            return true;
        }
    }

    public static class StylesheetFontStyleElement extends StylesheetElement implements Nodeable {
        private int m_style;

        public StylesheetFontStyleElement(int style) {
            this.m_style = style;
        }

        public int getStyle() {
            return this.m_style;
        }

        @Override
        public String getElementName() {
            return " font-style";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Font Style Stylesheet-Element", "Font-Size: "
                + RiffJavaToolbox.getFontStyleName(this.m_style));
            return true;
        }
    }

    // Height elements
    public static abstract class StylesheetHeightElement extends StylesheetElement {
        public abstract Object getMagnitude();
    }

    public static class StylesheetAbsoluteHeightElement extends StylesheetHeightElement implements Nodeable {
        private Integer m_magnitude;

        public StylesheetAbsoluteHeightElement(int magnitude) {
            this.m_magnitude = new Integer(magnitude);
        }

        @Override
        public Object getMagnitude() {
            return this.m_magnitude;
        }

        public void setMagnitude(int magnitude) {
            this.m_magnitude = new Integer(magnitude);
        }

        @Override
        public String getElementName() {
            return "n absolute height";
        }

        public boolean nodificate() {
            assert LegacyDebugger.open("Absolute Height Stylesheet-Element");
            assert LegacyDebugger.addNode("Magnitude: " + this.m_magnitude);
            assert LegacyDebugger.close();
            return true;
        }
    }

    public static class StylesheetPercentageHeightElement extends StylesheetHeightElement implements Nodeable {
        private Double m_magnitude;

        public StylesheetPercentageHeightElement(double magnitude) {
            this.m_magnitude = new Double(magnitude);
        }

        @Override
        public Object getMagnitude() {
            return this.m_magnitude;
        }

        public void setMagnitude(double magnitude) {
            this.m_magnitude = new Double(magnitude);
        }

        @Override
        public String getElementName() {
            return " percentage-height";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Percentage Height Stylesheet-Element", "Percentage: " + this.m_magnitude);
            return true;
        }
    }

    // Width elements
    public static abstract class StylesheetWidthElement extends StylesheetElement {
        public abstract Object getMagnitude();
    }

    public static class StylesheetAbsoluteWidthElement extends StylesheetWidthElement implements Nodeable {
        private Integer m_magnitude;

        public StylesheetAbsoluteWidthElement(int magnitude) {
            this.m_magnitude = new Integer(magnitude);
        }

        @Override
        public Object getMagnitude() {
            return this.m_magnitude;
        }

        public void setMagnitude(int magnitude) {
            this.m_magnitude = new Integer(magnitude);
        }

        @Override
        public String getElementName() {
            return "n absolute width";
        }

        public boolean nodificate() {
            assert LegacyDebugger.open("Absolute Width Stylesheet-Element");
            assert LegacyDebugger.addNode("Width: " + this.m_magnitude);
            assert LegacyDebugger.close();
            return true;
        }
    }

    public static class StylesheetPercentageWidthElement extends StylesheetWidthElement implements Nodeable {
        private Double m_magnitude;

        public StylesheetPercentageWidthElement(double magnitude) {
            this.m_magnitude = new Double(magnitude);
        }

        @Override
        public Object getMagnitude() {
            return this.m_magnitude;
        }

        public void setMagnitude(double magnitude) {
            this.m_magnitude = new Double(magnitude);
        }

        @Override
        public String getElementName() {
            return " percentage-width";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Percentage Width Stylesheet-Element", "Percentage: " + this.m_magnitude);
            return true;
        }
    }

    // Box model elements
    public static class StylesheetMarginElement extends StylesheetElement implements Nodeable {
        private int m_magnitude;

        public StylesheetMarginElement(int magnitude) {
            this.m_magnitude = magnitude;
        }

        public int getMagnitude() {
            return this.m_magnitude;
        }

        @Override
        public String getElementName() {
            return " margin";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Margin Stylesheet-Element", "Magnitude: " + this.m_magnitude);
            return true;
        }
    }

    public static class StylesheetBorderElement extends StylesheetElement implements Nodeable {
        private int m_magnitude;
        private ScriptKeywordType m_style;
        private Color m_color;

        public StylesheetBorderElement(int mag, ScriptKeywordType style, Color color) {
            this.m_magnitude = mag;
            this.m_style = style;
            this.m_color = color;
        }

        public int getMagnitude() {
            return this.m_magnitude;
        }

        public ScriptKeywordType getStyle() {
            return this.m_style;
        }

        public Color getColor() {
            return this.m_color;
        }

        @Override
        public String getElementName() {
            return " border";
        }

        public boolean nodificate() {
            assert LegacyDebugger.open("Border Stylesheet-Element");
            assert LegacyDebugger.addNode("Magnitude: " + this.m_magnitude);
            assert LegacyDebugger.addNode("Style: " + this.m_style);
            assert LegacyDebugger.addNode("Color: " + RiffJavaToolbox.getColorName(this.m_color));
            assert LegacyDebugger.close();
            return true;
        }
    }

    public static class StylesheetPaddingElement extends StylesheetElement implements Nodeable {
        private int m_magnitude;

        public StylesheetPaddingElement(int magnitude) {
            this.m_magnitude = magnitude;
        }

        public int getMagnitude() {
            return this.m_magnitude;
        }

        @Override
        public String getElementName() {
            return " padding";
        }

        public boolean nodificate() {
            assert LegacyDebugger.addSnapNode("Padding Stylesheet-Element", "Magnitude: " + this.m_magnitude);
            return true;
        }
    }

}
