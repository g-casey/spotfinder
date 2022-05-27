package com.gcasey2.spotfinder.models;

import java.util.List;

public class TopArtistsModel {
    private String href;
    private List<ArtistModel> items;
    private int limit;
    private String next;
    private int offset;
    private String previous;
    private int total;

    public TopArtistsModel(){}

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<ArtistModel> getItems() {
        return items;
    }

    public void setItems(List<ArtistModel> items) {
        this.items = items;
    }



    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "TopModel{" +
                "href='" + href + '\'' +
                ", items=" + items +
                ", limit=" + limit +
                ", next='" + next + '\'' +
                ", offset=" + offset +
                ", previous='" + previous + '\'' +
                ", total=" + total +
                '}';
    }
}
