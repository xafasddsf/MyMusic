package com.example.mymusic

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener,MusicFragmentDialog.Listener {
    lateinit var btn_form: ImageButton;
    lateinit var btn_back: ImageButton;
    lateinit var btn_play: ImageButton;
    lateinit var btn_forward: ImageButton;
    lateinit var btn_other: ImageButton;
    lateinit var tv_title: TextView;
    lateinit var music_list: ArrayList<String>
    var music_current_index: Int = 0;
    var i: Int = 0;
    var player_form:Int=0;
    lateinit var mediaPlayer: MediaPlayer
    lateinit var musicFragmentDialogDialog:MusicFragmentDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var mPermissionList = ArrayList<String>()
        for (i in PERMISSIONS_STORAGE) {
            if (ActivityCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED) mPermissionList.add(i);//判断权限是否赋予过
        }
        if (mPermissionList.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                mPermissionList.toTypedArray(), 101
            )
        } else {
            //初始化UI控件
            Init();
            //获取本地音乐文件
            GetLocalMusic();
            val sdCard: String = Environment.getExternalStorageDirectory().getPath()+ File.separator
            //第一步：重置
            mediaPlayer.reset();
            //第二步:设置数据源（这里播放的是本地文件，可播放其他文件）
            mediaPlayer.setDataSource(this, Uri.parse(music_list.get(music_current_index)));
            //第三步：准备
            mediaPlayer.prepare();
       tv_title.text=music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/")+1)
           mediaPlayer.setOnCompletionListener {
             when(player_form){
                 0->
                     music_current_index=++music_current_index%music_list.size
                 1->{}
                 2->
                     music_current_index=(0..music_list.size-1).random()
             }
               mediaPlayer.reset()
               mediaPlayer.setDataSource(music_list.get(music_current_index))
               mediaPlayer.prepare()
               mediaPlayer.start()
               i=1;
               btn_play.setImageResource(R.drawable.b4x)
               tv_title.text=music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/")+1)
           }

        }

    }

    private fun GetLocalMusic() {
        var cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        if (cursor?.moveToFirst()!!) {
            for (i in 0..cursor.count - 1) {
                //把本地音乐路径添加到music_list中
                music_list.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                cursor.moveToNext()
            }

        }

    }

    fun Init() {
        mediaPlayer = MediaPlayer()
        music_list = ArrayList<String>()
        btn_form = findViewById(R.id.ibtn_form)
        btn_back = findViewById(R.id.ibtn_back)
        btn_play = findViewById(R.id.ibtn_play)
        btn_forward = findViewById(R.id.ibtn_forward)
        btn_other = findViewById(R.id.ibtn_other)
        tv_title = findViewById(R.id.tv_music_title)
        btn_form.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        btn_other.setOnClickListener(this);
      

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibtn_form -> {
                player_form=++player_form%3;
                when(player_form){
                    0-> (p0 as ImageButton).setImageResource(R.drawable.b3v)
                    1-> (p0 as ImageButton).setImageResource(R.drawable.b3z)
                    2-> (p0 as ImageButton).setImageResource(R.drawable.b54)
                }
            }
            //快退一首歌
            R.id.ibtn_back -> {
               if(music_current_index==0)music_current_index=music_list.size;
                    music_current_index = --music_current_index;
                    if (mediaPlayer.isPlaying) mediaPlayer.stop();
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(music_list.get(music_current_index))
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    i=1;
                    btn_play.setImageResource(R.drawable.b4x)
                    tv_title.text=music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/")+1)
            }
            //播放与暂停
            R.id.ibtn_play -> {
                i = ++i % 2;
                when (i) {
                    1 -> {
                        mediaPlayer.start()
                        (p0 as ImageButton).setImageResource(R.drawable.b4x)
                    }
                    0 -> {
                        mediaPlayer.pause()
                        (p0 as ImageButton).setImageResource(R.drawable.b4z)
                    }
                }
            }
            //快进一首歌
            R.id.ibtn_forward -> {

                    music_current_index = ++music_current_index%music_list.size
                    if (mediaPlayer.isPlaying) mediaPlayer.stop();
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(music_list.get(music_current_index))
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    i = 1;
                    btn_play.setImageResource(R.drawable.b4x)
                    tv_title.text = music_list.get(music_current_index)
                        .substring(music_list.get(music_current_index).lastIndexOf("/") + 1)


            }
            R.id.ibtn_other -> {
 musicFragmentDialogDialog=MusicFragmentDialog(this,music_list)
                musicFragmentDialogDialog.show(supportFragmentManager,"MusicList")
            }
        }
    }

    /***
     * 申请权限后的回调函数
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasPermissionDismiss:Boolean=false
        if (requestCode == 101) {
            for (i in 0..grantResults.size - 1) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "获取读权限", Toast.LENGTH_SHORT).show()
                }
                else{
                    hasPermissionDismiss=true
                    Toast.makeText(this,"获取权限失败",Toast.LENGTH_SHORT).show()
                    break;
                }
            }
        }
        if(!hasPermissionDismiss){
            Init()
            GetLocalMusic();
            Toast.makeText(this, "1223", Toast.LENGTH_SHORT).show()
        }

    }

    override fun OnClick(index: Int) {

        music_current_index=index;
        if (mediaPlayer.isPlaying) mediaPlayer.stop();
        mediaPlayer.reset()
        mediaPlayer.setDataSource(music_list.get(music_current_index))
        mediaPlayer.prepare()
        mediaPlayer.start()
        i = 1;
        btn_play.setImageResource(R.drawable.b4x)
        tv_title.text = music_list.get(music_current_index)
            .substring(music_list.get(music_current_index).lastIndexOf("/") + 1)
        musicFragmentDialogDialog.dismiss()
    }
}