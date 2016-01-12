package app.ddf.danskdatahistoriskforening.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.main.ItemListFragment;

public class SearchManager {

    private static SearchListener usedSearchListener;
    private Stack<List<String>> stackTitles;
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
        if (itemTitles == null)
            itemTitles = new ArrayList<String>();
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
        usedSearchListener.onSearch(searchedTitles);
        return searchedTitles;
    }

    public static List<String>  searchList(List<String> list, String text) {
        List<String> result = new ArrayList<String>();
        for (String title : list) {
            if (title.toLowerCase().contains(text.toLowerCase()))
                result.add(title);
        }
        return result;
    }

    public static void setSearchList(SearchListener searchListener){
        usedSearchListener = searchListener;
    }

    public String getCurrentSearch() {
        return lastSearch;
    }

    public interface SearchListener{
        public void onSearch(List<String> result);
    }
}