package com.emika.app.presentation.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LayoutAnimationController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
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
import com.google.android.material.transition.MaterialFadeThrough
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class ChatFragment : Fragment() {
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
    private val getMessage = Observer { chat: PayloadChat? ->
        if (chat != null) {
            if (chat.messages != null) {
                adapter = ChatAdapterOld(context, chat.messages)
                chatRecycler!!.adapter = adapter
                chatRecycler!!.scrollToPosition(0)
            }
        }
    }
    private val onUpdateSuggestion = Emitter.Listener { args ->
        activity!!.runOnUiThread {
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
        activity!!.runOnUiThread {
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
                if (chatFragment!!.isVisible) socket!!.emit("server_read_messages", tokenJson)

//            adapter.notifyItemInserted(0);
//            adapter.notifyDataSetChanged();
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)
        initViews(view)
        activity!!.findViewById<ConstraintLayout>(R.id.main_part).visibility = View.GONE

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enterTransition = MaterialFadeThrough.create()

    }

    private fun initViews(view: View) {
        chatFragment = parentFragmentManager.findFragmentByTag("chatFragment") as ChatFragment?
        token = activity!!.intent.getStringExtra("token")
        tokenJson = JSONObject()
        try {
            tokenJson!!.put("token", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        adapter = ChatAdapterOld()
        EmikaApplication.instance.component?.inject(this)
        emikaImg = view.findViewById(R.id.chat_emika_img)
        socket = EmikaApplication.instance.socket
        Glide.with(context!!).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(emikaImg!!)
        socket!!.on("new_message", onNewMessage)
        socket!!.on("update_suggestions", onUpdateSuggestion)
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(ChatViewModel::class.java)
        chatRecycler = view.findViewById(R.id.chat_recycler)
        quickAnswerRecycler = view.findViewById(R.id.recycler_chat_quick_answer)
        val horizontal = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false)
        quickAnswerRecycler!!.layoutManager = horizontal
        quickAnswerRecycler!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        chatRecycler!!.layoutManager = layoutManager
        chatRecycler!!.setHasFixedSize(true)
        KeyboardVisibilityEvent.setEventListener(activity!!) { isOpen: Boolean ->
            if (isOpen) chatRecycler!!.scrollToPosition(0)
            if (!isOpen) chatRecycler!!.scrollToPosition(0)
        }
        //        viewModel.getItemPagedList().observe(getViewLifecycleOwner(), items -> {
//            adapter.submitList(items);
//            chatRecycler.scrollToPosition(0);
//        });
//        chatRecycler.setAdapter(adapter);
        viewModel!!.getMessageMutableLiveData(0, 200).observe(viewLifecycleOwner, getMessage)
        sendMessage = view.findViewById(R.id.chat_send_message)
        sendMessage!!.setOnClickListener { view: View -> sendMessage(view) }
        messageBody = view.findViewById(R.id.chat_body_message)
    }

    override fun onDestroy() {
        activity!!.findViewById<ConstraintLayout>(R.id.main_part).visibility = View.VISIBLE

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