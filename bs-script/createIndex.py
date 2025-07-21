from elasticsearch import Elasticsearch

es = Elasticsearch("http://localhost:9200", basic_auth=("elastic", "NuevaPassword123"))

indices = ["mediums", "espiritus", "ubicaciones"]

mapping = {
    "mappings": {
        "properties": {
            "id": {"type": "long"},
            "nombre": {"type": "text"},
            "descripcion": {"type": "text"},
            "descripcion_embedding": {
                "type": "dense_vector",
                "dims": 1024
            }
        }
    }
}

for indice in indices:
    if not es.indices.exists(index=indice):
        es.indices.create(index=indice, body=mapping)