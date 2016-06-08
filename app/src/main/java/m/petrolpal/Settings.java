package m.petrolpal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by m on 6/7/2016.
 */
public class Settings {

    private static DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
    private static Boolean celcius = true;


    public static Boolean getCelcius() {
        return celcius;
    }

    public static void toggleCelcius() {
        celcius = !celcius;
    }

    public static DateFormat getFormat() {
        return format;
    }


}
