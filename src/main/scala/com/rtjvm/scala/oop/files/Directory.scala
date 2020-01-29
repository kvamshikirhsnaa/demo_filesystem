package com.rtjvm.scala.oop.files

import scala.annotation.tailrec

class Directory(override val parentPath: String, override val name:String, val contents: List[DirEntry])
  extends DirEntry(parentPath, name) {

  def asDirectory: Directory = this

  def getType: String = "Directory"

  def hasEntry(name: String): Boolean = {
    findEntry(name) != null
  }
  def getAllFoldersInPath: List[String] = {
    // path => "/a/b/c/d"
    // path.substring(1) => "a/b/c/d".split(Directory.SEPARATOR).toList => List(a,b,c,d)
    path.substring(1).split(Directory.SEPARATOR).toList.filter(x => x.nonEmpty)
  }

  def findDescendant(path: List[String]): Directory = {
    if (path.isEmpty) this
    else findEntry(path.head).asDirectory.findDescendant(path.tail)
  }

  def addEntry(newEntry: DirEntry): Directory = {
    new Directory(parentPath, name, contents :+ newEntry)
  }


  def findEntry(entryName: String): DirEntry = {
    @tailrec
    def findEntryRec(name: String, contentList: List[DirEntry]): DirEntry = {
      if (contentList.isEmpty) null
      else if (contentList.head.name.equals(entryName)) contentList.head
      else findEntryRec(name, contentList.tail)
    }
    findEntryRec(entryName, contents)
  }

  def replaceEntry(entryName: String, newEntry: DirEntry): Directory = {
    new Directory(parentPath, name, contents.filter(x => (x != name)) :+ newEntry)
  }


}

object Directory {
  val SEPARATOR = "/"
  val ROOT_PATH = "/"

  def empty(parentPath: String, name: String): Directory = new Directory(parentPath, name, List())
  def ROOT: Directory = Directory.empty("", "")



}