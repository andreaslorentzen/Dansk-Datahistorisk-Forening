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

public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView itemList;
    JSONArray items;
    List<String> itemTitles;
    Stack<List<String>> stackTitles;
    String lastSearch = "";
    TextView emptyText;
    public ItemListFragment() {
        itemTitles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ItemListFragment", "created");
        View layout = inflater.inflate(R.layout.fragment_item_list, container, false);
        emptyText = (TextView) layout.findViewById(R.id.emptyText);
        itemTitles = Model.getInstance().getItemTitles();


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
        if (itemTitles != null && titles == null)
            titles = itemTitles;
        if (titles.isEmpty())
            emptyText.setVisibility(View.VISIBLE);
        else {
            emptyText.setVisibility(View.GONE);
            adapter.addAll(titles);
        }
        adapter.notifyDataSetChanged();
    }

    /**
    *   [h]         -> search itemTitles        -> titles       -> stack.push(titles)
    *   [h, e]      -> search stack.peek()      -> titles       -> stack.push(titles)
    *   [h, e, j]   -> search stack.peek()      -> titles       -> stack.push(titles)
    *   [h, e]      -> stack.pop()              -> titles       -> stack.push(titles)
    */
    public void searchItemList() {
        String search = Model.getInstance().getCurrentSearch();
        List<String> searchedTitles;
        if (search.equals("")) {
            // clear on empty search word
            stackTitles = new Stack<>();
            stackTitles.push(itemTitles);
            searchedTitles = itemTitles;
        } else if (lastSearch.length() > 0 && search.contains(lastSearch)  && search.length() == lastSearch.length() + 1) { // if 1 char is added, get last title list
            // incremented search word
            searchedTitles = searchList(stackTitles.peek(), search);
            stackTitles.push(searchedTitles);
        } else if (lastSearch.length() > 0 && search.equals(lastSearch.substring(0, lastSearch.length() - 1))) { // if 1 char is removed, delete last title list and get the "new last" title list
            // decremented search word
            stackTitles.pop();
            searchedTitles = stackTitles.peek();
        } else  {
            // new serach word - also handles a pasted search word (for loop)
            stackTitles = new Stack<>();
            stackTitles.push(itemTitles);
            lastSearch = search;
            for (int i = 0; i < search.length(); i++)
                stackTitles.push( searchList(stackTitles.peek(), search.substring(0,i+1)));
            searchedTitles = stackTitles.peek();
        }
        lastSearch = search;
        updateItemList(searchedTitles);
    }

    public List<String>  searchList(List<String> list, String text) {
        List<String> result = new ArrayList<String>();
        for (String title : list) {
            if (title.toLowerCase().contains(text.toLowerCase()))
                result.add(title);
        }
        return result;
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
