package com.example.mymusic

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment

class MusicFragmentDialog(var mContext: Context, var mList:ArrayList<String>,var mCurrentMusicName:String,var mMusicListIndex:Int,var mListener:Listener?=null): DialogFragment()
     {
    lateinit var music_list: ListView
    lateinit var tv_title:TextView
         lateinit var tv_music_list_form:TextView
         lateinit var music_list_adapter:MusicListAdapter
         lateinit var ibtn_delete_all:ImageButton
         var music_player_form:Int=0;
    override fun onAttach(context: Context) {
         super.onAttach(context)
         mListener=context as Listener
     }

    override fun onAttach(activity: Activity) {
         super.onAttach(activity)
         mListener=activity as Listener
     }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val lp = dialog?.window?.attributes
        lp?.gravity = Gravity.BOTTOM // 紧贴底部
        lp?.width = ViewGroup.LayoutParams.MATCH_PARENT // 宽度持平
        dialog?.window?.attributes = lp
        //dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        var view:View=inflater.inflate(R.layout.music_fragment_diaglog_layout,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        music_list_adapter=MusicListAdapter(mContext,mList,mMusicListIndex)
        tv_music_list_form=view.findViewById(R.id.tv_music_list_form)
        var drawable=resources.getDrawable(R.drawable.b3v)
        drawable.setBounds(0,0,100,100)
        tv_music_list_form.setCompoundDrawables(drawable,null,null,null)
        tv_music_list_form.setCompoundDrawablePadding(4);
        tv_music_list_form.setOnClickListener(View.OnClickListener {
            music_player_form=++music_player_form%3;
            when(music_player_form){
                0->{
                    mListener?.OnClick(-2)
                    tv_music_list_form.text="循环列表"
                    var drawable=resources.getDrawable(R.drawable.b3v)
                    drawable.setBounds(0,0,100,100)
                tv_music_list_form.setCompoundDrawables(drawable,null,null,null)
                    tv_music_list_form.setCompoundDrawablePadding(4);
                }
                1->{
                    mListener?.OnClick(-3)
                    tv_music_list_form.text="单曲循环"
                    var drawable=resources.getDrawable(R.drawable.b3z)
                    drawable.setBounds(0,0,100,100)
                    tv_music_list_form.setCompoundDrawables(drawable,null,null,null)
                    tv_music_list_form.setCompoundDrawablePadding(4);
                }
                2->{
                    mListener?.OnClick(-4)
                    tv_music_list_form.text="随机播放"
                    var drawable=resources.getDrawable(R.drawable.b54)
                    drawable.setBounds(0,0,100,100)
                    tv_music_list_form.setCompoundDrawables(drawable,null,null,null)
                    tv_music_list_form.setCompoundDrawablePadding(4);
                }
            }
        })
        ibtn_delete_all=view.findViewById(R.id.ibtn_music_delete_all)
        ibtn_delete_all.setOnClickListener(View.OnClickListener {
            var dialog:AlertDialog.Builder=AlertDialog.Builder(mContext)
            dialog.setMessage("确定要清空播放列表？")
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                mListener?.OnClick(-1)
                music_list_adapter.notifyDataSetChanged()
            }).setNegativeButton("取消", DialogInterface.OnClickListener { dialogInterface, i ->
            })
            dialog.create()
         dialog.show()
        })
        tv_title=view.findViewById(R.id.tv_title)
        tv_title.text=tv_title.text.toString()+mCurrentMusicName.substring(mCurrentMusicName.lastIndexOf("/")+1)
        music_list=view.findViewById(R.id.lv_music_list)
        music_list.adapter=music_list_adapter
        music_list?.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->
            mListener?.OnClick(i)
        } )
    }
    interface Listener{
        fun OnClick(index:Int)
    }



}