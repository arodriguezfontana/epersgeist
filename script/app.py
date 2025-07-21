from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
from elasticsearch import Elasticsearch
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

es = Elasticsearch("http://localhost:9200", basic_auth=("elastic", "NuevaPassword123"))
model = SentenceTransformer("BAAI/bge-large-en-v1.5")

def buscar_por_tipo(indice, texto, k=3, umbral=0.8):
    query_vector = model.encode(texto).tolist()

    response = es.search(
        index=indice,
        knn={
            "field": "descripcion_embedding",
            "k": k,
            "num_candidates": 100,
            "query_vector": query_vector
        }
    )

    resultados = []
    for hit in response["hits"]["hits"]:
        score = hit["_score"]
        if score >= umbral:
            src = hit["_source"]
            resultados.append({
                "id": src["id"],
                "nombre": src["nombre"],
                "descripcion": src["descripcion"],
                "score": score
            })

    return resultados

@app.route("/buscar-medium", methods=["POST"])
def buscar_medium():
    data = request.json
    return jsonify(buscar_por_tipo("mediums", data["texto"], data.get("k", 3)))

@app.route("/indexar-medium", methods=["POST"])
def indexar_medium():
    return indexar_entidad("mediums", request.get_json())

@app.route("/buscar-espiritu", methods=["POST"])
def buscar_espiritu():
    data = request.json
    return jsonify(buscar_por_tipo("espiritus", data["texto"], data.get("k", 3)))

@app.route("/indexar-espiritu", methods=["POST"])
def indexar_espiritu():
    return indexar_entidad("espiritus", request.get_json())

@app.route("/buscar-ubicacion", methods=["POST"])
def buscar_ubicacion():
    data = request.json
    return jsonify(buscar_por_tipo("ubicaciones", data["texto"], data.get("k", 3)))

@app.route("/indexar-ubicacion", methods=["POST"])
def indexar_ubicacion():
    return indexar_entidad("ubicaciones", request.get_json())

def indexar_entidad(indice, data):
    id = data["id"]
    nombre = data["nombre"]
    descripcion = data["descripcion"]

    embedding = model.encode(descripcion).tolist()

    doc = {
        "id": id,
        "nombre": nombre,
        "descripcion": descripcion,
        "descripcion_embedding": embedding
    }

    es.index(index=indice, id=id, document=doc)
    es.indices.refresh(index=indice)

    return jsonify({"mensaje": f"{indice.capitalize()} indexado correctamente"}), 200