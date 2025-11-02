package com.kmou.capstondesignarchive.Home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kmou.capstondesignarchive.R


class ContentAdapter(private val items: List<ContentItem>) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    // 뷰 홀더: item_content.xml의 뷰들을 보관하는 클래스
    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemTitle: TextView = itemView.findViewById(R.id.item_title)
        val itemSubtitle: TextView = itemView.findViewById(R.id.item_subtitle)

        // ✅ (수정) 아이템 클릭 리스너 구현
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                // 클릭된 위치가 유효한지 확인 (애니메이션 중 클릭 등 예외 방지)
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = items[position]

                    // DetailActivity로 이동할 Intent 생성
                    val intent = Intent(itemView.context, DetailActivity::class.java)

                    // ✅ 클릭된 아이템의 정보를 Intent에 추가
                    // 나중에는 title 대신 Firebase의 고유 ID를 전달하게 됩니다.
                    intent.putExtra("title", clickedItem.title)
                    intent.putExtra("subtitle", clickedItem.subtitle)

                    // 생성한 Intent로 DetailActivity 시작
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    // 뷰 홀더 생성 (레이아웃 인플레이트)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_card, parent, false)
        return ContentViewHolder(view)
    }

    // 뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val item = items[position]
        holder.itemImage.setImageResource(item.imageResId)
        holder.itemTitle.text = item.title
        holder.itemSubtitle.text = item.subtitle
    }

    // 아이템 총 개수
    override fun getItemCount(): Int {
        return items.size
    }
}