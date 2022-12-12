package cryptography
import java.io.File
import java.awt.image.BufferedImage
import java.awt.Color
import javax.imageio.ImageIO
typealias taskFun = () -> Unit

class SteganographyWorker {
    private lateinit var inputFile: File
    val imageBuffer: BufferedImage
    get() {
            return ImageIO.read(inputFile)
    }
    private lateinit var outputFile: File
    @Throws(Exception::class)
    fun getInputOutputImagesFromStdio(): Pair<File, File> {
        println("Input image file:")
        val input = readln()
        println("Output image file:")
        val output = readln()
        val inputFile = File(input)
        if (!inputFile.exists()) throw Exception("Can't read input file!")
        return Pair(inputFile, File(output))
    }
    fun hideAndSave() {
        println("Input Image: ${inputFile.path.replace(File.separator, "/")}")
        println("Output Image: ${outputFile.path.replace(File.separator, "/")}")

        val tmp = this.imageBuffer
        for (y in 0 until tmp.height) {
            for (x in 0 until tmp.width) {
                // from hint -  return if (pixel % 2 == 0) pixel + 1 else pixel
                val colorXY = Color(tmp.getRGB(x,y))
                val red = colorXY.red or 1
                val green = colorXY.green.or(1) // is no as infix
                val blue = colorXY.blue or 1
                tmp.setRGB(x,y, Color(red, green, blue).rgb)
            }
        }
        ImageIO.write(tmp, "png", outputFile)
        println("Image ${outputFile.name} is saved.")
    }
    constructor() {
        val (inputFile, outputFile) = getInputOutputImagesFromStdio()
        this.inputFile = inputFile
        this.outputFile = outputFile
    }
}

object tasks {
    fun hide() {
        try {
            val worker = SteganographyWorker()
            worker.hideAndSave()
            //println("Hiding message in image.")
        } catch(e: Exception) {
            println(e.toString().split(": ")[1])
        }
    }
    fun show() {
        println("Obtaining message from image.")
    }
    fun exit() {
        println("Bye!")
        kotlin.system.exitProcess(0)
    }
    val supportTaskTextList = listOf("hide", "show", "exit")
    val supportTaskFunList = listOf<taskFun>(::hide, ::show, ::exit)
    fun getTask() {
        println("Task (${supportTaskTextList.joinToString(", ")}):")
        val i = readln()
        for (idx in supportTaskFunList.indices) 
        {
            if (supportTaskTextList[idx] == i) return supportTaskFunList[idx]()
        }
        println("Wrong task: $i")
    }
}

fun main() {
 while(true) {
    tasks.getTask()
 }
