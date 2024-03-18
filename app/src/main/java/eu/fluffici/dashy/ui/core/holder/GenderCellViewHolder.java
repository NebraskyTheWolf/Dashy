package eu.fluffici.dashy.ui.core.holder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import eu.fluffici.dashy.R;
import eu.fluffici.dashy.ui.core.model.CellModel;

public class GenderCellViewHolder extends AbstractViewHolder {

    private final ImageButton cell_image_button;
    private final Drawable cell_male_drawable;
    private final Drawable cell_female_drawable;

    public GenderCellViewHolder(View itemView) {
        super(itemView);
        cell_image_button =  itemView.findViewById(R.id.cell_image_button);

        // Get vector drawables
        cell_male_drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.gender_male_svg);
        cell_female_drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.gender_female_svg);
    }

    public void setCellModel(CellModel p_jModel) {
        char c = String.valueOf(p_jModel.getData()).trim().charAt(0);

        if (c == 'F') {
            cell_image_button.setImageDrawable(cell_female_drawable);
        } else if (c == 'M') {
            cell_image_button.setImageDrawable(cell_male_drawable);
        }
    }
}