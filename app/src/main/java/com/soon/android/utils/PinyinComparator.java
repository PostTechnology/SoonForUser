package com.soon.android.utils;

import com.soon.android.adapterDataModels.CitySortModel;

import java.util.Comparator;

/**
 * Created by 84975 on 2018/1/30.
 */

public class PinyinComparator implements Comparator<CitySortModel> {
    @Override
    public int compare(CitySortModel o1, CitySortModel o2) {
        if (o1.getLetters().equals("@")
                || o2.getLetters().equals("#")) {
            return 1;
        } else if (o1.getLetters().equals("#")
                || o2.getLetters().equals("@")) {
            return -1;
        } else {
            return o1.getLetters().compareTo(o2.getLetters());
        }
    }
}
