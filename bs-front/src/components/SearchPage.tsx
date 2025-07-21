import React, { useState } from "react";
import styled from "styled-components";
import { buscarEntidad } from "../services/SearchService";

interface Props {
  tipo: "Medium" | "Espiritu" | "Ubicacion";
}

const Container = styled.div`
  max-width: 700px;
  margin: 60px auto;
  padding: 50px 60px;
  background: #251010;
  border-radius: 12px;
  border: 1px solid #4b2c2c;
  text-align: center;
`;

const Title = styled.h2`
  font-family: "Georgia", serif;
  font-size: 26px;
  margin-bottom: 30px;
  color: #f3e9e0;
  letter-spacing: 2px;
  border-bottom: 1px solid #4b2c2c;
  padding-bottom: 10px;
`;

const Input = styled.input`
  width: 85%;
  padding: 14px 18px;
  margin-bottom: 25px;
  font-size: 18px;
  border: 1px solid #6e4343;
  border-radius: 10px;
  background-color: #2f1515;
  color: #f0eae4;
  outline: none;
  transition: border 0.2s ease;

  &::placeholder {
    color: #c9b7b7;
    opacity: 0.6;
  }

  &:focus {
    border-color: #a16161;
  }
`;

const Button = styled.button`
  background-color: #632e2e;
  color: #f8f1e7;
  padding: 12px 28px;
  font-size: 17px;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s ease;

  &:hover {
    background-color: #7e3f3f;
  }
`;

const ResultsList = styled.ul`
  list-style: none;
  padding: 0;
  margin-top: 30px;
  background: #2a1414;
  border: 2px solid #4b2c2c;
  border-radius: 10px;
  max-height: 300px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 10px;
  }
  &::-webkit-scrollbar-thumb {
    background: #5e2c2c;
    border-radius: 8px;
  }
`;

const ResultItem = styled.li`
  padding: 20px 25px;
  border-bottom: 1px solid #3e1f1f;
  font-size: 20px;
  color: #f8f1e7;
  text-align: left;
  line-height: 1.6;
  background-color: #2f1515;
  transition: background 0.2s ease;

  &:hover {
    background-color: #462222;
  }

  strong {
    color: #f3e9e0;
    font-size: 21px;
  }
`;

const SearchPage: React.FC<Props> = ({ tipo }) => {
  const [texto, setTexto] = useState("");
  const [resultados, setResultados] = useState<any[]>([]);

  const handleBuscar = async () => {
    if (!texto.trim()) {
      setResultados([]);
      return;
    }
    const r = await buscarEntidad(tipo, texto);
    setResultados(r);
  };

  return (
    <Container>
      <Title>Búsqueda de {tipo}</Title>
      <Input
        type="text"
        value={texto}
        onChange={(e) => setTexto(e.target.value)}
        placeholder={`Buscar ${tipo.toLowerCase()}...`}
        onKeyDown={(e) => {
          if (e.key === "Enter") handleBuscar();
        }}
        autoFocus
      />
      <Button onClick={handleBuscar}>Buscar</Button>

      <ResultsList>
        {resultados.length === 0 ? (
          <ResultItem>No hay resultados.</ResultItem>
        ) : (
          resultados.map((item, i) => (
            <ResultItem key={i}>
              <strong>{item.nombre}</strong><br />
              {item.descripcion}
            </ResultItem>
          ))
        )}
      </ResultsList>
    </Container>
  );
};

export default SearchPage;