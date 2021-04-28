package kr.ac.kumoh.s20181105.homeworkproject.ui.mobile

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

class MobileViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest" //태그
        val SERVER_URL = "http://202.31.200.54:8080" //주소
    }

    val mQueue: RequestQueue //request Queue
    val imageLoader:ImageLoader //image loader

    data class Game (var id:Int, var name: String, var genre: String, var image: String, var company:String,var engine:String)
    //게임 데이터 저장 클래스
    val list = MutableLiveData<ArrayList<Game>>()
    private val game = ArrayList<Game>()
    init { //시작 시
        list.value = game
        mQueue  = MySingleton.getInstance(getApplication()).requestQueue //request queue 가져오기
        imageLoader = MySingleton.getInstance(getApplication()).imageLoader //imageloader 가져오기
        requestGame()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/game_image/" + URLEncoder.encode(game[i].image, "utf-8") //이미지 url 지정

    fun requestGame() {
        val request = JsonArrayRequest( //json array 불러오기
            Request.Method.GET,
            "$SERVER_URL/mobile_game",
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
        request.tag = QUEUE_TAG //태그 설정
        MySingleton.getInstance(getApplication()).addToRequestQueue(request) //저장
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