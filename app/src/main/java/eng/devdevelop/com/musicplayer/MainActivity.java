package eng.devdevelop.com.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends AppCompatActivity{
    ListView musiclist;
    Cursor musiccursor;
    int music_column_index;
    int count;
     static final int  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    MediaPlayer mMediaPlayer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_phone_music_list();
    }

    private void init_phone_music_list() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


    }

    private void AccessSongsandInit (){
        String[] proj = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE };

        // permission was granted, yay! Do the
        // contacts-related task you need to do.
        //callMethod();
        musiccursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);


        count = musiccursor.getCount();
        musiclist = (ListView) findViewById(R.id.song_list);
        musiclist.setAdapter(new MusicAdapter(getApplicationContext()));

        musiclist.setOnItemClickListener(musicgridlistener);
        mMediaPlayer = new MediaPlayer();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    AccessSongsandInit();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,
                                long id) {
           // System.gc();
            music_column_index = musiccursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            musiccursor.moveToPosition(position);
            String filename = musiccursor.getString(music_column_index);

            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.reset();
                }
                mMediaPlayer.setDataSource(filename);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Exception e) {

            }
        }
    };

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;

        public MusicAdapter(Context c) {
            mContext = c;
            inflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            MyViewHolder mViewHolder;
            String id = null;String size = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.song, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {

                /* We recycle a View that already exists */
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

                //Get the display name and the size of an image
            music_column_index = musiccursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            musiccursor.moveToPosition(position);
            id = musiccursor.getString(music_column_index);

            music_column_index = musiccursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            musiccursor.moveToPosition(position);
            size =  musiccursor.getString(music_column_index);

            mViewHolder.txtTitle.setText(id);
            mViewHolder.txtSize.setText(size);

            return convertView;
        }

        private class MyViewHolder {
            TextView txtTitle, txtSize;
            ImageView ivIcon;

            public MyViewHolder(View item) {
                txtTitle = (TextView) item.findViewById(R.id.song_title);
                txtSize = (TextView) item.findViewById(R.id.song_size);

            }
        }
    }
}