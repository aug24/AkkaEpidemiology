object ThreadGroupUtils {

  def getThreadGroup: ThreadGroup = Thread.currentThread.getThreadGroup

  def findAncestor(current: ThreadGroup)(isTarget: ThreadGroup => Boolean): Option[ThreadGroup] = {
    Option(current.getParent) match {
      case None =>
        None
      case opt @ Some(parent) if isTarget(parent) =>
        opt
      case Some(parent) =>
        findAncestor(parent)(isTarget)
    }
  }

  def root = findAncestor(getThreadGroup)(_.getParent == null).get

  def main = findAncestor(getThreadGroup)(_.getName == "main").get

  def getSubGroups(parent: ThreadGroup): Seq[ThreadGroup] = {
    val subgroups = new Array[ThreadGroup](parent.activeGroupCount)
    val copied = parent.enumerate(subgroups)
    subgroups.iterator.take(copied).toSeq
  }

  def printThreadGroups(group: ThreadGroup = root, indent: String = ""): Unit = {
    val subgroups =getSubGroups(group)
    println(s"${indent}${group.getName}: ${subgroups.size} subgroups, ${group.activeCount} threads")
    subgroups.foreach(printThreadGroups(_, indent + "    "))
  }
}

