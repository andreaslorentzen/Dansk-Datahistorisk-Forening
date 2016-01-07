package app.ddf.danskdatahistoriskforening;

/**
 * Created by mathias on 05/01/16.
 */
public class Model {
    private static Model ourInstance = new Model();
    private static IDAO dao = new TempDAO();
    private static boolean itemListUpdated = false;
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
}
