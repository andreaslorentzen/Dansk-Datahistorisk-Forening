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
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;

public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView itemList;
    JSONArray items;
    List<String> itemTitles;
    Stack<List<String>> stackTitles = new Stack<>();
    String lastSearch = "";

    public ItemListFragment() {
        itemTitles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ItemListFragment", "created");
        View layout = inflater.inflate(R.layout.fragment_item_list, container, false);

        itemTitles = Model.getInstance().getItemTitles();
        stackTitles.push(itemTitles);

        itemList = (ListView) layout.findViewById(R.id.itemList);
        itemList.setOnItemClickListener(this);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, itemTitles);
        itemList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
    }

    ArrayAdapter adapter;

    public void updateItemList(List<String> titles){
        itemTitles = titles;
        if(getActivity() != null){
            adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, itemTitles);
            itemList.setAdapter(adapter);
        }
    }

    /**
    *   [h]         -> search itemTitles        -> titles       -> stack.push(titles)
    *   [h, e]      -> search stack.peek()      -> titles       -> stack.push(titles)
    *   [h, e, j]   -> search stack.peek()      -> titles       -> stack.push(titles)
    *   [h, e]      -> stack.pop()              -> titles       -> stack.push(titles)
    *   @param search
    */
    public void searchItemList(String search) {
        List<String> currentTitles = itemTitles;
        if (search.equals("") && lastSearch.equals("")) {
            return;
        }
        if (search.contains(lastSearch) && search.length() == lastSearch.length() + 1) { // if 1 char is added, get last title list
            currentTitles = stackTitles.peek();
        } else if (lastSearch.length() > 0 && search.equals(lastSearch.substring(0, lastSearch.length() - 1))) { // if 1 char is removed, delete last title list and get the "new last" title list
            stackTitles.pop();
            currentTitles = stackTitles.peek();
            lastSearch = search;
            updateItemList(currentTitles);
            return;
        }
        List<String> searchedTitles = new ArrayList<String>();
        for (String title : currentTitles) {
            if (title.toLowerCase().contains(search.toLowerCase()))
                searchedTitles.add(title);
        }
        stackTitles.push(searchedTitles);
        lastSearch = search;
        updateItemList(searchedTitles);
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
