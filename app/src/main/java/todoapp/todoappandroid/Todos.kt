package todoapp.todoappandroid

import android.content.SharedPreferences

class Todos {
    private var todos: MutableList<Todo> = ArrayList()

    fun getTodos(): List<Todo> = todos

    fun get(index: Int): Todo = todos.get(index)

    fun add(todo: String) {
        todos.add(Todo(todo))
    }

    fun getSize(): Int = todos.size

    fun setDone(index: Int, done: Boolean) {
        todos.get(index).done = done
    }

    fun remove(index: Int) {
        todos.removeAt(index)
    }

    fun clearCompleted() {
        todos.removeAll { it.done }
    }

    fun saveState(prefs: SharedPreferences) {
        with(prefs.edit()) {
            if (todos.isEmpty()) {
                putString("todo_titles", null)
                putString("todo_done", null)
            } else {
                putString("todo_titles", todos.joinToString("\n") { it.title })
                putString("todo_done", todos.joinToString("\n") { if (it.done) "1" else "0" })
            }
            commit()
        }
    }

    fun restoreState(prefs: SharedPreferences) {
        todos.clear()
        val todoTitlesStr = prefs.getString("todo_titles", null)
        val todoDoneStr = prefs.getString("todo_done", null)
        if (todoTitlesStr != null && todoTitlesStr.isNotEmpty() && todoDoneStr != null) {
            val todoTitles = todoTitlesStr.split('\n')
            val todoDones = todoDoneStr.split('\n')

            for (i in 0..(todoTitles.size - 1)) {
                val todo = Todo(todoTitles[i])
                todo.done = todoDones[i] == "1"
                todos.add(todo)
            }
        }
    }
}

class Todo(var title: String,
           var done: Boolean = false)
