package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

class Mkdir(val name: String) extends Command {

  override def apply(state: State): State = {
    val wd: Directory = state.wd
    if (wd.hasEntry(name)) {
      state.setMessage(s"Entry $name already exists")
    } else if (name.contains(Directory.SEPARATOR)) state.setMessage(s"$name shouldn't contain separators")
    else if (checkIllegal(name)) {
      state.setMessage(s"$name: illegal entry name!")
    } else doMkdir(state, name)
  }

  def checkIllegal(name: String): Boolean = name.contains(".")

  def doMkdir(state: State, name: String): State = {
    def updateStructure(currDir: Directory, path: List[String], newEntry: DirEntry): Directory = {
      /*
       Somedir: in side folders a and b
        /a , /b
         now trying to add new dir "d" inside Somedir
        result will be => new Somedir : inside folders /a , /b, /d
        here new Somedir will be new instance and we use old a and b instances
        as it is, cuz inside a and b there might be some folders so

       if i have nested path
       i have  /a/b  here parent is a
                 /c
                 /d   want to add new folder /g

          new /a
           new /b ( parent /a)
           /c  reused old instance
           /d  reused old instance
           /g (newly added)

       */

      if (path.isEmpty) currDir.addEntry(newEntry)
      else {
        /*
          /a/b
            /c
            /d
            add newEntry
          currDir = /a
          path = List(b)
          newEntry = /g

         */
        println(path)
        println(path.head)
        println(path.head.isEmpty)
        println(currDir.findEntry(path.head))
        val oldEntry = currDir.findEntry(path.head).asDirectory
        currDir.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail,newEntry))

        /*
           /a/b
            (contents)
            (new entry) g

            updateStructure(root, List(a,b), /g) = root.replaceEntry(/a, updateStructure(/a, List(b), /g) = /a.replaceEntry(/b, updateStructure(/b, List(), /g) == /b.addEntry(/g) )
             => path.isEmpty =>false
             => oldEntry = /a (with all it's contents)
                root.replaceEntry(/a, updateStructure(/a, List(b), /g) = /a.replaceEntry(/b, updateStructure(/b, List(), /g) == /b.addEntry(/g) )
                    => path.isEmpty => false
                    => oldEntry = /b (with all it's content)
                    => /a.replaceEntry(/b, updateStructure(/b, List(), /g) == /b.addEntry(/g) result will be called here recursion)
                       => path.isEmpty => true
                       => /b.addEntry(/g)


         */
      }


    }


    val wd = state.wd

    // 1. all the directories in the full path
    val allDirsInPath = wd.getAllFoldersInPath

    // 2. create new directory in working directory
    val newDir: Directory = Directory.empty(wd.path, name)

    // 3. update the whole directory structure starting from the root(the directory structure is immutable)
    val newRoot = updateStructure(state.root, allDirsInPath, newDir)


    // 4. find new working directory INSTANCE given wd's full path, in the NEW directory structure
    val newWd = newRoot.findDescendant(allDirsInPath)

    State(newRoot, newWd)



  }




}
