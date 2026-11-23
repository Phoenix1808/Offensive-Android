package com.example.offensiveandroid.ui.theme

import java.io.BufferedReader
import java.io.InputStreamReader

class ShellCmdExe {

    //executes shell cmd provided as string
    fun execute(command: String):String{
        return try {
            val cmdParts = command.split(" ")

            //this is java Utility which allows to create & manage system processes
            val processBuilder = ProcessBuilder(cmdParts)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.readText()
            }
            process.waitFor() //wait for process ot complete
            output
        } catch (e:Exception){
            "Error executing command: ${e.message}"
        }
    }
}


//The process.inputStream provides the combined output (stdout and stderr) of the process
// which is then passed to an InputStreamReader to convert the raw InputStream into a character stream
// A BufferedReader is used to buffer this character stream for efficient reading, and the .use {} block ensures that the BufferedReader is properly closed after use to prevent resource leaks
// Finally, reader.readText() reads the entire output of the process as a String, making it easy to access and handle the command's result.