
# BIENVENIDO AL PROGRAMA "BASIC DASHBOARD BICIMAD"#


## En este programa podrá encontrar:



1. Carpeta Python:
    
	* Fichero ejecutable de python para consultar la API de bicimad y guardarlo en un fichero. El archivo original se encuentra en el nodo frontera "icemd-cluster:/home/bferrol/raw/BicimadAPI.PY"


2. Carpeta Flume:
    
	* Ejecutable de flume, el interceptor JSONCleaner y la configuración de Flume. Estos archivos se encuentran en el nodo frontera en la carpeta "icemd-cluster:/home/bferrol/raw/"


3. Carpeta Spark:
    
	* Proceso spark KPI Calculator y fichero de spark-submit. En el nodo frontera podemos encontrar el jar en "icemd-cluster:/home/bferrol/KPICalculator-assembly-0.1.jar"


4. Carpeta Hive:
    
	* Queries para crear las tablas externas.



## Funcionamiento del programa:



1. Al encender Flume, se quedará esperando recibir datos en la carpeta "icemd-cluster:/home/bferrol/raw/data/"

2. Se ejecutará el BicimadAPI.py que consultará cada 60s la API de bicimad y guardará los json en "icemd-cluster:/home/bferrol/raw/data/"

3. Flume cogerá estos ficheros json, les aplicará un interceptor que los va a limpiar y los dejará de forma replicada en dos carpetas:
    
	a. "hdfs:/user/bferrol/bicicletas/flume/kpi/" por canal de memoria.
    
	b. "hdfs:/user/bferrol/bicicletas/flume/historicalData/%y%m%d/%H%M/" por canal file para historificar.

4. Se ejecuta spark (idealmente con un cron), coge y borra los datos de "hdfs:/user/bferrol/bicicletas/flume/kpi/" y crea dos ficheros csv:
    
	a. Fichero de KPI agregados en "hdfs:/user/bferrol/bicicletas/kpi/".
    
	b. Fichero de KPI por estación en "hdfs:/user/bferrol/bicicletas/stations/"

5. Hive crea por única vez 2 tablas externas en los directorios donde spark deja los ficheros, de esta forma, cada un minuto spark procesa los datos, sobreescribe el actual de la carpeta y las tablas de Hive tienen los kpi actualizados.

6. Power BI conectado con las tablas de flume presenta los KPI en un dashboard.
