package com.selvia.quiz20.service;

/**
 * Created by ds on 2016-07-24.
 */
public class MyTokenizer {
    String ori, dil, tmp;
    boolean islast = false;
    boolean from_first = true;

    public MyTokenizer(String ori, String dil) {
        this.ori = ori;
        this.dil = dil;
        tmp = ori;
    }

    public MyTokenizer(String ori, String dil, boolean from_first) {
        this.ori = ori;
        this.dil = dil;
        tmp = ori;
        this.from_first = from_first;
    }

    public boolean hasMoreTokens() {
        if(tmp.contains(dil)) return true;
        if(!islast && tmp.length() > 0) {
            return true;
        }
        return false;
    }

    public String nextToken() {
        if(from_first) return nextToken_front();
        else return nextToken_end();
    }

    private String nextToken_front() {
        int end = tmp.indexOf(dil);
        if(end == -1) {
            if(islast) return null;
            islast = true;
            return tmp;
        }

        String next = tmp.substring(0, end);
        tmp = tmp.substring(end+1);
        return next;
    }

    private String nextToken_end() {
        int start = tmp.lastIndexOf(dil);
        if(start == -1) {
            if(islast) return null;
            islast = true;
            return tmp;
        }

        String next = tmp.substring(start+1, tmp.length());
        tmp = tmp.substring(0, start);
        return next;
    }
}
