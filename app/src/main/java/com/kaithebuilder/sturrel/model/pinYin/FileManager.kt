package com.kaithebuilder.sturrel.model.pinYin

import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.util.UUID

class FileManager private constructor() {
    companion object {
        val instance: FileManager by lazy {
            FileManager()
        }
    }

    var filesDir: File? = null
    val json = Gson()

    fun <T> read(filename: FileName, decodeType: Class<T>): T {
        val filePath = "${filesDir!!.path}/${filename.path}"
        println("Reading from $filePath")
        val file = File(filePath)

        if (!file.exists()) {
            throw FileNotFoundException("File not found: $filePath")
        }

        val content = file.readText()
        return json.fromJson(content, decodeType)
    }

    fun <T> write(filename: FileName, data: T) {
        val filePath = "${filesDir!!.path}/${filename.path}"
        println("Writing to $filePath")
        val file = File(filePath)

        val text = json.toJson(data)

        file.writeText(text)
    }
}

open class FileName {
    open var path: String = "Invalid"
}
class RootFileName: FileName() {
    override var path: String = "root.json"
}
class ManifestFileName: FileName() {
    override var path: String = "manifest.json"
}
class VocabsFileName: FileName() {
    override var path: String = "vocabs.json"
}
class CustomFolderFileName : FileName() {
    var id: UUID = UUID.randomUUID()

    override var path: String
        get() = "custom/CUSTOM_$id.json"
        set(_) {}
}