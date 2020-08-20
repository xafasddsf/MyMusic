package com.example.mymusic

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class MusicFragmentDialog(var mContext: Context, var mList:ArrayList<String>,var mCurrentMusicName:String,var mMusicListIndex:Int,var mListener:Listener?=null): DialogFragment()
     {
    lateinit var music_list: ListView
    lateinit var tv_title:TextView
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
        tv_title=view.findViewById(R.id.tv_title)
        tv_title.text=tv_title.text.toString()+mCurrentMusicName.substring(mCurrentMusicName.lastIndexOf("/")+1)
        music_list=view.findViewById(R.id.lv_music_list)
        music_list.adapter=MusicListAdapter(mContext,mList,mMusicListIndex)
        music_list?.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->
            mListener?.OnClick(i)

        } )
    }


    interface Listener{
        fun OnClick(index:Int)
    }



}