package KPI

import org.apache.hadoop.fs._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, explode, split, sum}

object KPICalculator {
  def main(args: Array[String]): Unit = {

    //Utilidad: Cambiar el log para que solo muestre errores y no saque tantas trazas
    val logger = Logger.getLogger("org.apache.spark")
    logger.setLevel(Level.ERROR)

    // 1. Crear el SparkSession
    val spark = SparkSession.builder()
      .appName("bicimad")
      .getOrCreate()
    // 2. Creamos DataFrame
    val jsonTweets = spark.read.format("json")
      .load("/user/bferrol/bicicletas/flume/kpi/*")
    val stations = jsonTweets.withColumn("Estaciones", explode(col("stations")))
    val biciDF = stations.select("Estaciones")
    val biciDF2 = biciDF.select("Estaciones.name","Estaciones.address", "Estaciones.dock_bikes","Estaciones.free_bases",
      "Estaciones.id", "Estaciones.latitude", "Estaciones.longitude", "Estaciones.light",
    "Estaciones.no_available","Estaciones.number", "Estaciones.reservations_count",
    "Estaciones.total_bases", "Estaciones.activate")

    // 3. Creamos KPI
    val tempView = biciDF2.createOrReplaceTempView("bicimad")
    val kpiQuery = spark.sql(
      """
select
 	count(distinct id) as numBases,
 	(sum(boSinUsar)/count(distinct id))*100 as PorcEstacionesSinUsar,
 	(sum(boNotAvaiable)/count(distinct id))*100 as PorcEstacionesSinBicicletasLibres,
 	sum(free_bases) as BicicletasSiendoUtilizadas,
 	sum(reservations_count) as ReservasProgramadas,
 	--sum(select distinct id from bicimad where no_available=1) as BasesNoDisponibles,
 	sum(free_bases)/count(distinct id) as PromedioBasesLibres,
 	sum(dock_bikes)/count(distinct id) as PromedioBasesAncladas

from
 	(
 	select
 		id,
 		name,
 		case
 			when total_bases=dock_bikes
 			then 1 else 0 end as boSinUsar,
 		case
 			when free_bases=0
 			then 1 else 0 end as boNotAvaiable,
 		total_bases,
 		case
 			when no_available=1 then 0
 			else total_bases end as total_bases_available,
 		reservations_count,
 		latitude,
 		longitude,
 		light,
 		activate,
    free_bases,
    dock_bikes
from bicimad
 	)a
        """).toDF()

    val stationsQuery = spark.sql(
      """
select
  id,
  name,
  latitude,
  longitude,
 	boSinUsar,
 	boNotAvaiable,
 	free_bases as BicicletasSiendoUtilizadas,
  dock_bikes as BicicletasAncladas,
 	reservations_count as ReservasProgramadas,
  ((free_bases + reservations_count)/total_bases)*100 as UseLevel

from
 	(
 	select
 		id,
 		name,
 		case
 			when total_bases=dock_bikes
 			then 1 else 0 end as boSinUsar,
 		case
 			when free_bases=0
 			then 1 else 0 end as boNotAvaiable,
 		total_bases,
 		case
 			when no_available=1 then 0
 			else total_bases end as total_bases_available,
 		reservations_count,
 		latitude,
 		longitude,
 		light,
 		activate,
    free_bases,
    dock_bikes
from bicimad
 	)a
        """).toDF()

    kpiQuery.write.format("csv")
      .option("sep", "#")
      .mode("overwrite")
      .option("header", false)
      .option("path", "/user/bferrol/bicicletas/kpi")
      .save()
    stationsQuery.write.format("csv")
      .option("sep", "#")
      .mode("overwrite")
      .option("header", false)
      .option("path", "/user/bferrol/bicicletas/stations")
      .save()

    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    fs.delete(new Path("/user/bferrol/bicicletas/flume/kpi/*"), true)

  }
}
/*

activate: long (nullable = true)
|    |-- address: string (nullable = true)
|    |-- dock_bikes: long (nullable = true)
|    |-- free_bases: long (nullable = true)
|    |-- id: long (nullable = true)
|    |-- latitude: string (nullable = true)
|    |-- light: long (nullable = true)
|    |-- longitude: string (nullable = true)
|    |-- name: string (nullable = true)
|    |-- no_available: long (nullable = true)
|    |-- number: string (nullable = true)
|    |-- reservations_count: long (nullable = true)
|    |-- total_bases: long (nullable = true)
INSERT INTO bferrol.sparktest


*/
