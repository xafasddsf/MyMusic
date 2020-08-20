package com.example.mymusic

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity(), View.OnClickListener,MusicFragmentDialog.Listener {
    lateinit var btn_form: ImageButton;
    lateinit var btn_back: ImageButton;
    lateinit var btn_play: ImageButton;
    lateinit var btn_forward: ImageButton;
    lateinit var btn_other: ImageButton;
    lateinit var tv_title: TextView;
    lateinit var iv_bg:ImageView
    lateinit var music_list: ArrayList<String>
    lateinit var temp_list:ArrayList<String>;
    var music_current_index: Int = 0;
    var i: Int = 0;
    var player_form:Int=0;
    lateinit var animator: ObjectAnimator
    var currentPlayTime:Long = 0
    lateinit var mediaPlayer: MediaPlayer
    lateinit var musicFragmentDialogDialog:MusicFragmentDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
            Init()
            val handler: Handler = object : Handler() {
                override  fun handleMessage(msg: Message) {
                    temp_list = ArrayList<String>()
                    for (i in music_list) temp_list.add(i)
                    //第一步：重置
                    mediaPlayer.reset();
                    //第二步:设置数据源（这里播放的是本地文件，可播放其他文件）
                    mediaPlayer.setDataSource(this@MainActivity, Uri.parse(music_list.get(music_current_index)));
                    //第三步：准备
                    mediaPlayer.prepare();
                    tv_title.text = music_list.get(music_current_index)
                        .substring(music_list.get(music_current_index).lastIndexOf("/") + 1)
                    mediaPlayer.setOnCompletionListener {
                        when (player_form) {
                            0 ->
                                music_current_index = ++music_current_index % music_list.size
                            1 -> {
                            }
                            2 ->
                                music_current_index = (0..music_list.size - 1).random()
                        }
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(music_list.get(music_current_index))
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        musicFragmentDialogDialog.music_list_adapter.mMusicListIndex=temp_list.indexOf(music_list.get(music_current_index))
                        musicFragmentDialogDialog.mMusicListIndex=musicFragmentDialogDialog.music_list_adapter.mMusicListIndex
                        musicFragmentDialogDialog.music_list_adapter.notifyDataSetChanged()
                        musicFragmentDialogDialog.tv_title.text= "当前播放:"+music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/")+1)
                        i = 1;
                        btn_play.setImageResource(R.drawable.b4x)
                        tv_title.text = music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/") + 1)
                        animator.start()

                    }
                }
            }

            var thread:Thread=Thread()
            thread.run {
                GetLocalMusic();
                handler.sendEmptyMessage(5)
            }
            thread.start()


        }

    }
//获取本地音乐文件
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
//初始化控件
    fun Init() {
        mediaPlayer = MediaPlayer()
        music_list = ArrayList<String>()
        btn_form = findViewById(R.id.ibtn_form)
        btn_back = findViewById(R.id.ibtn_back)
        btn_play = findViewById(R.id.ibtn_play)
        btn_forward = findViewById(R.id.ibtn_forward)
        btn_other = findViewById(R.id.ibtn_other)
        tv_title = findViewById(R.id.tv_music_title)

    tv_title.setSelected(true)
        iv_bg=findViewById(R.id.iv_music_bg)
        btn_form.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        btn_other.setOnClickListener(this);
     animator=ObjectAnimator.ofFloat(iv_bg,"rotation",0f,360f)
    animator.duration=7000
    animator.setRepeatMode(ObjectAnimator.RESTART);
    animator.repeatCount= ObjectAnimator.INFINITE
     currentPlayTime=0

    }
//按钮监听事件
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            //播放形式：循环，单曲循环，随机
            R.id.ibtn_form -> {
                player_form=++player_form%3;
                when(player_form){
                    0-> (p0 as ImageButton).setImageResource(R.drawable.b3v)//循环
                    1-> (p0 as ImageButton).setImageResource(R.drawable.b3z)//单曲循环
                    2-> (p0 as ImageButton).setImageResource(R.drawable.b54)//随机
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
                animator.start()

            }
            //播放与暂停
            R.id.ibtn_play -> {
                i = ++i % 2;

                when (i) {
                    //播放
                    1 -> {
                        mediaPlayer.start()
                        (p0 as ImageButton).setImageResource(R.drawable.b4x)
                        animator.start()
                        animator.setCurrentPlayTime(currentPlayTime);
                    }
                    //暂停
                    0 -> {
                        mediaPlayer.pause()
                        currentPlayTime = animator.getCurrentPlayTime();
                        animator.cancel();
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
                    tv_title.text = music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/") + 1)
                animator.start()
                animator.setCurrentPlayTime(currentPlayTime);
            }
            R.id.ibtn_other -> {
            var music_list_index=temp_list.indexOf(music_list.get(music_current_index))
             musicFragmentDialogDialog=MusicFragmentDialog(this,temp_list,music_list.get(music_current_index),music_list_index)
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
        var hasPermissionDismiss: Boolean = false
        if (requestCode == 101) {
            for (i in 0..grantResults.size - 1) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "获取读权限", Toast.LENGTH_SHORT).show()
                } else {
                    hasPermissionDismiss = true
                    Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show()
                    break;
                }
            }
        }
        if (!hasPermissionDismiss) {
            Init()
            val handler: Handler = object : Handler() {
              override  fun handleMessage(msg: Message) {
                  temp_list = ArrayList<String>()
                  for (i in music_list) temp_list.add(i)
                  //第一步：重置
                  mediaPlayer.reset();
                  //第二步:设置数据源（这里播放的是本地文件，可播放其他文件）
                  mediaPlayer.setDataSource(this@MainActivity, Uri.parse(music_list.get(music_current_index)));
                  //第三步：准备
                  mediaPlayer.prepare();
                  tv_title.text = music_list.get(music_current_index)
                      .substring(music_list.get(music_current_index).lastIndexOf("/") + 1)
                  mediaPlayer.setOnCompletionListener {
                      when (player_form) {
                          0 ->
                              music_current_index = ++music_current_index % music_list.size
                          1 -> {
                          }
                          2 ->
                              music_current_index = (0..music_list.size - 1).random()
                      }
                      mediaPlayer.reset()
                      mediaPlayer.setDataSource(music_list.get(music_current_index))
                      mediaPlayer.prepare()
                      mediaPlayer.start()
                      musicFragmentDialogDialog.music_list_adapter.mMusicListIndex=temp_list.indexOf(music_list.get(music_current_index))
                      musicFragmentDialogDialog.mMusicListIndex=musicFragmentDialogDialog.music_list_adapter.mMusicListIndex
                      musicFragmentDialogDialog.music_list_adapter.notifyDataSetChanged()
                      musicFragmentDialogDialog.tv_title.text= "当前播放:"+music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/")+1)
                      i = 1;
                      btn_play.setImageResource(R.drawable.b4x)
                      tv_title.text = music_list.get(music_current_index).substring(music_list.get(music_current_index).lastIndexOf("/") + 1)
                      animator.start()

                  }
                }
            }

            var thread:Thread=Thread()
            thread.run {
                GetLocalMusic();
                handler.sendEmptyMessage(5)
            }
            thread.start()
        }
    }
    override fun OnClick(index: Int) {
    if(index==-1){
for(i in temp_list.size-1 downTo 0)temp_list.removeAt(i)
    }
    else if(index==-2){player_form=0}
    else if(index==-3){player_form=1}
    else if(index==-4){player_form=2}
    else {
    music_current_index = index;
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
    animator.start()
    }
    }
}