package kr.ac.kumoh.s20181105.homeworkproject.ui.online

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import kr.ac.kumoh.s20181105.homeworkproject.MySingleton
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class OnlineViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val QUEUE_TAG = "VolleyRequest" //태그
        val SERVER_URL = "http://202.31.200.54:8080" //주소
    }

    val mQueue:RequestQueue
    data class Game (var id:Int, var name: String, var genre: String, var image: String, var company:String,var engine:String)
    //game data 클래스
    val list = MutableLiveData<ArrayList<Game>>()
    private val game = ArrayList<Game>()

    val imageLoader: ImageLoader

    init { //시작 시
        list.value = game
        mQueue  = MySingleton.getInstance(getApplication()).requestQueue
        imageLoader = MySingleton.getInstance(getApplication()).imageLoader
        requestGame()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/game_image/" + URLEncoder.encode(game[i].image, "utf-8")

    fun requestGame() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/online_game",
            null,
            {
                game.clear()
                parseJson(it)
                list.value = game
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )
        request.tag = QUEUE_TAG
        MySingleton.getInstance(getApplication()).addToRequestQueue(request)
    }

    fun getGame(i: Int) = game[i]
    fun getSize() = game.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) { //json 저장
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id =item.getInt("name_id")
            val genre = item.getString("genre")
            val name = item.getString("name")
            val image = item.getString("image")
            val company = item.getString("company_name")
            val engine = item.getString("engine_name")
            game.add(Game(id,name,genre,image,company,engine))
        }
    }
}