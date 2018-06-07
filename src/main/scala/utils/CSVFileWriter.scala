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

    val newTickets = for (
      value <- values if (!tickets.toString.contains(value.toString))
    ) yield (value)

    println("\n  new = " + newTickets)
    reader.close()

    writeCSVFile(newTickets)
    newTickets
  }
}
