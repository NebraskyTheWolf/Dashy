package eu.fluffici.dashy.ui.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.List;

import eu.fluffici.dashy.R;
import eu.fluffici.dashy.ui.core.holder.CellViewHolder;
import eu.fluffici.dashy.ui.core.holder.ColumnHeaderViewHolder;
import eu.fluffici.dashy.ui.core.holder.GenderCellViewHolder;
import eu.fluffici.dashy.ui.core.holder.RowHeaderViewHolder;
import eu.fluffici.dashy.ui.core.model.CellModel;
import eu.fluffici.dashy.ui.core.model.ColumnHeaderModel;
import eu.fluffici.dashy.ui.core.model.RowHeaderModel;
import eu.fluffici.dashy.ui.core.viewmodel.users.UsersTableViewModel;
import eu.fluffici.data.database.entity.User;

public class UsersTableAdapter extends AbstractTableAdapter<ColumnHeaderModel, RowHeaderModel, CellModel> {

    private final UsersTableViewModel myTableViewModel;

    public UsersTableAdapter(Context p_jContext) {
        super(p_jContext);

        this.myTableViewModel = new UsersTableViewModel();
    }


    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View layout;

        switch (viewType) {
            case UsersTableViewModel.GENDER_TYPE:
                // Get gender cell xml Layout
                layout = LayoutInflater.from(mContext).inflate(R.layout
                        .tableview_gender_cell_layout, parent, false);

                return new GenderCellViewHolder(layout);

            default:
                // Get default Cell xml Layout
                layout = LayoutInflater.from(mContext).inflate(R.layout.tableview_cell_layout,
                        parent, false);

                // Create a Cell ViewHolder
                return new CellViewHolder(layout);
        }
    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object p_jValue, int
            p_nXPosition, int p_nYPosition) {
        CellModel cell = (CellModel) p_jValue;

        if (holder instanceof CellViewHolder) {
            // Get the holder to update cell item text
            ((CellViewHolder) holder).setCellModel(cell, p_nXPosition);

        } else if (holder instanceof GenderCellViewHolder) {
            ((GenderCellViewHolder) holder).setCellModel(cell);
        }

    }

    @Override
    public AbstractSorterViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout
                .tableview_column_header_layout, parent, false);

        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object p_jValue, int
            p_nXPosition) {
        ColumnHeaderModel columnHeader = (ColumnHeaderModel) p_jValue;

        // Get the holder to update cell item text
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;

        columnHeaderViewHolder.setColumnHeaderModel(columnHeader, p_nXPosition);
    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {

        // Get Row Header xml Layout
        View layout = LayoutInflater.from(mContext).inflate(R.layout.tableview_row_header_layout,
                parent, false);

        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object p_jValue, int p_nYPosition) {

        RowHeaderModel rowHeaderModel = (RowHeaderModel) p_jValue;

        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(rowHeaderModel.getData());

    }

    @Override
    public View onCreateCornerView() {
        return LayoutInflater.from(mContext).inflate(R.layout.tableview_corner_layout, null, false);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        return myTableViewModel.getCellItemViewType(position);
    }


    /**
     * This method is not a generic Adapter method. It helps to generate lists from single user
     * list for this adapter.
     */
    public void setUserList(List<User> userList) {
        // Generate the lists that are used to TableViewAdapter
        myTableViewModel.generateListForTableView(userList);

        // Now we got what we need to show on TableView.
        setAllItems(myTableViewModel.getColumHeaderModeList(), (List<RowHeaderModel>) myTableViewModel.getRowHeaderModelList(), myTableViewModel.getCellModelList());
    }

}