package cryptography

object tasks {
    val supportTaskTextList = listOf("hide", "show", "exit")
    val supportTaskFunList = listOf(::hide, ::show, ::exit)
    fun hide() {
        println("Hiding message in image.")
    }
    fun show() {
        println("Obtaining message from image.")
    }
    fun exit() {
        println("Bye!")
        kotlin.system.exitProcess(0)
    }
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
}

