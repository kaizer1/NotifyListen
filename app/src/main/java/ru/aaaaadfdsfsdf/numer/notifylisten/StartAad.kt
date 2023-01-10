package ru.aaaaadfdsfsdf.numer.notifylisten

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isEmpty
import java.io.*

class StartAad : AppCompatActivity() {

    private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    private val ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    private var imageChangeBroadcastReceiver: ImageChangeBroadcastReceiver? = null
    private var enableNotificationListenerAlertDialog: AlertDialog? = null
    lateinit var lView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listlayout)

        lView = findViewById(R.id.list_vie)

        imageChangeBroadcastReceiver = ImageChangeBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("ru.aaaaadfdsfsdf.numer.notifylisten")
        registerReceiver(imageChangeBroadcastReceiver, intentFilter)

        hideSystemUI()

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            println(" ok GRANTED ! ")


          drawLosList()

        }else {
            launchMultiPermission()
        }


        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog!!.show()
        }
    }


    private fun drawLosList() {

        val userList = getUsers()

        if (userList!!.isEmpty()) {
            println(" users is empty ! ")
        } else {
            lView.adapter = null
            val adapter: ListAdapter = SimpleAdapter(
                this,
                userList,
                R.layout.list_r,
                arrayOf("title", "text", "package", "time"),
                intArrayOf(R.id.title, R.id.text, R.id.packages, R.id.time)
            )
            lView.adapter = adapter
        }
    }

    private fun changeInterceptedNotificationImage(notificationCode: Int) {


        drawLosList()

    }

    private fun launchMultiPermission() {
        requestMultiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

            )
        )
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")

            if(it.key == android.Manifest.permission.READ_EXTERNAL_STORAGE && it.value){

                val userList  = getUsers()

                if(userList!!.isEmpty()){
                    println(" users is empty ! ")
                }else {
                    val adapter: ListAdapter = SimpleAdapter(
                        this,
                        userList,
                        R.layout.list_r,
                        arrayOf("title", "text", "package", "time"),
                        intArrayOf(R.id.title, R.id.text, R.id.packages, R.id.time))
                    lView.adapter = adapter
                }
            }
        }
    }



    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            ENABLED_NOTIFICATION_LISTENERS
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(imageChangeBroadcastReceiver)
    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    fun getUsers(): ArrayList<HashMap<String, String>>? {

        val userList = ArrayList<HashMap<String, String>>()
        val `is`: FileInputStream
        val reader: BufferedReader

        val f = File(application.cacheDir, "losWriteTestApps")
        val file = File(f,
            "loaded11.txt"
        )

        var  title : String? = ""
        var text : String? = ""
        var packages : String? = ""
        var times : String? = ""

        var values: Int = 1
        if (file.exists()) {
            `is` = FileInputStream(file)
            reader = BufferedReader(InputStreamReader(`is`))
            var line = reader.readLine()
            title = line
            while (line != null) {
                line = reader.readLine()

                if(values == 4){

                    val user = HashMap<String, String>()
                    user["title"] = title!!
                    user["text"] = text!!
                    user["package"] = packages!!
                    user["time"] = times!!
                    userList.add(user)


                        //println(" my messages = $packages")
                    title = ""
                    text = ""
                    packages = ""
                    times = ""
                    values = 0
                }else{

                    when(values){
                        0 -> {  title = line }
                        1 -> { text = line }  // println("Text $line ")
                        2 ->  packages = line  // println("Package $line")
                        3 ->  times = line //  println("Time $line")
                    }
                    values++
                }
            }
        }
        return userList
    }

    inner class ImageChangeBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            println(" my get Receive is ")



            val receivedNotificationCode = intent.getIntExtra("Notification Code", -1)
                changeInterceptedNotificationImage(receivedNotificationCode)
        }
    }

    override fun onResume() {
        super.onResume()


        println("onResume")

        val userList  = getUsers()

        if(userList!!.isEmpty()){
            println(" users is empty in on Resumr ! ")
        }else {
            lView.adapter = null;

            val adapter: ListAdapter = SimpleAdapter(
                this,
                userList,
                R.layout.list_r,
                arrayOf("title", "text", "package", "time"),
                intArrayOf(R.id.title, R.id.text, R.id.packages, R.id.time))
            lView.adapter = adapter
        }
    }


    private fun buildNotificationServiceAlertDialog(): AlertDialog? {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialog, id ->
                startActivity(
                    Intent(
                        ACTION_NOTIFICATION_LISTENER_SETTINGS
                    )
                )
            })
        alertDialogBuilder.setNegativeButton(R.string.no,
            DialogInterface.OnClickListener { dialog, id ->
            })
        return alertDialogBuilder.create()
    }
}