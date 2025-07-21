import axios from "axios";

export async function buscarEntidad(
  tipo: "Medium" | "Espiritu" | "Ubicacion",
  texto: string
): Promise<any[]> {
  let endpoint = "";

  switch (tipo) {
    case "Medium":
      endpoint = "/buscar-medium";
      break;
    case "Espiritu":
      endpoint = "/buscar-espiritu";
      break;
    case "Ubicacion":
      endpoint = "/buscar-ubicacion";
      break;
    default:
      return [];
  }

  try {
    const response = await axios.post(`http://localhost:5050${endpoint}`, {
      texto,
    });
    return response.data;
  } catch (error) {
    console.error("Error en búsqueda:", error);
    return [];
  }
}