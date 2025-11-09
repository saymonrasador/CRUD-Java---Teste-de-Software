package util;

import javax.swing.ImageIcon;
import java.net.URL;

public class Util {
	private static final String ICONS_PATH = "/view/icones/";

	public static ImageIcon getIcon(Class<?> kclass, String icone) {
		URL location = kclass.getResource(ICONS_PATH + icone + ".gif");
		if (location == null) {
			System.err.println("Ícone não encontrado: " + ICONS_PATH + icone + ".gif");
			return null; // Ou retorne um ícone padrão/fallback
		}
		return new ImageIcon(location);
	}
}