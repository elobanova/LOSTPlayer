package lab.android.evgalexandrakaterwth.lostplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import lab.android.evgalexandrakaterwth.lostplayer.R;
import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;

/**
 * Created by ekaterina on 26.07.2015.
 */
public class SongListAdapter extends BaseAdapter {
    private List<SongItem> listOfSongs = new ArrayList<>();
    private final Context context;

    public SongListAdapter(Context context, List<SongItem> listOfSongs) {
        this.context = context;
        this.listOfSongs = listOfSongs;
    }

    @Override
    public int getCount() {
        if (this.listOfSongs != null)
            return this.listOfSongs.size();

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return this.listOfSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.listOfSongs.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout songItemLayout = (LinearLayout) inflater.inflate
                (R.layout.song_item, parent, false);
        TextView songView = (TextView) songItemLayout.findViewById(R.id.song_title);
        SongItem songItem = this.listOfSongs.get(position);
        songView.setText(songItem.getTitle());
        songItemLayout.setTag(position);
        return songItemLayout;
    }
}
