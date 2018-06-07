package utils

import models.AssystTicketModel
import com.github.tototoshi.csv._

object CSVFileWriter {

  def writeCSVFile(values: Seq[AssystTicketModel]) = {

    val writer = CSVWriter.open("assyst.csv", true)

    writer.writeRow(values)
    writer.close
  }

  def readCSVFile(values: Seq[AssystTicketModel]) = {
    val reader = CSVReader.open("assyst.csv")
    val tickets = reader.all()

    println("tickets = " + values.toString + " num = " + values.size)
    val newTickets = for (
      value <- values if (!tickets.toString.contains(value.toString)) | value.priority == (1 | 2)
    ) yield (value)

    println("new = " + newTickets.toString)

    reader.close()
    writeCSVFile(newTickets)
    newTickets
  }
}
