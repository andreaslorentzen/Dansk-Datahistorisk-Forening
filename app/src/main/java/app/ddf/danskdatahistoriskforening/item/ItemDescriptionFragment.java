package app.ddf.danskdatahistoriskforening.item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.R;

public class ItemDescriptionFragment extends Fragment implements ItemUpdater{
    EditText itemDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_description, container, false);

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

    @Override
    public void onResume() {
        super.onResume();

        updateItem(((ItemActivity) getActivity()).getItem());
    }

    @Override
    public void updateItem(Item item) {
        item.setItemDescription(itemDescription.getText().toString());
    }
}
