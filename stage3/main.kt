package cryptography
import java.io.File
import java.awt.image.BufferedImage
import java.awt.Color
import java.util.BitSet
import javax.imageio.ImageIO
//import java.nio.file.Paths

typealias taskFun = () -> Unit

class SteganographyWorker {
    private val ends = buildString {
        append('\u0000') // markers
        append('\u0000')
        append('\u0003')
    }
    private val endsInBit = "000000000000000000000011"
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
    private fun show(file: File) {
        val tmp = ImageIO.read(file)
        val msgAllBits = mutableListOf<Int>()
        for (y in 0 until tmp.height) {
            for (x in 0 until tmp.width) {
                val pixelColor = Color(tmp.getRGB(x,y))
                // pixelColor.blue to binary. after found there last bit. toInt it bit
                msgAllBits += (pixelColor.blue).toString(2).takeLast(1).toInt()
                //TODO("Show method")
            }
        }
        println("Message:")
        // joinToString from list of ints 000101010101 after get first element. chunk by 8 size. after print
        msgAllBits.joinToString("").
        split(endsInBit).first().
        chunked(8).forEach { print(it.toInt(2).toChar()) }
        println()
    }
    @Throws(Exception::class)
    private fun show(filePath: String) {
        val f = File(filePath)
        if (f.exists() == false) throw Exception("Can't read input file!")
        return show(f)
    }
    fun show() {
        println("Input image file:")
        return show(readln())
    }
    // maybe exists some like bitset in java? maybe better to use it? idk
    private fun getEncodeMessageBits(msg: String): List<Int> {
        val msgWithEnds = (msg + ends)
        val binaryData = msgWithEnds.map( { it.code/*.shl(1)*/.toString(2) } )
        var ret = mutableListOf<Int>()
        for (block in binaryData)
        {
            var correctBlock = if (block.length == 8) block else {
                val padSize = 8 - block.length
                "0".repeat(padSize) + block
            }
            //println(correctBlock)
            for (ch in correctBlock) {
                if(ch == '1') ret.add(1)
                else ret.add(0)
            }
        }
        return ret
    }
    fun hideAndSave(msg: String? = null) {
        //println("Input Image: ${inputFile.path.replace(File.separator, "/")}")
        //println("Output Image: ${outputFile.path.replace(File.separator, "/")}")
        val encodedBits = if (msg != null) getEncodeMessageBits(msg) else {
            println("Message to hide:")
            getEncodeMessageBits(readln())
        }
        var idx = 0;
        val input = this.imageBuffer
        val output = BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB)
        if (input.width * input.height < encodedBits.size) throw Exception("The input image is not large enough to hold this message.")
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                // from hint -  return if (pixel % 2 == 0) pixel +q 1 else pixel
                val colorXY = Color(input.getRGB(x,y))
                val r = colorXY.red //or 1
                val g = colorXY.green //.or(1) // is no as infix
                val b = colorXY.blue //or 1

...., [12.12.2022 16:34]


                if (idx >= encodedBits.size) {
                    output.setRGB(x,y, Color(r, g, b).rgb)
                } else {
                    val bit = encodedBits[idx] //if () 1 else 0
                    // 254 = 11111110
                    // or 1 or 0.
                    output.setRGB(x, y, Color(r, g, b.and(254).or(bit)).rgb)
                    idx++
                }

            }
        }
        ImageIO.write(output, "png", outputFile)
        println("Message saved in ${outputFile.name} image.")
    }
    constructor() {
        val (inputFile, outputFile) = getInputOutputImagesFromStdio()
        this.inputFile = inputFile
        this.outputFile = outputFile
    }
}

object tasks {
    var worker: SteganographyWorker? = null
    fun hide() {
        try {
            if (worker == null) worker = SteganographyWorker()
            worker?.hideAndSave()
            //println("Hiding message in image.")
        } catch(e: Exception) {
            println(e.toString().split(": ")[1])
        }
    }
    fun show() {
        if (worker == null) worker = SteganographyWorker()
        worker!!.show()
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
    //val path = Paths.get("").toAbsolutePath().toString()
    //println(path)


    while(true) {
        tasks.getTask()
    }
}
