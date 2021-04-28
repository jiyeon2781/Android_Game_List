package kr.ac.kumoh.s20181105.homeworkproject.ui.video


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

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val QUEUE_TAG = "VolleyRequest" //큐태그에 달아줄 String
        val SERVER_URL = "http://202.31.200.54:8080"//서버 주소

    }
    val mQueue: RequestQueue
    data class Game (var id:Int, var name: String, var genre: String, var image: String, var company:String, var engine:String) //game data 저장 class

    val list = MutableLiveData<ArrayList<Game>>()
    private val game = ArrayList<Game>()


    val imageLoader: ImageLoader
    init { //시작시
        list.value = game //list value에 game data 대입
        mQueue  = MySingleton.getInstance(getApplication()).requestQueue //request queue 가져옴
        imageLoader = MySingleton.getInstance(getApplication()).imageLoader //imageloader 가져옴
        requestGame() //game request 시작
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/game_image/" + URLEncoder.encode(game[i].image, "utf-8")

    fun requestGame() {
        val request = JsonArrayRequest( //json array를 받아온다
            Request.Method.GET,
            "$SERVER_URL/video_game",
            null,
            {
                game.clear()
                parseJson(it) //json 저장
                list.value = game

            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )
        request.tag = QUEUE_TAG //태그 작성
        MySingleton.getInstance(getApplication()).addToRequestQueue(request) //request add
    }

    fun getGame(i: Int) = game[i] //game index 하나 가져옴
    fun getSize() = game.size //game size 저장

    override fun onCleared() { //queue tag 삭제
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) { //json 저장
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id =item.getInt("name_id") //게임 id
            val genre = item.getString("genre") //게임 장르
            val name = item.getString("name") //게임 이름
            val image = item.getString("image") //게임 이미지 이름
            val company = item.getString("company_name") // 회사 이름
            val engine = item.getString("engine_name") //개발 엔진 이름
            game.add(Game(id,name,genre,image,company,engine))
        }
    }
}