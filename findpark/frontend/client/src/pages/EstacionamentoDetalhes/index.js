import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../../services/api";

export default function DetalhesEstacionamento() {
  const { id } = useParams();
  const [estacionamento, setEstacionamento] = useState(null);

  useEffect(() => {
    const fetchDetalhes = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        const response = await api.get(`/api/estacionamentos/${id}/detalhes`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setEstacionamento(response.data);
      } catch (err) {
        console.error("Erro ao buscar detalhes do estacionamento", err);
      }
    };

    fetchDetalhes();
  }, [id]);

  if (!estacionamento) return <p>Carregando...</p>;

  return (
    <div className="container mt-4">
      <h3>{estacionamento.nome}</h3>
      <p><strong>Endereço:</strong> {estacionamento.endereco}</p>
      <p><strong>Capacidade:</strong> {estacionamento.capacidade}</p>
      <p><strong>Vagas disponíveis:</strong> {estacionamento.vagasDisponiveis}/{estacionamento.capacidade}</p>
      <p><strong>Horário de funcionamento:</strong> {estacionamento.horaAbertura} às {estacionamento.horaFechamento}</p>

      <h5 className="mt-4">Vagas disponíveis</h5>
      <ul className="list-group">
        {estacionamento.vagas.map((vaga) => (
          <li key={vaga.id} className="list-group-item d-flex justify-content-between align-items-center">
            <div>
              Tipo: {vaga.tipo.join(", ")} - Preço: R$ {vaga.preco}
            </div>
            <Link to={`/vagas/${vaga.id}`} className="btn btn-primary btn-sm">
              Reservar
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
