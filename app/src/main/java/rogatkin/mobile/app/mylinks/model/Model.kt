package rogatkin.mobile.app.mylinks.model

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import rogatkin.mobile.data.pertusin.DataAssistant
import rogatkin.mobile.data.pertusin.UIAssistant
import rogatkin.mobile.data.pertusin.WebAssistant

class Model(ctx: Context) : SQLiteOpenHelper(ctx, "links.db", null, 1) {
    val vc: UIAssistant by lazy { UIAssistant(ctx) }
    val helper = DataAssistant(ctx)
    val web : WebAssistant by lazy { WebAssistant(ctx) }
    val TAG = Model::class.java.name

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Creating: " + "db")

        db!!.execSQL(helper.getCreateQuery(group::class.java))
        db.execSQL(helper.getCreateQuery(line::class.java))
        db.execSQL("insert into group_tb (name, created_on, modified_on) values('All links', TIME('now'), TIME('now'))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // TODO("Add version upgrading logi c")
        db!!.execSQL(helper.getDropQuery(group::class.java))
        db.execSQL(helper.getDropQuery(line::class.java))
        onCreate(db)
    }

    fun save(r: Id, vararg extras: String) {
        val db = writableDatabase
        val name = helper.resolveStoreName(r.javaClass)
        try {
            if (r.id > 0) {
                db.update(
                    name, helper.asContentValues(
                        r, false, *merge(
                            arrayOf("id", "created_on"),
                            extras as Array<String>
                        )
                    ), "_id=" +
                            r.id, null
                )
            } else {
                // TODO update or insert for a table with global_id
                r.id = db.replaceOrThrow(
                    name, null,
                    helper.asContentValues(r, false,  "id", *extras)
                )
            }
        } catch(se: SQLException) {
            Log.e(TAG, "An error in saving: $se", se)
        } finally {
            db.close()
        }
    }

    fun validate(data: line) : Boolean {
        when {
            data.group_id < 1 -> data.group_id = 1
            data.name.isBlank() -> return false
            data.url.isBlank() || !data.url.startsWith("http") -> return false
        }
        return true
    }

    fun <T> load(
        filter: ContentValues?, pojo: Class<T>?, order: String?,
        vararg fields: String?
    ): ArrayList<T>? {
        val database = this.writableDatabase
        return try {
            ArrayList(
                helper.select(
                    database, pojo, filter,
                    order, null, false, *fields
                )
            )
        } catch (npe: java.lang.NullPointerException) {
            ArrayList()
        } finally {
            database.close()
        }
    }

    fun <T> load(filter: ContentValues?, pojo: T,
                 vararg fields: String?): T? {
        val database = this.writableDatabase
        return try {
            helper.select(database, pojo, filter, false, *fields)
        } catch (npe: NullPointerException) {
            if (true) Log.d(TAG, "Load", npe)
            null
        } finally {
            database.close()
        }
    }

    fun <T> whereVals(pojo: T, vararg fields: String?): ContentValues {
        return helper.asContentValues(pojo, true, *fields)
    }

    fun remove(r: Id): Int {
        val database = this.writableDatabase
        return database.use {
            it.delete(
                helper.resolveStoreName(r.javaClass),
                "_id=?", arrayOf("" + r.id)
            )
        }
    }

    fun removeGroup(gr:group) :Int{
        val database = this.writableDatabase
        return database.use {
            it.delete("group_tb", "_id=? and NOT EXISTS (\n" +
                    "    SELECT *\n" +
                    "    FROM line\n" +
                    "    WHERE group_id = group_tb._id)", arrayOf(""+gr.id))
        }
    }

    fun <T> asList(vararg input: T): List<T> {
        val result = ArrayList<T>()
        for (item in input) // input is an Array
            result.add(item)
        return result
    }

    fun merge(arr: Array<String>, elements: Array<String>): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.addAll(elements)
        return list.toTypedArray()
    }
}