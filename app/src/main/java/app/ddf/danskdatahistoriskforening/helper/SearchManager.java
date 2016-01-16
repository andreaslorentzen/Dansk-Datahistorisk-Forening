package app.ddf.danskdatahistoriskforening.helper;

import android.util.Pair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import app.ddf.danskdatahistoriskforening.Model;

public class SearchManager {

    private static OnSearchListener usedOnSearchListener;
    private Stack<Pair<List<String>, List<JSONObject>>> stackTitleAndObjects;
    private String lastSearch = "";
    private List<String> itemTitles;

    /**
     *   [h]         -> search itemTitles        -> titles       -> stack.push(titles)
     *   [h, e]      -> search stack.peek()      -> titles       -> stack.push(titles)
     *   [h, e, j]   -> search stack.peek()      -> titles       -> stack.push(titles)
     *   [h, e]      -> stack.pop()              -> titles       -> stack.push(titles)
     * @param search
     */
    public List<String> searchItemList(String search) {
        List<String> itemTitles = Model.getInstance().getItemTitles();
        List<JSONObject> items = Model.getInstance().getItems();
        if (itemTitles == null)
            itemTitles = new ArrayList<String>();
        if (items == null)
            items = new ArrayList<>();
        List<String> searchedTitles;
        if (search.equals("")) {
            // clear on empty search word
            stackTitleAndObjects = new Stack<>();
            stackTitleAndObjects.push(new Pair<List<String>, List<JSONObject>>(itemTitles, items));
            searchedTitles = itemTitles;
        } else if (lastSearch.length() > 0 && search.contains(lastSearch)  && search.length() == lastSearch.length() + 1) { // if 1 char is added, get last title list
            // incremented search word
            stackTitleAndObjects.push(searchList(stackTitleAndObjects.peek(), search));
            searchedTitles = stackTitleAndObjects.peek().first;
        } else if (lastSearch.length() > 0 && search.equals(lastSearch.substring(0, lastSearch.length() - 1))) { // if 1 char is removed, delete last title list and get the "new last" title list
            // decremented search word
            stackTitleAndObjects.pop();
            searchedTitles = stackTitleAndObjects.peek().first;
        } else  {
            // new serach word - also handles a pasted search word (for loop)
            stackTitleAndObjects = new Stack<>();
            stackTitleAndObjects.push(new Pair<List<String>, List<JSONObject>>(itemTitles, items));
            lastSearch = search;
            for (int i = 0; i < search.length(); i++)
                stackTitleAndObjects.push(searchList(stackTitleAndObjects.peek(),search.substring(0,i+1)));
            searchedTitles = stackTitleAndObjects.peek().first;
        }
        lastSearch = search;
        Model.setCurrentJSONObjects(stackTitleAndObjects.peek().second);
        usedOnSearchListener.onSearch(searchedTitles);
        return searchedTitles;
    }

    public static Pair<List<String>, List<JSONObject>>  searchList(Pair<List<String>, List<JSONObject>> pair, String text) {
        List<String> result = new ArrayList<String>();
        List<JSONObject> resultObjects = new ArrayList<>();
        List<String> list = pair.first;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).toLowerCase().contains(text.toLowerCase())) {
                result.add(list.get(i));
                resultObjects.add(pair.second.get(i));
            }
        }
        return new Pair<List<String>, List<JSONObject>>(result, resultObjects);
    }

    public static void setOnSearchListener(OnSearchListener onSearchListener){
        usedOnSearchListener = onSearchListener;
    }

    public static OnSearchListener getSearchList(){
        return usedOnSearchListener;
    }

    public String getCurrentSearch() {
        return lastSearch;
    }

    public interface OnSearchListener {
        public void onSearch(List<String> result);
    }
}