package ru.aaaaadfdsfsdf.numer.notifylisten

import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.io.*
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class CallLosService : NotificationListenerService() {


    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("df", " my Notifications ! ")


        // write to sdCard
        matchNotificationCode(sbn)
        //if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
        val intent = Intent("ru.aaaaadfdsfsdf.numer.notifylisten")
        intent.putExtra("Notification Code", 1001)
        sendBroadcast(intent)
        //}
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        matchNotificationCode(sbn)
        Log.d("df", " my Notifications removed ")
        //if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
        val activeNotifications = this.activeNotifications
        if (activeNotifications != null && activeNotifications.isNotEmpty()) {
            for (i in activeNotifications.indices) {
                // if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                val intent = Intent("ru.aaaaadfdsfsdf.numer.notifylisten")
                intent.putExtra("Notification Code", 1001)
                sendBroadcast(intent)
                break

            }
        }

    }

    private fun matchNotificationCode(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        Log.d("df", " my Notifications in this !  " + sbn.packageName)
        val title = sbn.notification.extras.getString("android.title")
        val text = sbn.notification.extras.getString("android.text")
        //sbn.getNotification().extras.
        Log.d("df", " my title app's == $title")
        Log.d("df", " my text app's == $text")

        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm")
        val resultdate = Date(sbn.postTime)
        val textTime =  sdf.format(resultdate).toString()
        Log.d("df", " my textTime app's == $textTime")

        logWrite2(title, text, packageName, textTime)


    }


    private fun logWrite2(tit: String?, text: String?, packaName: String?, timeOut: String?){

        //val path : File? = application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val path : File? = application.cacheDir
        val foldermain  = "losWriteTestApps"


        println(" my path = $path")
        val f = File(application.cacheDir, foldermain)

        if(!f.exists()){
            f.mkdir()
        }
        println( " my f = ${f.toString()}")
        val file = File(f, "loaded11.txt")


            // /data/user/0/ru.aaaaadfdsfsdf.numer.notifylisten/cache/losWriteTestApps
//
//        try {
//            //
//            val outputStreamWriter  = OutputStreamWriter(file.outputStream(), Charset.defaultCharset())
//
//            //val bufferWri = BufferedWriter(file.outputStream(), Charset.defaultCharset())
//            // for (item in arraylist){
//
//            val item = "Title my messages"
//            outputStreamWriter.write(tit + "\n")
//            outputStreamWriter.write(text + "\n")
//            outputStreamWriter.write(packaName + "\n")
//            outputStreamWriter.write(timeOut + "\n")
//            // }
//
//            outputStreamWriter.close();
//        }
//        catch (e : IOException) {
//            println("Exception File write failed:  ${e.toString()} ");
//        }


        val fos: FileOutputStream

        try {
            val f = File(application.cacheDir, foldermain)
            fos = FileOutputStream( File (f , "loaded11.txt")  ,  true)
            val fWriter: FileWriter
            try {
                fWriter = FileWriter(fos.fd)
                fWriter.write(tit + "\n")
                fWriter.write(text + "\n")
                fWriter.write(packaName + "\n")
                fWriter.write(timeOut + "\n\n")
                fWriter.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos.fd.sync()
                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}