package com.kmou.capstondesignarchive.Search

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kmou.capstondesignarchive.Home.DetailActivity
import com.kmou.capstondesignarchive.R

class SearchAdapter(private val projectList: List<Project>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.projectTitle)
        val team: TextView = itemView.findViewById(R.id.teamName)
        val department: TextView = itemView.findViewById(R.id.departmentName)
        val summary: TextView = itemView.findViewById(R.id.projectSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = projectList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projectList[position]
        holder.title.text = project.title
        holder.team.text = project.team
        holder.department.text = project.department
        holder.summary.text = project.summary

        // ✅ 클릭 시 DetailActivity로 이동
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("title", project.title)
            intent.putExtra("team", project.team)
            intent.putExtra("department", project.department)
            intent.putExtra("summary", project.summary)
            intent.putExtra("createdAt", project.createdAt)
            context.startActivity(intent)
        }
    }
}
