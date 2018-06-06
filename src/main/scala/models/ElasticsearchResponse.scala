package models

case class ElasticsearchResponse(
  hits: Seq[ElasticsearchJsonModel])

