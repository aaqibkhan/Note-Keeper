package treebo.taskaaqib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import treebo.taskaaqib.util.Constants;
import treebo.taskaaqib.widget.CircleView;

public class BGColorAdapter extends RecyclerView.Adapter<BGColorAdapter.MyViewHolder> {

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
        holder.circleView.setColor(Constants.bgColors[position]);
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleView circleView;

        public MyViewHolder(View view) {
            super(view);
            circleView = (CircleView) view;
        }

    }
}
