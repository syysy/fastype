package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.EditProfilBinding
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.objets.ProfilModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.nio.charset.Charset
import java.util.*

class EditProfilActivity : AppCompatActivity() {

    lateinit var binding: EditProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var storageRef : StorageReference
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var headerLayout : HeaderLayoutBinding
    private lateinit var userModel : ProfilModel
    private var file : Uri? = null
    // header changements

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inflater: LayoutInflater = this@EditProfilActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById (R.id.nav_view)
        val view = inflater.inflate(R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(R.id.text_username)
        val email : TextView = view.findViewById(R.id.text_user_mail)
        val image : ImageView = view.findViewById(R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)
        // Changement de l'image du profil
        userModel = ProfilModel("","",0.0,0,"")
        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://fastype-c5cc4.appspot.com")

        val userDate = firebaseAuth.currentUser
        val date = Date(userDate!!.metadata!!.creationTimestamp)
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = formatter.format(date)

        val imageProfil : ImageView = findViewById(R.id.imageProfil)
        val textRank : TextView = findViewById(R.id.text_rank)
        val textMoyenne : TextView = findViewById(R.id.textMoyenne)
        val textPseudo : TextInputEditText = findViewById(R.id.textInput_Editpseudo)
        val textNbGameJouees : TextView = findViewById(R.id.textNbGameJouees)
        val textCompteCreationDate : TextView = findViewById(R.id.textCompteCreationDate)
        val textBestGame : TextView = findViewById(R.id.text_bestScore)
        val imageCountry : ImageView = findViewById(R.id.imageCountry)
        val spinnerCountry : Spinner = findViewById(R.id.spinnerCountry)

        databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").get().addOnSuccessListener {
            userModel.name = it.value.toString()
            textPseudo.setText(it.value.toString())
            name.text = it.value.toString()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("email").get().addOnSuccessListener {
            userModel.email = it.value.toString()
            email.text = it.value.toString()
        }

        val obj = JSONObject(loadJSONFromAsset())
        val arrayList: ArrayList<String> = ArrayList()
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("country").get().addOnSuccessListener {

            userModel.country = it.value.toString()
            try {
                if (it.value.toString() == "Unknown"){
                    Glide.with(binding.root).load(Uri.parse(obj[it.value.toString()].toString())).into(imageCountry)
                } else {
                    ProfilActivity.Utils().fetchSVG(binding.root.context, obj[it.value.toString()].toString(),imageCountry)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            for (key in obj.keys()) {
                arrayList.add(key.toString())
            }
            val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCountry.setAdapter(arrayAdapter)
            spinnerCountry.setSelection(arrayList.indexOf(it.value.toString()))
        }

        spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                try {
                    if (arrayList[position] == "Unknown"){
                        Glide.with(binding.root).load(Uri.parse(obj[arrayList[position]].toString())).into(imageCountry)
                    } else {
                        ProfilActivity.Utils().fetchSVG(binding.root.context, obj[arrayList[position]].toString(),imageCountry)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }


        databaseRef.child(firebaseAuth.currentUser!!.uid).child("moyenne").get().addOnSuccessListener {
            userModel.moyenne = it.value.toString().toDouble()
            textMoyenne.text = "Mean : " + userModel.moyenne.toString()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("imageAvatarUrl").get().addOnSuccessListener {
            userModel.imageAvatarUrl = it.value.toString()
            Glide.with(headerLayout.root).load(Uri.parse(it.value.toString())).into(image)
            Glide.with(binding.root).load(Uri.parse(it.value.toString())).into(imageProfil)
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("bestGame").get().addOnSuccessListener {
            userModel.bestGame = it.value.toString().toInt()
            textBestGame.text = "Best score : " + it.value.toString().toInt()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("numberGamePlayed").get().addOnSuccessListener {
            userModel.numberGamePlayed = it.value.toString().toInt()
            textNbGameJouees.text = "Game Played : " + userModel.numberGamePlayed.toString()
        }
        textRank.text = "Rank : " + MainActivity().getRank()

        textCompteCreationDate.text = "Account created on : $formattedDate"

        // toggle menu

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_profil -> startActivity(Intent(this,ProfilActivity::class.java))
                R.id.item_leaderboard -> StatsRepository().updateDate { startActivity(Intent(this,LeaderBoardActivity::class.java)) }
                R.id.item_home -> StatsRepository().updateDate {  startActivity(Intent(this,MainActivity::class.java)) }
                R.id.item_logout -> MainActivity().dialog()
                //R.id.item_settings ->
                R.id.item_rate -> startActivity(Intent(this,WaitingActivity::class.java))
                //R.id.item_share ->
            }
            true
        }

        val mAdViewTop : AdView = binding.adViewTop
        val adRequestTop: AdRequest = AdRequest.Builder().build()
        mAdViewTop.loadAd(adRequestTop)

        binding.imageProfil.setOnClickListener { pickImage() }

        binding.Save.setOnClickListener {
            if (file != null  && file.toString() != userModel.imageAvatarUrl) {
                uploadImage(file!!){

                    val newCountry = spinnerCountry.selectedItem.toString()
                    val country = userModel.country
                    if (textPseudo.text.toString() != userModel.name){
                        databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").setValue(textPseudo.text.toString())
                        userModel.name = textPseudo.text.toString()
                    }
                    if (newCountry != userModel.country){
                        databaseRef.child(firebaseAuth.currentUser!!.uid).child("country").setValue(newCountry)
                        userModel.country = newCountry
                    }
                    startActivity(Intent(this,ProfilActivity::class.java))
                }
            }else{
                if (textPseudo.text.toString() != userModel.name){
                    databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").setValue(textPseudo.text.toString())
                    userModel.name = textPseudo.text.toString()
                }
                if (spinnerCountry.selectedItem.toString() != userModel.country){
                    databaseRef.child(firebaseAuth.currentUser!!.uid).child("country").setValue(spinnerCountry.selectedItem.toString())
                }
                startActivity(Intent(this,ProfilActivity::class.java))
            }
        }


    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11)
    }
    private fun uploadImage(file : Uri,callback : () -> Unit = {}) {
        val ref = storageRef.child(firebaseAuth.currentUser!!.uid)
        val uploadTask = ref.putFile(file)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
            ref.downloadUrl.addOnSuccessListener {
                val uploadedImage = it.toString()
                databaseRef.child(firebaseAuth.currentUser!!.uid).child("imageAvatarUrl").setValue(uploadedImage)
                callback()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            file = data.data!!
            binding.imageProfil.setImageURI(file)
            Glide.with(binding.root).load(Uri.parse(file.toString())).into(binding.imageProfil)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    open fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val inputStream = assets.open("country.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            val charset: Charset = Charsets.UTF_8
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, charset)
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }
    private fun <T> iterate(i: Iterator<T>): Iterable<T>? {
        return object : Iterable<T> {
            override fun iterator(): Iterator<T> {
                return i
            }
        }
    }
}
