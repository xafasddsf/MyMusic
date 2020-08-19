package com.example.mymusic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

class MusicListAdapter(var mContext: Context,var list:ArrayList<String>): BaseAdapter() {
    class MusicHolder(var music_name:TextView?=null,var btn_delete:ImageButton?=null){}
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
       var musicHolder:MusicHolder?=null
        var view:View
        if(p1==null){
            view=LayoutInflater.from(mContext).inflate(R.layout.music_item_layout,null);
            musicHolder= MusicHolder();
            musicHolder.music_name=view.findViewById(R.id.tv_music_name)
            musicHolder.btn_delete=view.findViewById(R.id.ibtn_music_delete)
            view?.tag=musicHolder
        }
        else {
            view=p1;
            musicHolder= view.tag as MusicHolder?}
        musicHolder?.music_name?.text=list.get(p0).substring(list.get(p0).lastIndexOf("/")+1)
        return view
    }

    override fun getItem(p0: Int): Any {
        return list.get(p0)
    }

    override fun getItemId(p0: Int): Long {
       return p0.toLong()
    }

    override fun getCount(): Int {
     return list.size
    }

}