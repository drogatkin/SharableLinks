package rogatkin.mobile.app.mylinks.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import rogatkin.mobile.data.pertusin.DataAssistant
import rogatkin.mobile.data.pertusin.UIAssistant

class Model(ctx: Context) : SQLiteOpenHelper(ctx ,"links.db", null, 1) {
    val vc: UIAssistant by lazy { UIAssistant(ctx) }
    val helper = DataAssistant(ctx)
    val TAG = Model::class.java.name

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Creating: " + "db");

        db!!.execSQL(helper.getCreateQuery(group::class.java))
        db.execSQL(helper.getCreateQuery(line::class.java))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
       // TODO("Add version upgrading logi c")
        db!!.execSQL(helper.getDropQuery(group::class.java))
        db.execSQL(helper.getDropQuery(line::class.java))
        onCreate(db)
    }

    fun save(r: Id) {
        val db = writableDatabase
        val name = helper.resolveStoreName(r.javaClass)
        try {
            if (r.id > 0) {
                db.update(name, helper.asContentValues(r, false, "id"), "_id="
                        + r.id, null)
            } else {
                r.id = db.insert(name, null,
                        helper.asContentValues(r, false, "id"))
            }
        } finally {
            db.close()
        }
    }

    fun <T> load(filter: ContentValues?, pojo: Class<T>?, order: String?,
                 vararg fields: String?): ArrayList<T>? {
        val database = this.writableDatabase
        return try {
            ArrayList(helper.select(database, pojo, filter,
                    order, null, false, *fields))
        } catch (npe: java.lang.NullPointerException) {
            ArrayList()
        } finally {
            database.close()
        }
    }
}