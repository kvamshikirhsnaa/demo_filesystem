package com.rtjvm.scala.oop.filesystem

import java.util.Scanner

import com.rtjvm.scala.oop.commands.Command
import com.rtjvm.scala.oop.files.Directory

object FileSystem {
  def main(args: Array[String]): Unit = {

    val root = Directory.ROOT
    var state = State(root, root)

    val scanner = new Scanner(System.in)

    while(true) {
      state.show
      val input = scanner.nextLine()
      state = Command.from(input).apply(state)

    }
  }

}
