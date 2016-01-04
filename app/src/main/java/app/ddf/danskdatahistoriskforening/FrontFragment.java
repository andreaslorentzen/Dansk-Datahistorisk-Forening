package app.ddf.danskdatahistoriskforening;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FrontFragment extends Fragment {

    Button registerButton;
    Button listButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_front, container, false);
        registerButton = (Button) layout.findViewById(R.id.registerButton);
        listButton = (Button) layout.findViewById(R.id.listButton);


        return layout;
    }

}
