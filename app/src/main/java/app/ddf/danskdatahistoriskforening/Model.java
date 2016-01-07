package app.ddf.danskdatahistoriskforening;

import java.text.SimpleDateFormat;

/**
 * Created by mathias on 05/01/16.
 */
public class Model {
    private static Model ourInstance = new Model();
    private static IDAO dao = new TempDAO();
    private static boolean itemListUpdated = false;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
    }

    public static boolean isListUpdated(){return itemListUpdated;}

    public static void setListUpdated(boolean listUpdated){itemListUpdated = listUpdated;}

    public static IDAO getDAO(){
        return dao;
    }

    public static SimpleDateFormat getFormatter(){return formatter;}
}
