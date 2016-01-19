package app.ddf.danskdatahistoriskforening.main;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.domain.ListItem;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.domain.UserSelection;

public class ListItemFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, UserSelection.SearchObservator {

    ListView itemList;
    TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ListItemFragment", "created");
        View layout = inflater.inflate(R.layout.fragment_list_item, container, false);

        emptyText = (TextView) layout.findViewById(R.id.emptyText);

        itemList = (ListView) layout.findViewById(R.id.itemList);
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1,  new ArrayList<ListItem>());
        itemList.setAdapter(adapter);
        itemList.setOnItemClickListener(this);

        Logic.instance.userSelection.searchObservators.add(this);
        onSearchChange();

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logic.instance.userSelection.searchObservators.remove(this);
    }

    @Override
    public void onSearchChange() {
        ArrayAdapter adapter = (ArrayAdapter) itemList.getAdapter();
        adapter.clear();

        List<ListItem> items =  Logic.instance.searchedItems;
        if(items == null || items.isEmpty())
            emptyText.setVisibility(View.VISIBLE);
        else {
            emptyText.setVisibility(View.GONE);
            adapter.addAll(items);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> aV, View v, int position, long l){
        ListItem item = Logic.instance.searchedItems.get(position);
        Logic.instance.userSelection.selectedListItem = item;
        Log.d("ListItemFragment", "OnItemClick");
        ((MainActivity)getActivity()).setFragmentDetails(position);
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).startRegister();
    }

}
