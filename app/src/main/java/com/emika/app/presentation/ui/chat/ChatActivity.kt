package com.emika.app.presentation.ui.chat

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.chat.Message
import com.emika.app.data.network.pojo.chat.PayloadChat
import com.emika.app.data.network.pojo.chat.Suggestion
import com.emika.app.deprecated.ChatAdapterOld
import com.emika.app.di.User
import com.emika.app.presentation.adapter.chat.QuickAnswerAdapter
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.chat.ChatViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class ChatActivity : AppCompatActivity() {


    @JvmField
    @Inject
    var user: User? = null
    private var viewModel: ChatViewModel? = null
    private var token: String? = null
    private var messageBody: EditText? = null
    private var chatRecycler: RecyclerView? = null
    private var quickAnswerRecycler: RecyclerView? = null
    private var quickAnswerAdapter: QuickAnswerAdapter? = null
    private var adapter: ChatAdapterOld? = null
    private val offset = 0
    private val limit = 25
    private var sendMessage: ImageButton? = null
    private var socket: Socket? = null
    private var emikaImg: ImageView? = null
    private var tokenJson: JSONObject? = null
    private var chatFragment: ChatFragment? = null
    private val getMessage = Observer { chat: PayloadChat ->
            if (chat.messages != null) {
                adapter = ChatAdapterOld(this, chat.messages)
                chatRecycler!!.adapter = adapter
                chatRecycler!!.scrollToPosition(0)
            }

    }
    private val onUpdateSuggestion = Emitter.Listener { args ->
        runOnUiThread {
            var text: String
            var active: Boolean
            var suggestion: Suggestion
            val suggestions: MutableList<Suggestion> = ArrayList()
            try {
                val jsonArray = JSONArray(Arrays.toString(args))
                val array = jsonArray.getJSONArray(0)
                for (i in 0 until array.length()) {
                    var jsonObject = JSONObject()
                    jsonObject = array.getJSONObject(i)
                    text = jsonObject.getString("text")
                    suggestion = Suggestion()
                    suggestion.text = text
                    suggestions.add(suggestion)
                }
                if (suggestions.size > 0) {
                    quickAnswerRecycler!!.visibility = View.VISIBLE
                    quickAnswerAdapter = QuickAnswerAdapter(suggestions, viewModel, adapter)
                    quickAnswerRecycler!!.adapter = quickAnswerAdapter
                } else {
                    quickAnswerRecycler!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    private val onNewMessage = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            val text: String
            val createdAt: String
            val id: String
            val isEmika: Boolean
            val message: Message
            //        PagedList<Message> messagePagedList = adapter.getCurrentList();
            try {
                val jsonArray = JSONArray(Arrays.toString(args))
                val jsonObject = jsonArray.getJSONObject(0)
                createdAt = jsonObject.getString("created_at")
                isEmika = jsonObject.getBoolean("is_emika")
                id = jsonObject.getString("account_id")
                text = jsonObject.getString("text")
                message = Message()
                message.id = id
                message.isEmika = isEmika
                message.text = text
                message.createdAt = createdAt
                //            if (message.getId().equals("emika"))
//            new Thread(()->{
//                adapter.update(message);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//            else
                adapter!!.update(message)
                chatRecycler!!.scrollToPosition(0)
                socket!!.emit("server_read_messages", tokenJson)

//            adapter.notifyItemInserted(0);
//            adapter.notifyDataSetChanged();
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_fragment)
        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.overflowIcon!!.setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white)
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.title = "Calendar"
        toolbar.setNavigationOnClickListener{ onBackPressed() }
        initViews()

//        enterTransition = MaterialFadeThrough.create()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)


    }

    private fun initViews() {
//        chatFragment = parentFragmentManager.findFragmentByTag("chatFragment") as ChatFragment?
        token = EmikaApplication.instance.sharedPreferences?.getString("token", null)
        tokenJson = JSONObject()
        try {
            tokenJson!!.put("token", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        adapter = ChatAdapterOld()
        EmikaApplication.instance.component?.inject(this)
        emikaImg = findViewById(R.id.chat_emika_img)
        socket = EmikaApplication.instance.socket
        Glide.with(this).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(emikaImg!!)
        socket!!.on("new_message", onNewMessage)
        socket!!.on("update_suggestions", onUpdateSuggestion)
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(ChatViewModel::class.java)
        chatRecycler = findViewById(R.id.chat_recycler)
        quickAnswerRecycler = findViewById(R.id.recycler_chat_quick_answer)
        val horizontal = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false)
        quickAnswerRecycler!!.layoutManager = horizontal
        quickAnswerRecycler!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        chatRecycler!!.layoutManager = layoutManager
        chatRecycler!!.setHasFixedSize(true)
        KeyboardVisibilityEvent.setEventListener(this) { isOpen: Boolean ->
            if (isOpen) chatRecycler!!.scrollToPosition(0)
            if (!isOpen) chatRecycler!!.scrollToPosition(0)
        }
        //        viewModel.getItemPagedList().observe(getViewLifecycleOwner(), items -> {
//            adapter.submitList(items);
//            chatRecycler.scrollToPosition(0);
//        });
//        chatRecycler.setAdapter(adapter);
        viewModel!!.getMessageMutableLiveData(0, 200).observe(this, getMessage)
        sendMessage = findViewById(R.id.chat_send_message)
        sendMessage!!.setOnClickListener { view: View -> sendMessage(view) }
        messageBody = findViewById(R.id.chat_body_message)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun sendMessage(view: View) {
        if (!messageBody!!.text.toString().isEmpty()) {
            val message = Message()
            message.text = messageBody!!.text.toString()
            message.accountId = user!!.id
            message.isEmika = false
            viewModel!!.sendMessage(message)
            messageBody!!.setText("")
        } else {
            messageBody!!.requestFocus()
            messageBody!!.error = "You can't send empty message"
        }
    }

    companion object {
        private const val TAG = "ChatFragment"
    }

}
