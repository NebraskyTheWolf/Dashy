package eu.fluffici.dashy.ui.core.viewmodel.users;

import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;

import eu.fluffici.dashy.ui.core.model.CellModel;
import eu.fluffici.dashy.ui.core.model.ColumnHeaderModel;
import eu.fluffici.dashy.ui.core.model.RowHeaderModel;
import eu.fluffici.data.database.entity.User;

public class UsersTableViewModel {
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
            // Id
            case 0:
                return Gravity.CENTER;
            // Name
            case 1:
                return Gravity.LEFT;
            // Email
            case 2:
                return Gravity.LEFT;
            // CreatedAt
            case 3:
                return Gravity.CENTER;
            // UpdatedAt
            case 4:
                return Gravity.CENTER;
            default:
                return Gravity.CENTER;
        }

    }

    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        list.add(new ColumnHeaderModel("Id"));
        list.add(new ColumnHeaderModel("Name"));
        list.add(new ColumnHeaderModel("Email"));
        list.add(new ColumnHeaderModel("CreatedAt"));
        list.add(new ColumnHeaderModel("UpdatedAt"));

        return list;
    }

    private List<List<CellModel>> createCellModelList(List<User> userList) {
        List<List<CellModel>> lists = new ArrayList<>();

        // Creating cell model list from User list for Cell Items
        // In this example, User list is populated from web service

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);

            List<CellModel> list = new ArrayList<>();

            // The order should be same with column header list;
            list.add(new CellModel("1-" + i, user.id));          // "Id"
            list.add(new CellModel("2-" + i, user.name));        // "Name"
            list.add(new CellModel("4-" + i, user.email));       // "Email"
            list.add(new CellModel("10-" + i, user.created_at)); // "CreatedAt"
            list.add(new CellModel("11-" + i, user.updated_at)); // "UpdatedAt"

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


    public void generateListForTableView(List<User> users) {
        mColumnHeaderModelList = createColumnHeaderModelList();
        mCellModelList = createCellModelList(users);
        mRowHeaderModelList = createRowHeaderList(users.size());
    }

}