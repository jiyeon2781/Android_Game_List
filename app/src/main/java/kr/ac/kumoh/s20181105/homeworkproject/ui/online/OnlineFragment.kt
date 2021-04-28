package kr.ac.kumoh.s20181105.homeworkproject.ui.online

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20181105.homeworkproject.InfoActivity
import kr.ac.kumoh.s20181105.homeworkproject.R
class OnlineFragment : Fragment() {

    private lateinit var model: OnlineViewModel
    private val mAdapter = GameAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        model = ViewModelProvider(activity as AppCompatActivity).get(OnlineViewModel::class.java)
        model.list.observe(viewLifecycleOwner,Observer<ArrayList<OnlineViewModel.Game>> {
            mAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_online, container, false)

        val lsResult_online = root.findViewById<RecyclerView>(R.id.lsResult_online)
        lsResult_online.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        return root
    }

    inner class GameAdapter() : RecyclerView.Adapter<GameAdapter.ViewHolder>(){ //게임 어댑터
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val txName:TextView = itemView.findViewById<TextView>(R.id.txName)
            val txGenre:TextView = itemView.findViewById<TextView>(R.id.txGenre)
            val imImage:NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.imImage)
            val txCompany:TextView = itemView.findViewById<TextView>(R.id.txCompany)

            init {
                imImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
            }
        }

        override fun getItemCount(): Int { //리스트 아이템의 개수
            return model.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_game,parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: GameAdapter.ViewHolder, position: Int) {
            holder.txName.text = model.getGame(position).name
            holder.txGenre.text =model.getGame(position).genre
            holder.txCompany.text = "${model.getGame(position).company} (${model.getGame(position).engine})"
            holder.imImage.setImageUrl(model.getImageUrl(position),model.imageLoader)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, InfoActivity::class.java)
                intent.putExtra("GAME_ID",model.getGame(position).id.toString())
                startActivity(intent) //상세 정보 액티비티로 이동
            }
        }

    }
}
