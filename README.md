# Parking-App-ElasticSearch
 aplicación de parqueadero

## Documentación Útil
- Documentación oficial de [Spring Data Elasticsearch 4.2.3](https://docs.spring.io/spring-data/elasticsearch/docs/4.2.3/reference/html/#new-features)
- Documentación oficial de [Elasticsearch 7.10](https://www.elastic.co/guide/en/elasticsearch/reference/7.10/index.html)

Inicialmente es necesario cargar el mapping de la estructura de los datos:
```bash
curl.exe -X PUT "<<Full Access URL de Bonsai>>/places" `-H "Content-Type: application/json" ` --data "@Parking_mapping.json"

``````

Recuerda que para cargar los datos de prueba debes ejecutar el siguiente comando (desde la carpeta raíz del proyecto):
```bash

curl.exe -X POST "<<Full Access URL de Bonsai>>/_bulk" `-H "Content-Type: application/json" ` --data-binary "@Parking_bulk.ndjson"
```
No es necesario que crees el índice manualmente, Spring Data Elasticsearch lo hará por ti (gracias a la anotación `@Document`).
