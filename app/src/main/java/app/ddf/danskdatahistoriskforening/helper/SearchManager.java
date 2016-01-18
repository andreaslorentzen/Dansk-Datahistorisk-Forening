package app.ddf.danskdatahistoriskforening.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import app.ddf.danskdatahistoriskforening.domain.ListItem;
import app.ddf.danskdatahistoriskforening.domain.Logic;

public class SearchManager {

    private Stack<List<ListItem>> stackListItems = new Stack<>();
    private String lastSearch = "";

    /**
     *   [h]         -> search itemTitles        -> titles       -> stack.push(titles)
     *   [h, e]      -> search stack.peek()      -> titles       -> stack.push(titles)
     *   [h, e, j]   -> search stack.peek()      -> titles       -> stack.push(titles)
     *   [h, e]      -> stack.pop()              -> titles       -> stack.push(titles)
     * @param searchQuery
     */
    public List<ListItem> search(String searchQuery) {
        List<ListItem> listItems = Logic.instance.items;

        if (listItems == null)
            listItems = new ArrayList<>();

        List<ListItem> searchedListItems;
        if (searchQuery == null || searchQuery.equals("")) {
            // clear on empty search word
            stackListItems = new Stack<>();
            stackListItems.push(listItems);
            searchedListItems = listItems;
        } else if (lastSearch.length() > 0 && searchQuery.contains(lastSearch)  && searchQuery.length() == lastSearch.length() + 1) { // if 1 char is added, get last title list
            // incremented search word
            stackListItems.push(search(stackListItems.peek(), searchQuery));
            searchedListItems = stackListItems.peek();
        } else if (lastSearch.length() > 0 && searchQuery.equals(lastSearch.substring(0, lastSearch.length() - 1))) { // if 1 char is removed, delete last title list and get the "new last" title list
            // decremented search word
            stackListItems.pop();
            searchedListItems = stackListItems.peek();
        } else  {
            // new serach word - also handles a pasted search word (for loop)
            stackListItems = new Stack<>();
            stackListItems.push(listItems);
            lastSearch = searchQuery;
            for (int i = 0; i < searchQuery.length(); i++)
                stackListItems.push(search(stackListItems.peek(), searchQuery.substring(0, i + 1)));
            searchedListItems = stackListItems.peek();
        }
        lastSearch = searchQuery == null ? "" : searchQuery;
//        Model.setCurrentJSONObjects(stackTitleAndObjects.peek().second);
        //    usedOnSearchListener.onSearch(searchedListItems);
        return searchedListItems;
    }

    private List<ListItem> search(List<ListItem> items, String searchQuery) {
        List<ListItem> result = new ArrayList<>();
        List<ListItem> list = items;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).title.toLowerCase().contains(searchQuery.toLowerCase())) {
                result.add(list.get(i));
            }
        }
        return result;
    }

}