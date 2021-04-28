package kr.ac.kumoh.s20181105.homeworkproject
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_info.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.CookieHandler
import java.net.CookieManager

class InfoActivity : AppCompatActivity() {

    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "http://202.31.200.54:8080/"
    }

    data class Game (var id:Int, var name: String, var genre: String, var year:Int, var image: String, var company:String,var platform: String,var engine:String,var intro:String)
    //game data class
    var mArray = ArrayList<Game>()
    lateinit var mQueue: RequestQueue //request queue
    lateinit var id:String

    lateinit var mImageLoader: ImageLoader //이미지 url loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        id = intent.getStringExtra("GAME_ID").toString() //game id를 받아와 저장

        CookieHandler.setDefault(CookieManager())
        mQueue  = MySingleton.getInstance(getApplication()).requestQueue //singleton을 사용해 requestqueue를 가져옴
        mImageLoader = MySingleton.getInstance(getApplication()).imageLoader //singleton을 사용해 imageloader를 가져옴
        requestGame()

    }

    override fun onStop() {
        super.onStop()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun requestGame() {
        val request = JsonArrayRequest(Request.Method.GET, "${SERVER_URL}select?id=${id.toInt()}",null,{ //jsonarray request
            draw_json_detail(it) //json을 받아와 text 저장
        }
            , Response.ErrorListener { error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show() })
        request.tag = QUEUE_TAG //태그를 걸어줌
        MySingleton.getInstance(getApplication()).addToRequestQueue(request) //추가
    }

    private fun draw_json_detail(items: JSONArray){  //game 정보를 가져와 text에 저장하는 부분
        mArray.clear()

        val item: JSONObject = items[0] as JSONObject //상세정보 데이터 1개 받아옴
        val id =item.getInt("name_id") //게임 id
        val genre = item.getString("genre") //게임 장르
        val year = item.getInt("release_year") //출시연도
        val name = item.getString("name") //게임 이름
        val image = item.getString("image") //이미지 이름
        val company = item.getString("company_name") //회사 이름
        val platform = item.getString("platform1") //유통 플랫폼
        val engine = item.getString("engine_name") //개발 엔진
        val intro = item.getString("introduction") //간단한 소개
        mArray.add(Game(id, name, genre,year, image, company,platform,engine,intro)) //array에 저장
        setTitle(mArray[0].name); //타이틀 지정

        imImage.setImageUrl("${SERVER_URL}game_image/${mArray[0].image}",mImageLoader) //이미지 세팅
        txGenre.text = "장르 : ${mArray[0].genre}"
        txCompany.text = "개발 회사 : ${mArray[0].company}"
        txName.text = mArray[0].name
        txYear.text =  "출시 연도 : ${mArray[0].year}"
        txPlatform.text =  "실행 가능한 플랫폼 : ${mArray[0].platform}"
        txEngine.text = "개발 엔진 : ${mArray[0].engine}"
        txIntro.text = mArray[0].intro
        //각 정보들 출력하는 부분

    }

}