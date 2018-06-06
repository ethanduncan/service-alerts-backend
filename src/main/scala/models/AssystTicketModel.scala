package models

case class AssystTicketModel(
  priority: Int,
  status: String,
  name: String,
  time: String) {
  override def toString: String = s"($priority,$status,$name,$time)"
}
