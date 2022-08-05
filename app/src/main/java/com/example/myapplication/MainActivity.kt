package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.myapplication.Fragments.ForgotpasswordFragment
import com.example.myapplication.Fragments.HomeFragment
import com.example.myapplication.Fragments.LoginFragment
import com.example.myapplication.Fragments.RegisterFragment
import java.io.File
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // injecter le fragment dans notre boite
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, LoginFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    val sauvegarde = mutableListOf<String>()

   /* @SuppressLint("SetTextI18n")
    fun scannerEtAjout(){
        val scanner = Scanner(File("data/listWords.csv"))
        val listeMots = mutableListOf<String>()

        while (scanner.hasNextLine()){
            val i = scanner.nextLine()
            listeMots.add(i)
        }
        for (j in 0 until 200){
            val rand = Random.nextInt(0, listeMots.size)
            sauvegarde.add(listeMots[rand])

        }
        println(sauvegarde)
<<<<<<< HEAD
        for(i in sauvegarde){
            val view : TextView = findViewById(R.id.text_randomList)
            view.text = view.text.toString() + i
        }
    }*/
=======
        //for(i in sauvegarde){
        //    val view : TextView = findViewById(R.id.text_randomList)
        //     view.text = view.text.toString() + i
        //}
    }

    /*
     *  boutons pour changer de vue
     */


    fun buttonGotoRegister(view: View) {
        // injecter le fragment dans notre boite
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, RegisterFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun buttonGotoLogin(view: View) {
        // injecter le fragment dans notre boite
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, LoginFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun buttongotoForgotpassword(view: View) {
        // injecter le fragment dans notre boite
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, ForgotpasswordFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun buttonlogin(view: View) {

        // SYSTEME DE LOGIN

        // injecter le fragment dans notre boite
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, HomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
>>>>>>> 586ece0 (1.2)


}

