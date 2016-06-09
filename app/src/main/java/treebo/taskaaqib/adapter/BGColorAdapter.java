package treebo.taskaaqib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import treebo.taskaaqib.util.Constants;
import treebo.taskaaqib.widget.CircleView;

/**
 * Handles list of background colors for note
 */
public class BGColorAdapter extends RecyclerView.Adapter<BGColorAdapter.MyViewHolder> {

    /**
     * Interface to notify when a color is clicked
     */
    public interface BGColorCallbacks {
        void onColorSelected(String color);
    }


    private BGColorCallbacks mListener;

    public BGColorAdapter(BGColorCallbacks listener) {
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(new CircleView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // Set CircleView's color from list of background colors
        holder.circleView.setColor(Constants.bgColors[position]);

        // Set click listener to notify when a color is clicked
        holder.circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onColorSelected(Constants.bgColors[holder.getAdapterPosition()]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Constants.bgColors.length;
    }

    /**
     * ViewHolder to avoid costly findViewById
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleView circleView;

        public MyViewHolder(View view) {
            super(view);
            circleView = (CircleView) view;
        }

    }
}
