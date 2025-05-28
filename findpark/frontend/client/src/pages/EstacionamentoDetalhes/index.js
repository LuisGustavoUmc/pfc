import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";

export default function DetalhesEstacionamento() {
  const { id } = useParams(); // ID do estacionamento
  const [estacionamento, setEstacionamento] = useState(null);
  const [vagas, setVagas] = useState([]);
  const role = localStorage.getItem("userRole");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDetalhes = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        const response = await api.get(`/api/estacionamentos/${id}/detalhes`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const estac = response.data.content[0];
        setEstacionamento(estac);
        setVagas(estac.vagas);
      } catch (err) {
        console.error("Erro ao buscar detalhes do estacionamento", err);
      }
    };

    fetchDetalhes();
  }, [id]);

  const handleDeletarEstacionamento = async (id) => {
    if (!window.confirm("Tem certeza que deseja excluir este estacionamento?")) return;

    try {
      const token = localStorage.getItem("accessToken");
      await api.delete(`/api/estacionamentos/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert("Estacionamento excluído com sucesso!");
      navigate("/home-proprietario");
    } catch (err) {
      console.error("Erro ao excluir estacionamento:", err);
      alert("Erro ao excluir estacionamento.");
    }
  };

  const handleDeletarVaga = async (vagaId) => {
    if (!window.confirm("Deseja excluir esta vaga?")) return;

    try {
      const token = localStorage.getItem("accessToken");
      await api.delete(`/api/vagas/${vagaId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setVagas((prev) => prev.filter((v) => v.id !== vagaId));
      alert("Vaga excluída com sucesso!");
    } catch (err) {
      console.error("Erro ao excluir vaga:", err);
      alert("Erro ao excluir vaga.");
    }
  };

  if (!estacionamento) return <p>Carregando...</p>;

  return (
    <div className="container mt-4">
      {role && (
        <Link
          to={role === "PROPRIETARIO" ? "/home-proprietario" : "/home-cliente"}
          className="btn btn-link text-decoration-none text-dark mb-3"
        >
          <i className="fas fa-arrow-left me-2"></i>Voltar
        </Link>
      )}

      <h3>{estacionamento.nome}</h3>
      <p><strong>Endereço:</strong> {formatarEndereco(estacionamento.endereco)}</p>
      <p><strong>Capacidade:</strong> {estacionamento.capacidade}</p>
      <p><strong>Vagas disponíveis:</strong> {estacionamento.vagasDisponiveis}/{estacionamento.capacidade}</p>
      <p><strong>Horário de funcionamento:</strong> {estacionamento.horaAbertura} às {estacionamento.horaFechamento}</p>

      {role === "PROPRIETARIO" && (
        <div className="d-flex gap-2 my-3">
          <Link to={`/estacionamentos/editar/${estacionamento.id}`} className="btn btn-warning">
            <i className="fas fa-edit me-2"></i>Editar Estacionamento
          </Link>
          <button className="btn btn-danger" onClick={() => handleDeletarEstacionamento(estacionamento.id)}>
            <i className="fas fa-trash-alt me-2"></i>Excluir Estacionamento
          </button>
        </div>
      )}

      <h5 className="mt-4">Vagas disponíveis</h5>
      <ul className="list-group">
        {vagas.length > 0 ? (
          vagas.map((vaga) => (
            <li
              key={vaga.id}
              className="list-group-item d-flex justify-content-between align-items-center"
            >
              <div>
                Tipo: {Array.isArray(vaga.tipo) ? vaga.tipo.join(", ") : vaga.tipo} - 
                Preço:{" "}
                {Intl.NumberFormat("pt-BR", {
                  style: "currency",
                  currency: "BRL",
                }).format(vaga.preco)}
              </div>

              {role === "PROPRIETARIO" && (
                <div className="d-flex gap-2">
                  <Link to={`/vagas/${vaga.id}/editar`} className="btn btn-secondary btn-sm">
                    Editar
                  </Link>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDeletarVaga(vaga.id)}>
                    Excluir
                  </button>
                </div>
              )}

              {role === "CLIENTE" && (
                <div className="d-flex gap-2">
                  <Link to={`/vagas/${vaga.id}`} className="btn btn-secondary btn-sm">
                    Reservar
                  </Link>
                </div>
              )}
            </li>
          ))
        ) : (
          <li className="list-group-item">Não há vagas disponíveis</li>
        )}
      </ul>
    </div>
  );
}
