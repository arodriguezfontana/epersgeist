package ar.edu.unq.epersgeist.persistencia.dao.impl;

import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneradorDatosAmbasBases {

    // --- Configuración Neo4j ---
    private static final String NEO4J_URI = "bolt://localhost:7687";
    private static final String NEO4J_USER = "neo4j";
    private static final String NEO4J_PASSWORD = "rootroot";

    // --- Configuración PostgreSQL ---
    private static final String PG_DB_URL = "jdbc:postgresql://localhost:5432/epersgeist";
    private static final String PG_DB_USER = "postgres";
    private static final String PG_DB_PASSWORD = "root";

    public static void main(String[] args) {
        final int NUM_NODES = 100;
        final int CONNECTIONS_PER_NODE = 10;

        Map<Integer, Integer> nodeData = generateNodeData(NUM_NODES);
        System.out.println("Datos de nodos (ID y Energía) generados una sola vez para consistencia.");

        Driver neo4jDriver = null;
        try {
            System.out.println("\n--- Generando datos en Neo4j ---");
            neo4jDriver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));

            List<Long> neo4jNodeNativeIds = createNeo4jNodes(neo4jDriver, nodeData);

            createRandomNeo4jConnections(neo4jDriver, neo4jNodeNativeIds, CONNECTIONS_PER_NODE);
            System.out.println("Datos de ubicaciones y conexiones generados exitosamente en Neo4j.");

        } catch (Exception e) {
            System.err.println("Error al generar datos en Neo4j:");
            e.printStackTrace();
        } finally {
            if (neo4jDriver != null) {
                neo4jDriver.close();
            }
        }

        Connection pgConnection = null;
        try {
            System.out.println("\n--- Generando datos en PostgreSQL ---");
            Class.forName("org.postgresql.Driver");

            pgConnection = DriverManager.getConnection(PG_DB_URL, PG_DB_USER, PG_DB_PASSWORD);
            pgConnection.setAutoCommit(false);

            createPostgreSQLNodes(pgConnection, nodeData);

            pgConnection.commit();
            System.out.println("Datos de ubicaciones generados exitosamente en PostgreSQL.");

        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de PostgreSQL no encontrado. Asegúrate de tenerlo en tu classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de SQL al generar datos en PostgreSQL:");
            e.printStackTrace();
            try {
                if (pgConnection != null) {
                    pgConnection.rollback();
                    System.err.println("Transacción de PostgreSQL revertida.");
                }
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback:");
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado al generar datos en PostgreSQL:");
            e.printStackTrace();
        } finally {
            if (pgConnection != null) {
                try {
                    pgConnection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión de PostgreSQL:");
                    e.printStackTrace();
                }
            }
        }
    }
    private static Map<Integer, Integer> generateNodeData(int numNodes) {
        Map<Integer, Integer> data = new HashMap<>();
        for (int i = 0; i < numNodes; i++) {
            data.put(i, ThreadLocalRandom.current().nextInt(0, 101));
        }
        return data;
    }
    private static List<Long> createNeo4jNodes(Driver driver, Map<Integer, Integer> nodeData) {
        List<Long> neo4jNodeNativeIds = new ArrayList<>();
        try (Session session = driver.session()) {
            for (Map.Entry<Integer, Integer> entry : nodeData.entrySet()) {
                int idLogico = entry.getKey();
                int energia = entry.getValue();

                Result result = session.run("CREATE (u:UbicacionNeo {id: $id, nombre: $nombre, energia: $energia}) " +
                                "RETURN id(u) AS nodeId",
                        parameters("id", idLogico, "nombre", "Ubi_" + idLogico, "energia", energia));
                neo4jNodeNativeIds.add(result.single().get("nodeId").asLong());
            }
        }
        System.out.println(nodeData.size() + " ubicaciones creadas en Neo4j.");
        return neo4jNodeNativeIds;
    }

    private static void createRandomNeo4jConnections(Driver driver, List<Long> neo4jNodeNativeIds, int connectionsPerNode) {
        Random random = ThreadLocalRandom.current();
        try (Session session = driver.session()) {
            List<Long> availableTargetNodeIds = new ArrayList<>(neo4jNodeNativeIds);
            int connectionsCount = 0;

            for (Long sourceNodeNativeId : neo4jNodeNativeIds) {
                Collections.shuffle(availableTargetNodeIds, random);

                int connectionsMade = 0;
                for (Long targetNodeNativeId : availableTargetNodeIds) {
                    if (!sourceNodeNativeId.equals(targetNodeNativeId)) {
                        session.run("MATCH (u:UbicacionNeo) WHERE id(u) = $sourceNativeId " +
                                        "MATCH (ub:UbicacionNeo) WHERE id(ub) = $targetNativeId " +
                                        "MERGE (u)-[:CONECTADA]->(ub)",
                                parameters("sourceNativeId", sourceNodeNativeId, "targetNativeId", targetNodeNativeId));
                        connectionsMade++;
                        connectionsCount++;
                    }
                    if (connectionsMade >= connectionsPerNode) {
                        break;
                    }
                }
            }
            System.out.println(connectionsCount + " conexiones creadas en Neo4j.");
        }
    }

    private static void createPostgreSQLNodes(Connection connection, Map<Integer, Integer> nodeData) throws SQLException {
        String sql = "INSERT INTO Ubicacion (id, nombre, energia) VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET nombre = EXCLUDED.nombre, energia = EXCLUDED.energia";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : nodeData.entrySet()) {
                int idLogico = entry.getKey();
                int energia = entry.getValue();

                pstmt.setInt(1, idLogico);
                pstmt.setString(2, "Ubi_" + idLogico);
                pstmt.setInt(3, energia);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        System.out.println(nodeData.size() + " ubicaciones creadas en PostgreSQL.");
    }
}

