package krishnaapps.com.pushdatastockbreakout.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import krishnaapps.com.pushdatastockbreakout.R;
import krishnaapps.com.pushdatastockbreakout.modules.Swing;


public class SwingAdapter extends RecyclerView.Adapter<SwingAdapter.SwingViewHolder> {
private Context mContext;
private List<Swing> mSwings;
private OnSwingItemClickListener mListener;

public SwingAdapter(Context context, List<Swing> uploads) {
        mContext = context;
        mSwings = uploads;
        }

@Override
public SwingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.swing_items, parent, false);
        return new SwingViewHolder(v);
        }

@Override
public void onBindViewHolder(SwingViewHolder holder, int position) {
        Swing uploadCurrent = mSwings.get(position);

        holder.textViewName.setText(uploadCurrent.getSwingName());
        holder.textViewDesc.setText(uploadCurrent.getSwingDesc());
        holder.textViewDate.setText(uploadCurrent.getSwingDate());

        Glide.with(mContext)
        .load(uploadCurrent.getSwingImageUrl())
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.ic_launcher_background)
        .into(holder.imageView);

        }

@Override
public int getItemCount() {
        return mSwings.size();
        }

public class SwingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    public TextView textViewName, textViewDesc, textViewDate;
    public ImageView imageView;

    public SwingViewHolder(View itemView) {
        super(itemView);

        textViewName = itemView.findViewById(R.id.swing_text_view_name);
        textViewDesc = itemView.findViewById(R.id.swing_text_view_desc);
        textViewDate = itemView.findViewById(R.id.swing_text_view_date);
        imageView = itemView.findViewById(R.id.swing_image_view_upload);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(position);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
        MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

        doWhatever.setOnMenuItemClickListener(this);
        delete.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (mListener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {

                switch (item.getItemId()) {
                    case 1:
                        mListener.onWhatEverClick(position);
                        return true;
                    case 2:
                        mListener.onDeleteClick(position);
                        return true;
                }
            }
        }
        return false;
    }
}

public interface OnSwingItemClickListener {
    void onItemClick(int position);

    void onWhatEverClick(int position);

    void onDeleteClick(int position);
}

    public void setOnSwingItemClickListener(OnSwingItemClickListener listener) {
        mListener = listener;
    }
}