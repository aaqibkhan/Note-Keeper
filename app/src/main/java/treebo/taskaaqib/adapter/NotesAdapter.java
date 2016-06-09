package treebo.taskaaqib.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import treebo.taskaaqib.R;
import treebo.taskaaqib.model.Note;

public class NotesAdapter extends RealmRecyclerViewAdapter<Note, NotesAdapter.MyViewHolder> {

    public interface NotesListener {
        void onNoteClicked(Note note);
    }


    private NotesListener mListener;

    public NotesAdapter(Context context, RealmResults<Note> data, NotesListener listener) {
        super(context, data, true);
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Note note = getData().get(position);
        if (TextUtils.isEmpty(note.getHeading())) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(note.getHeading());
        }
        if (TextUtils.isEmpty(note.getBody())) {
            holder.body.setVisibility(View.GONE);
        } else {
            holder.body.setVisibility(View.VISIBLE);
            holder.body.setText(note.getBody());
        }
        if (TextUtils.isEmpty(note.getBgColorHex())) {
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.rootView.getContext(), R.color.default_bg));
        } else {
            try {
                holder.rootView.setCardBackgroundColor(Color.parseColor(note.getBgColorHex()));
            } catch (IllegalArgumentException e) {
                holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.rootView.getContext(), R.color.default_bg));
            }
        }
        if (TextUtils.isEmpty(note.getTextColorHex())) {
            holder.title.setTextColor(ContextCompat.getColor(holder.rootView.getContext(), R.color.default_text));
            holder.body.setTextColor(ContextCompat.getColor(holder.rootView.getContext(), R.color.default_text));
        } else {
            try {
                holder.title.setTextColor(Color.parseColor(note.getTextColorHex()));
                holder.body.setTextColor(Color.parseColor(note.getTextColorHex()));
            } catch (IllegalArgumentException e) {
                holder.title.setTextColor(ContextCompat.getColor(holder.rootView.getContext(), R.color.default_text));
                holder.body.setTextColor(ContextCompat.getColor(holder.rootView.getContext(), R.color.default_text));
            }
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNoteClicked(getData().get(holder.getAdapterPosition()));
                }
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body;
        public CardView rootView;

        public MyViewHolder(View view) {
            super(view);
            rootView = (CardView) view;
            title = (TextView) view.findViewById(R.id.tv_title);
            body = (TextView) view.findViewById(R.id.tv_body);
        }

    }
}