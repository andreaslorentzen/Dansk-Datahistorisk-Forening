package app.ddf.danskdatahistoriskforening.main;


import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.helper.SearchManager;

public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, SearchManager.SearchListener {

    ListView itemList;
    JSONArray items;

    TextView emptyText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ItemListFragment", "created");
        View layout = inflater.inflate(R.layout.fragment_item_list, container, false);
        emptyText = (TextView) layout.findViewById(R.id.emptyText);

        SearchManager.setSearchList(this);
        itemList = (ListView) layout.findViewById(R.id.itemList);
        itemList.setOnItemClickListener(this);
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1,  new ArrayList<String>());
        itemList.setAdapter(adapter);
        updateItemList(null);


        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        return layout;
    }


    private void updateItemList(List<String> titles){
        ArrayAdapter adapter = (ArrayAdapter) itemList.getAdapter();
        adapter.clear();
        List<String>  itemTitles = Model.getInstance().getItemTitles();
        if (itemTitles != null && titles == null)
            titles = itemTitles;
        if (titles == null || titles.isEmpty())
            emptyText.setVisibility(View.VISIBLE);
        else {
            emptyText.setVisibility(View.GONE);
            adapter.addAll(titles);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        SearchManager.setSearchList(null);
    }

    @Override
    public void onSearch(List<String> result) {
        updateItemList(result);
    }

    @Override
    public void onItemClick(AdapterView<?> aV, View v, int position, long l){
        Log.d("ItemListFragment", "OnItemClick");
        ((MainActivity)getActivity()).setFragmentDetails(position);
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).startRegister();
    }


}
