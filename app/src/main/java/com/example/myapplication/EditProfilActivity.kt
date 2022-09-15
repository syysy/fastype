package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.adapter.LeaderBoardAdapter
import com.example.myapplication.databinding.EditProfilBinding
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.objets.ProfilModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
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

    override fun onBackPressed() {
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfilBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        // set max length for pseudo
        binding.textInputEditpseudo.filters = arrayOf(InputFilter.LengthFilter(13))

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        NavBar(this).navItems(binding.navView)

        val inflater: LayoutInflater = this@EditProfilActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById(R.id.nav_view)
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

        val obj = JSONObject(LeaderBoardAdapter.OpenAsset().loadJsonFromRaw(this))
        val arrayList: ArrayList<String> = ArrayList()

        userModel = ProfilModel().instancierProfil(firebaseAuth.currentUser!!.uid){
            textPseudo.setText(userModel.name)
            name.text = userModel.name
            email.text = userModel.email
            try {
                val obj = JSONObject(LeaderBoardAdapter.OpenAsset().loadJsonFromRaw(this))
                if (userModel.country == "Unknown"){
                    Glide.with(applicationContext).load(Uri.parse(obj[userModel.country].toString())).into(imageCountry)
                } else {
                    ProfilActivity.Utils()
                        .fetchSVG(applicationContext, obj[userModel.country].toString(),imageCountry)
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
            spinnerCountry.setSelection(arrayList.indexOf(userModel.country))


            Glide.with(applicationContext).load(Uri.parse(userModel.imageAvatarUrl)).into(image)
            Glide.with(applicationContext).load(Uri.parse(userModel.imageAvatarUrl)).into(imageProfil)

            val editRessources = EditRessources(this)
            var deviceLanguage : String
            try {
                deviceLanguage = editRessources.loadEditableJsonFile("app_config.json")["language"].toString()
                LocaleHelper.setLocale(this, deviceLanguage)
            } catch (e: JSONException) {
                editRessources.writeJsonFile("app_config.json", JSONObject().put("language", "en"))
                deviceLanguage = "en"
            }
            when(deviceLanguage){
                "fr" ->
                {   textRank.text = "Rang : ${MainActivity().getRank(userModel.uid)}"
                    textMoyenne.text = "Moyenne : " + userModel.moyenne.toString()
                    textNbGameJouees.text = "Nombre de parties jouées : " + userModel.numberGamePlayed.toString()
                    textCompteCreationDate.text = "Compte créé le : " + userModel.date
                    textBestGame.text = "Meilleur score : " + userModel.bestGame.toString()
                }
                else ->
                {
                    textRank.text = "Rank : " + MainActivity().getRank(userModel.uid)
                    textMoyenne.text = "Mean : " + userModel.moyenne.toString()
                    textNbGameJouees.text = "Games played : " + userModel.numberGamePlayed.toString()
                    textCompteCreationDate.text = "Account creation date : " + userModel.date
                    textBestGame.text = "Best game : " + userModel.bestGame.toString()
                }
            }

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


        binding.imageProfil.setOnClickListener { pickImage() }


        binding.Save.setOnClickListener {
            // visibilty on de la progress bar
            binding.progressBar.visibility = View.VISIBLE

            if (file != null  && file.toString() != userModel.imageAvatarUrl) {
                uploadImage(file!!){
                    val newCountry = spinnerCountry.selectedItem.toString()
                    if (textPseudo.text.toString() != userModel.name){
                        databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").setValue(textPseudo.text.toString())
                        userModel.name = textPseudo.text.toString()
                    }
                    if (newCountry != userModel.country){
                        databaseRef.child(firebaseAuth.currentUser!!.uid).child("country").setValue(newCountry)
                        userModel.country = newCountry
                    }
                    startActivity(Intent(this,ProfilActivity::class.java))
                    finish()
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
                finish()
            }
        }

        binding.Cancel.setOnClickListener {
            startActivity(Intent(this,ProfilActivity::class.java))
            finish()
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
