package app.ddf.danskdatahistoriskforening.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;

public class FrontFragment extends Fragment implements View.OnClickListener{

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
        registerButton.setOnClickListener(this);
        listButton = (Button) layout.findViewById(R.id.listButton);
        listButton.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        if(v == registerButton){
            ((MainActivity) getActivity()).startRegister();
        }
        else if(v == listButton){
            ((MainActivity) getActivity()).setFragmentList();
        }
    }
}
