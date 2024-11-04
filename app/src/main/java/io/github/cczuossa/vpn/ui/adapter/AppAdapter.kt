package io.github.cczuossa.vpn.ui.adapter

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import io.github.cczuossa.vpn.R
import androidx.recyclerview.widget.RecyclerView
import io.github.cczuossa.vpn.utils.log

class AppAdapter(val apps: List<ApplicationInfo>) : RecyclerView.Adapter<AppAdapterHolder>() {
    val selects = hashSetOf<String>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AppAdapterHolder {
        return AppAdapterHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.app_item, p0, false), this
        )
    }

    override fun onBindViewHolder(p0: AppAdapterHolder, p1: Int) {
        p0.init(apps[p1])
    }

    override fun getItemCount(): Int {
        "apps count: ${apps.size}".log()
        return apps.size
    }

    fun selectAll() {
        apps.forEach {
            selects.add(it.packageName)
        }
        notifyDataSetChanged()
    }

    fun unselectAll() {
        selects.clear()
        notifyDataSetChanged()
    }

}

class AppAdapterHolder(v: View, adapter: AppAdapter) : RecyclerView.ViewHolder(v) {
    private val icon: ImageView = v.findViewById<ImageView>(R.id.app_item_icon)
    private val title: TextView = v.findViewById<TextView>(R.id.app_item_title)
    private val check: CheckBox = v.findViewById<CheckBox>(R.id.app_item_check)
    private val adapter = adapter;

    fun init(info: ApplicationInfo) {
        "init app info $info".log()
        title.text = title.context.packageManager.getApplicationLabel(info)
        icon.setImageDrawable(info.loadIcon(icon.context.packageManager))
        check.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                adapter.selects.add(info.packageName)
            else
                adapter.selects.remove(info.packageName)
        }
        check.isChecked = adapter.selects.contains(info.packageName)
    }

}