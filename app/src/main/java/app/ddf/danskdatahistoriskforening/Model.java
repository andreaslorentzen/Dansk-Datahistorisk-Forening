package app.ddf.danskdatahistoriskforening;

/**
 * Created by mathias on 05/01/16.
 */
public class Model {
    private static Model ourInstance = new Model();
    private static IDAO dao = new TempDAO();

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
    }

    public static IDAO getDAO(){
        return dao;
    }
}
