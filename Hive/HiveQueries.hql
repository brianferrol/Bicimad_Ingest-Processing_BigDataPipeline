USE bferrol;

--===================================
--=== CREACIÓN DE TABLAS EXTERNAS ===
--===================================

-- TABLA DE KPI AGREGADOS:

CREATE EXTERNAL TABLE IF NOT EXISTS bferrol.bicimadKPI 
(numBases INT,
PorcEstacionesSinUsar FLOAT,
PorcEstacionesSinBicicletasLibres FLOAT,
BicicletasSiendoUtilizadas INT,
ReservasProgramadas INT,
BasesNoDisponibles INT,
PromedioBasesLibres INT,
PromedioBasesAncladas INT)
COMMENT 'KPI ya generados'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '#'
STORED AS TEXTFILE
LOCATION '/user/bferrol/bicicletas/kpi/';


-- TABLA DE KPI POR ESTACIÓN:

CREATE EXTERNAL TABLE IF NOT EXISTS bferrol.bicimadStations 
(id STRING,
name STRING,
boSinUsar INT,
boNotAvaiable INT,
BicicletasSiendoUtilizadas INT,
Bicicletas ancladas INT,
ReservasProgramadas INT,
UseLevel FLOAT)
COMMENT 'Datos de estaciones'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '#'
STORED AS TEXTFILE
LOCATION '/user/bferrol/bicicletas/stations/';




