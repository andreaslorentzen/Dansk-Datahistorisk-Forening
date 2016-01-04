package app.ddf.danskdatahistoriskforening;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ItemDetails extends Fragment {

    private String detailsURI;

    public ItemDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_details, container, false);

        new tempDAO().getDetailsFromBackEnd(detailsURI);



        return layout;
    }

    public void setDetailsURI(String detailsURI){
        this.detailsURI = detailsURI;
    }

    public String getDetailsURI(){
        return this.detailsURI;
    }
}
