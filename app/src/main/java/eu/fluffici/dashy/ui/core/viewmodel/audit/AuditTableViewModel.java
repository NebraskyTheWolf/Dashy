package eu.fluffici.dashy.ui.core.viewmodel.audit;

import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;

import eu.fluffici.dashy.ui.core.model.CellModel;
import eu.fluffici.dashy.ui.core.model.ColumnHeaderModel;
import eu.fluffici.dashy.ui.core.model.RowHeaderModel;
import eu.fluffici.data.database.entity.Audit;
import eu.fluffici.data.database.entity.User;

public class AuditTableViewModel {
    // View Types
    public static final int GENDER_TYPE = 1;
    public static final int MONEY_TYPE = 2;

    private List<ColumnHeaderModel> mColumnHeaderModelList;
    private List<RowHeaderModel> mRowHeaderModelList;
    private List<List<CellModel>> mCellModelList;

    public int getCellItemViewType(int column) {

        switch (column) {
            case 5:
                // 5. column header is gender.
                return GENDER_TYPE;
            case 8:
                // 8. column header is Salary.
                return MONEY_TYPE;
            default:
                return 0;
        }
    }

    public int getColumnTextAlign(int column) {
        switch (column) {
            case 1:
            case 2:
                return Gravity.LEFT;
            case 0:
            case 4:
            default:
                return Gravity.CENTER;
        }

    }

    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        list.add(new ColumnHeaderModel("Id"));
        list.add(new ColumnHeaderModel("Name"));
        list.add(new ColumnHeaderModel("Type"));
        list.add(new ColumnHeaderModel("Slug"));

        return list;
    }

    private List<List<CellModel>> createCellModelList(List<Audit> auditList) {
        List<List<CellModel>> lists = new ArrayList<>();

        // Creating cell model list from User list for Cell Items
        // In this example, User list is populated from web service

        for (int i = 0; i < auditList.size(); i++) {
            Audit user = auditList.get(i);

            List<CellModel> list = new ArrayList<>();

            // The order should be same with column header list;
            list.add(new CellModel("1-" + i, user.id));          // "Id"
            list.add(new CellModel("2-" + i, user.name));        // "Name"
            list.add(new CellModel("3-" + i, user.type));       // "Email"
            list.add(new CellModel("4-" + i, user.slug)); // "CreatedAt"

            // Add
            lists.add(list);
        }

        return lists;
    }

    private List<RowHeaderModel> createRowHeaderList(int size) {
        List<RowHeaderModel> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new RowHeaderModel(String.valueOf(i + 1)));
        }
        return list;
    }


    public List<ColumnHeaderModel> getColumHeaderModeList() {
        return mColumnHeaderModelList;
    }

    public List<RowHeaderModel> getRowHeaderModelList() {
        return mRowHeaderModelList;
    }

    public List<List<CellModel>> getCellModelList() {
        return mCellModelList;
    }


    public void generateListForTableView(List<Audit> audits) {
        mColumnHeaderModelList = createColumnHeaderModelList();
        mCellModelList = createCellModelList(audits);
        mRowHeaderModelList = createRowHeaderList(audits.size());
    }

}