import React from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";

const Container = styled.div`
  max-width: 700px;
  margin: 80px auto;
  padding: 60px;
  background: #251010;
  border-radius: 12px;
  border: 1px solid #4b2c2c;
  text-align: center;
`;

const Title = styled.h1`
  font-family: "Georgia", serif;
  font-size: 28px;
  margin-bottom: 50px;
  color: #f3e9e0;
  letter-spacing: 2px;
  border-bottom: 1px solid #4b2c2c;
  padding-bottom: 12px;
`;

const Button = styled.button`
  background-color: #632e2e;
  color: #f8f1e7;
  padding: 16px 40px;
  margin: 15px;
  border-radius: 10px;
  font-size: 18px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: background 0.2s ease;

  &:hover {
    background-color: #7e3f3f;
  }
`;

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container>
      <Title>Selecciona qué buscar</Title>
      <Button onClick={() => navigate("/mediums")}>✨ Mediums</Button>
      <Button onClick={() => navigate("/espiritus")}>🪽 Espíritus</Button>
      <Button onClick={() => navigate("/ubicaciones")}>📍 Ubicaciones</Button>
    </Container>
  );
};

export default HomePage;