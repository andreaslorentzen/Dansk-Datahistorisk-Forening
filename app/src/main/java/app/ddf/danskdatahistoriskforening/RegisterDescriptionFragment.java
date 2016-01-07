package app.ddf.danskdatahistoriskforening;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RegisterDescriptionFragment extends Fragment {
    EditText itemDescription;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_description, container, false);

        itemDescription = (EditText) layout.findViewById(R.id.itemDescription);

        Item item = ((ItemActivity) getActivity()).getItem();
        setItemDescription(item.getItemDescription());

        return layout;
    }

    public String getItemDescription(){
        if(itemDescription == null){
            return "";
        }
        return itemDescription.getText().toString();
    }

    public void setItemDescription(String description) {
        this.itemDescription.setText(description);
    }
}
