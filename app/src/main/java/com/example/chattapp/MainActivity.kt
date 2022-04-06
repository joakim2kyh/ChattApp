package com.example.chattapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattapp.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmConfiguration

private lateinit var binder: ActivityMainBinding
private lateinit var userDao: UserDao
private lateinit var realmListener: RealmChangeListener<Realm>
private lateinit var usersList: ArrayList<Contact>

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

//creates or initializes the database
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("chatAppDB")
            .allowWritesOnUiThread(true)
            .schemaVersion(1)
            .build()
        Realm.setDefaultConfiguration(config)

        userDao = UserDao()
        loadList()

//creates and add a listener to database to update everytime new items are added
        realmListener = RealmChangeListener {

            loadList()
        }
        userDao.db.addChangeListener(realmListener)

        binder.addUserBtn.setOnClickListener {
            DialogMaker.createChat(this, userDao)

        }

    }

    /**
     * loads the user list in the recycler view
     */
    private fun loadList() {

        binder.chatsList.layoutManager = LinearLayoutManager(this)
        usersList = userDao.getUsers()
        val adapter = MyAdapter((usersList),{position -> onListItemClick(position)},{position -> onListItemLongClick(position)})
        binder.chatsList.adapter = adapter

    }

    private fun onListItemClick(position: Int) {

        Toast.makeText(this, "click detected position $position", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun onListItemLongClick(position: Int){

        val id = usersList[position].id
        userDao.deleteUser(id)

    }
}