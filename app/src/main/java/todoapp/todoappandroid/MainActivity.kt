package todoapp.todoappandroid

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    var todos: Todos = Todos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText.setOnEditorActionListener { textView, actionId, event ->
            if ((actionId == EditorInfo.IME_NULL && event.action == KeyEvent.ACTION_DOWN) ||
                    actionId == EditorInfo.IME_ACTION_NEXT) {
                onAddTodo(textView.text)
                textView.text = ""
            }
            true
        }

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        todoList.layoutManager = layoutManager
        todoList.adapter = MyAdapter(todos)

        clearCompletedBtn.setOnClickListener {
            todos.clearCompleted()
            todoList.adapter.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs: SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        todos.saveState(prefs)
    }

    override fun onResume() {
        super.onResume()
        val prefs: SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        todos.restoreState(prefs)
    }

    private fun onAddTodo(todo: CharSequence) {
        if (!todo.isEmpty()) {
            todos.add(todo.toString())
            todoList.adapter.notifyItemInserted(todos.getSize())
        }
    }
}


class MyAdapter(private val todos: Todos) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_list_view, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val todo = todos.get(position)

        holder.view.findViewById<TextView>(R.id.titleText).text = todo.title

        val doneCheck = holder.view.findViewById<CheckBox>(R.id.doneCheck)
        doneCheck.isChecked = todo.done
        doneCheck.setOnCheckedChangeListener { button, checked ->
            onSetDone(holder.adapterPosition, checked)
        }

        val delBtn = holder.view.findViewById<ImageButton>(R.id.delBtn)
        delBtn.setOnClickListener {
            onDeleteTodo(holder.adapterPosition)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = todos.getSize()

    private fun onSetDone(position: Int, done: Boolean) {
        todos.setDone(position, done)
    }

    private fun onDeleteTodo(position: Int) {
        todos.remove(position)
        notifyItemRemoved(position)
    }
}
