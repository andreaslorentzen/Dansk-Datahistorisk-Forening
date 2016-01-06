package app.ddf.danskdatahistoriskforening;


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

public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener{

    ListView itemList;
    JSONArray items;
    List<String> itemTitles;
    Stack<List<String>> stackTitles = new Stack<List<String>>();
    String lastSearch = "";

    public ItemListFragment() {
        itemTitles = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ItemListFragment", "created");

        View layout = inflater.inflate(R.layout.content_main2, container, false);
        itemList = (ListView) layout.findViewById(R.id.itemList);
        itemList.setOnItemClickListener(this);

        ((MainActivity)getActivity()).changeToOnlySearch();

        updateItemList(null);
        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
    }

    ArrayAdapter adapter;

    public void updateItemList(List<String> titles){
        if(titles != null)
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
        if (!lastSearch.equals("")){
            if (search.contains(lastSearch) && search.length() == lastSearch.length()+1) // if 1 char is added, get last title list
                currentTitles = stackTitles.peek();
            else if (search.length() == lastSearch.length()-1) { // if 1 char is removed, delete last title list and get the "new last" title list
                stackTitles.pop();
                currentTitles = stackTitles.peek();
            }
        }

        List<String> searchedTitles = new ArrayList<String>();
        for (String title : currentTitles) {
            if (title.contains(search))
                searchedTitles.add(title);
        }
        searchedTitles = currentTitles;
        stackTitles.push(searchedTitles);
        updateItemList(searchedTitles);
    }

    @Override
    public void onItemClick(AdapterView<?> aV, View v, int position, long l){
        Log.d("ItemListFragment", "OnItemClick");
        ((MainActivity)getActivity()).setFragmentDetails(position);
    }
}
