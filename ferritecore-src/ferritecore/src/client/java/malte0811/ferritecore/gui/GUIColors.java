package malte0811.ferritecore.gui;

/**
 * Centralized color configuration for the ClickGUI.
 * All colors are stored as ARGB hex values.
 */
public class GUIColors {
    // Header and background colors
    public static int headerBackground = 0xdd303456;
    public static int headerBackgroundUnfocused = 0x99303456;
    public static int moduleBackground = 0xaa303446;

    // Accent color (used for tickbox fill, slider handle, focused borders)
    public static int accent = 0xffea76cb;
    public static int accentTransparent = 0xeeea76cb;

    // Module state colors
    public static int moduleEnabled = 0xffa6ff89;
    public static int moduleDisabled = 0xffff6264;

    // Border and outline colors
    public static int border = 0x99ffffff;
    public static int separator = 0x99dddddd;

    // Text color
    public static int text = 0xffffffff;

    // Button colors
    public static int buttonActive = 0xdd4CAF50;
    public static int buttonInactive = 0xdd666666;
    public static int buttonProgress = 0xddFFA500;

    // Status message colors
    public static int statusSuccess = 0xffa6ff89;
    public static int statusError = 0xffff6264;

    // Category accent for background tint
    public static int categoryAccent = 0x017287fd;

    /**
     * Creates a color from HSVA values.
     * @param h Hue (0-360)
     * @param s Saturation (0-1)
     * @param v Value/Brightness (0-1)
     * @param a Alpha (0-1)
     * @return ARGB color as int
     */
    public static int fromHSVA(float h, float s, float v, float a) {
        int rgb = java.awt.Color.HSBtoRGB(h / 360f, s, v);
        int alpha = (int) (a * 255) & 0xFF;
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    /**
     * Extracts HSVA values from an ARGB color.
     * @param argb The ARGB color
     * @return float array [h, s, v, a] where h is 0-360, others are 0-1
     */
    public static float[] toHSVA(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
        return new float[] { hsb[0] * 360f, hsb[1], hsb[2], a / 255f };
    }

    /**
     * Gets the alpha component from an ARGB color.
     */
    public static int getAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    /**
     * Gets the red component from an ARGB color.
     */
    public static int getRed(int argb) {
        return (argb >> 16) & 0xFF;
    }

    /**
     * Gets the green component from an ARGB color.
     */
    public static int getGreen(int argb) {
        return (argb >> 8) & 0xFF;
    }

    /**
     * Gets the blue component from an ARGB color.
     */
    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    /**
     * Creates an ARGB color from components.
     */
    public static int fromARGB(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Applies alpha to an existing color.
     */
    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    /**
     * Corner rounding factor (0.0 = sharp corners, 1.0 = maximum rounding).
     * Used by UI components for consistent corner styling.
     */
    public static float cornerRounding = 0.5f;
}

