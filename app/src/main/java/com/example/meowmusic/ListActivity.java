package com.example.meowmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mrv;
    private int[] songs = {R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5};
    private int[] images = {R.drawable.cover1, R.drawable.cover2, R.drawable.cover3, R.drawable.cover4, R.drawable.cover5};
    private String[] names = {"快乐儿歌", "世界末日", "你比从前快乐", "以父之名", "搁浅"};
    private String[] singers = {"佚名", "周杰伦", "周杰伦", "周杰伦", "周杰伦"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mrv = findViewById(R.id.rv);
        mrv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mrv.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public int getItemCount() {
            return songs.length;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mView = View.inflate(getApplicationContext(), R.layout.music_item, null);
            MyHolder myHolder = new MyHolder(mView);

            return myHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int p) {

            holder.imageView.setBackgroundResource(images[p]);
            holder.textView.setText(names[p]);
            holder.singerText.setText(singers[p]);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = names[p];
                    Integer song = songs[p];
                    Integer image = images[p];
                    System.out.println(name);
                    System.out.println(image);

                    Message msg = MusicService.handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("song", song);
                    bundle.putString("name", name);
                    bundle.putInt("image", image);
                    msg.setData(bundle);
                    MusicService.handler.sendMessage(msg);

//                    Message msg2 = PlayActivity.handler2.obtainMessage();
//                    Bundle bundle2 = new Bundle();
//                    bundle2.putString("name", name);
//                    bundle2.putInt("image", image);
//                    msg2.setData(bundle2);
//                    PlayActivity.handler2.sendMessage(msg2);

                    Intent intent = new Intent(ListActivity.this, PlayActivity.class);
                    startActivity(intent);
                }
            });

        }

    }

    private class MyHolder extends RecyclerView.ViewHolder {

        public ImageView imageView = null;
        public TextView textView = null, singerText = null;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.music_image);
            textView = itemView.findViewById(R.id.music_text);
            singerText = itemView.findViewById(R.id.music_singer);
        }
    }

}