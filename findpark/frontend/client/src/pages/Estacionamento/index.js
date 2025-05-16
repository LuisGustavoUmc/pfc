import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import "./styles.css";

export default function Estacionamento() {
  const [estacionamentos, setEstacionamentos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get("/api/estacionamentos/meus")
      .then((res) => setEstacionamentos(res.data))
      .catch((err) => {
        console.error("Erro ao buscar estacionamentos:", err);
        setEstacionamentos([]);
      });
  }, []);

  const handleCadastrarVaga = (estacionamentoId) => {
    navigate(`/estacionamentos/${estacionamentoId}/cadastrar-vaga`);
  };

  return (
    <div className="p-3">
      <div className="text-center mb-6">
        <h1 className="text-2xl font-bold">Meus Estacionamentos</h1>
      </div>

      <div className="grid gap-4">
        {estacionamentos.length === 0 ? (
          <p className="text-center">Nenhum estacionamento cadastrado ainda.</p>
        ) : (
          estacionamentos.map((e) => (
            <div key={e.id} className="border rounded p-4 shadow">
              <h3 className="text-lg font-bold">{e.nome}</h3>
              <p><strong>Endereço:</strong> {e.endereco}</p>
              <p><strong>Capacidade:</strong> {e.capacidade}</p>
              <p><strong>Horário:</strong> {e.horaAbertura} às {e.horaFechamento}</p>
              <button
                className="btn btn-primary mt-2"
                onClick={() => handleCadastrarVaga(e.id)}
              >
                📍 Cadastrar Vaga
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
