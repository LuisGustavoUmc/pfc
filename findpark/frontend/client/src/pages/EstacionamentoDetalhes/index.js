import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";

export default function DetalhesEstacionamento() {
  const { id } = useParams();  // id do estacionamento
  const [estacionamento, setEstacionamento] = useState(null); // Inicializado com null
  const [vagas, setVagas] = useState([]);  // Vagas será inicializado como array vazio

  useEffect(() => {
    const fetchDetalhes = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        const response = await api.get(`/api/estacionamentos/${id}/detalhes`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        // A resposta será uma página de detalhes, com um estacionamento e suas vagas
        setEstacionamento(response.data.content[0]); // Primeiro estacionamento da página
        setVagas(response.data.content[0].vagas);    // Vagas do estacionamento
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
      <p><strong>Endereço:</strong> {formatarEndereco(estacionamento.endereco)}</p>
      <p><strong>Capacidade:</strong> {estacionamento.capacidade}</p>
      <p><strong>Vagas disponíveis:</strong> {estacionamento.vagasDisponiveis}/{estacionamento.capacidade}</p>
      <p><strong>Horário de funcionamento:</strong> {estacionamento.horaAbertura} às {estacionamento.horaFechamento}</p>

      <h5 className="mt-4">Vagas disponíveis</h5>
      {/* Verifique se existem vagas */}
      <ul className="list-group">
        {vagas.length > 0 ? (
          vagas.map((vaga) => (
            <li key={vaga.id} className="list-group-item d-flex justify-content-between align-items-center">
              <div>
                Tipo: {Array.isArray(vaga.tipo) ? vaga.tipo.join(", ") : vaga.tipo} - 
                Preço: {Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(vaga.preco)}
              </div>
              <Link to={`/vagas/${vaga.id}`} className="btn btn-primary btn-sm">
                Reservar
              </Link>
            </li>
          ))
        ) : (
          <li className="list-group-item">Não há vagas disponíveis</li>
        )}
      </ul>
    </div>
  );
}
