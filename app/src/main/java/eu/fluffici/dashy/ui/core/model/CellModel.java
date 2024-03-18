package eu.fluffici.dashy.ui.core.model;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.sort.ISortableModel;

public class CellModel implements ISortableModel {
    private final String mId;
    private final Object mData;

    public CellModel(String pId, Object mData) {
        this.mId = pId;
        this.mData = mData;
    }

    public Object getData() {
        return mData;
    }

    @NonNull
    @Override
    public String getId() {
        return mId;
    }

    @Override
    public Object getContent() {
        return mData;
    }
}