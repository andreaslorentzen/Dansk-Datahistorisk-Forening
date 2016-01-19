package app.ddf.danskdatahistoriskforening.helper;

import java.net.URI;
import java.util.List;

public class MADATA {
    URI uri;
    public List<Integer> durationList;
    public MADATA(URI uri, List<Integer> durationList) {
    this.uri = uri;
        this.durationList = durationList;
    }
}
