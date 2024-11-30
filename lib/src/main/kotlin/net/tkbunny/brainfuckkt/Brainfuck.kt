package net.tkbunny.brainfuckkt

import java.io.OutputStream
import java.io.InputStream
import java.io.IOException
import kotlin.Byte
import kotlin.byteArrayOf

class BrainfuckKt(val tapeSize: Int = 30000) {
  private val minifyRegex: Regex = "[^\\[\\]\\-\\+.,><]".toRegex()

  private fun readByteFromStream(stream: InputStream): Byte {
    if (stream.available() == 0) {
      return 0.toByte()
    }

    try {
      return stream.read().toByte()
    } catch(e: IOException) {
      println("WARNING: Got an IOException, returning 0")
      return 0.toByte()
    }
  }

  fun runBf(code: String, inp: InputStream = System.`in`): String {
    var out = ArrayList<Byte>()
    val minifiedCode: String = code.replace(minifyRegex, "")

    val loopPtrs: ArrayList<Int> = ArrayList<Int>()

    val tape: ByteArray = ByteArray(tapeSize, { 0.toByte() })
    var tapeIter = 0

    var iter = 0
    while (iter < minifiedCode.length) {
      var increaseIterBy: Int = 1

      when (minifiedCode.get(iter).toString()) {
        "+" -> tape[tapeIter]++
        "-" -> tape[tapeIter]--
        ">" -> tapeIter++
        "<" -> tapeIter--
        "." -> {
          out.add(tape[tapeIter])
        }
        "," -> tape[tapeIter] = readByteFromStream(inp)
        "[" -> {
          if (tape[tapeIter].toInt() == 0) {
            var loopCount = 1

            iter++
            while (loopCount > 0 && iter.toInt() < minifiedCode.length) {
              val char: String = minifiedCode.get(iter).toString()

              if (char == "[") {
                loopCount++
              } else if (char == "]") {
                loopCount--
              }

              iter++
            }

            increaseIterBy = 0
          } else {
            loopPtrs.add(iter)
          }
        }
        "]" -> {
          if (tape[tapeIter].toInt() != 0) {
            iter = loopPtrs.last()
            loopPtrs.removeLast()
            increaseIterBy = 0
          }
        }
      }

      iter += increaseIterBy
    }

    return String(out.toByteArray(), Charsets.ISO_8859_1)
  }
}
