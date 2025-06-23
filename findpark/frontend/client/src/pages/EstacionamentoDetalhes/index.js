import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";
import { toast } from "react-toastify";
import Swal from "sweetalert2";

export default function DetalhesEstacionamento() {
  const { id } = useParams();
  const [estacionamento, setEstacionamento] = useState(null);
  const [vagas, setVagas] = useState([]);
  const role = localStorage.getItem("userRole");
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  const navigate = useNavigate();

  const confirmarAcao = async (mensagem) => {
    const result = await Swal.fire({
      title: "Tem certeza?",
      text: mensagem,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sim, confirmar",
      cancelButtonText: "Cancelar",
      buttonsStyling: false,
      customClass: {
        confirmButton: "btn btn-danger me-2",
        cancelButton: "btn btn-secondary",
      },
    });

    return result.isConfirmed;
  };

  useEffect(() => {
    const fetchDetalhes = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        const response = await api.get(`/api/estacionamentos/${id}/detalhes`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const estac = response.data.content[0];
        setEstacionamento(estac);

        const vagasResp = await api.get(`/api/vagas/estacionamento/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
          params: {
            page: paginaAtual,
            size: 6,
          },
        });

        setVagas(vagasResp.data.content);
        setTotalPaginas(vagasResp.data.totalPages);
      } catch (err) {
        console.error("Erro ao buscar detalhes do estacionamento", err);
      }
    };

    fetchDetalhes();
  }, [id, paginaAtual]);

  const handleDeletarEstacionamento = async (id) => {
  const confirmar = await confirmarAcao(
    "Tem certeza que deseja excluir este estacionamento? Esta ação não poderá ser desfeita. Todas as reservas ativas serão canceladas e as vagas serão removidas."
  );

  if (!confirmar) return;

  try {
    const token = localStorage.getItem("accessToken");

    await api.delete(`/api/estacionamentos/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    toast.success("Estacionamento excluído com sucesso!");
    navigate("/home-proprietario");
  } catch (err) {
    console.error("Erro ao excluir estacionamento:", err);

    const status = err.response?.status;
    const mensagem = err.response?.data?.message || "Erro ao excluir estacionamento.";

    if (status === 409) {
      toast.error(`${mensagem}`); // Conflito: pode ser reserva ativa
    } else if (status === 404) {
      toast.error("Estacionamento não encontrado.");
    } else if (status === 403) {
      toast.error("Você não tem permissão para excluir este estacionamento.");
    } else {
      toast.error("Erro inesperado ao excluir estacionamento.");
    }
  }
};

  const handleDeletarVaga = async (vagaId) => {
    const confirmar = await confirmarAcao("Deseja excluir esta vaga?");
    if (!confirmar) return;

    try {
      const token = localStorage.getItem("accessToken");
      await api.delete(`/api/vagas/${vagaId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      toast.success("Vaga excluída com sucesso!");
      // Recarrega a lista de vagas da página atual
      const vagasResp = await api.get(`/api/vagas/estacionamento/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
        params: { page: paginaAtual, size: 6 },
      });

      setVagas(vagasResp.data.content);
      setTotalPaginas(vagasResp.data.totalPages);
    } catch (err) {
      console.error("Erro ao excluir vaga:", err);
      toast.error("Erro ao excluir vaga.");
    }
  };

  if (!estacionamento)
    return (
      <div className="text-center my-5">
        <div className="spinner-border" role="status" />
      </div>
    );

  return (
    <div className="container-lg my-4">
      {role && (
        <Link
          to={role === "PROPRIETARIO" ? "/home-proprietario" : "/home-cliente"}
          className="btn btn-outline-secondary mb-3"
        >
          <i className="fas fa-arrow-left me-2"></i>Voltar
        </Link>
      )}
      <div className="card shadow-sm p-4">
        <div className="card-body text-center">
          <h3 className="card-title mb-3 text-primary text-dark">
            {estacionamento.nome}
          </h3>
          <p>
            <strong>Endereço:</strong>{" "}
            {formatarEndereco(estacionamento.endereco)}
          </p>
          <p>
            <strong>Capacidade:</strong> {estacionamento.capacidade}
          </p>
          <p>
            <strong>Vagas disponíveis:</strong>{" "}
            <span className="badge bg-success">
              {estacionamento.vagasDisponiveis}/{estacionamento.capacidade}
            </span>
          </p>
          <p>
            <strong>Horário de funcionamento:</strong>{" "}
            {estacionamento.horaAbertura} às {estacionamento.horaFechamento}
          </p>

          {role === "PROPRIETARIO" && (
            <div className="d-flex justify-content-center flex-wrap gap-2 mt-4">
              <Link
                to={`/estacionamentos/editar/${estacionamento.id}`}
                className="btn btn-warning"
              >
                <i className="fas fa-edit me-2"></i>Editar
              </Link>
              <button
                className="btn btn-danger"
                onClick={() => handleDeletarEstacionamento(estacionamento.id)}
              >
                <i className="fas fa-trash-alt me-2"></i>Excluir
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="mt-5">
        <h5 className="mb-3">Vagas disponíveis</h5>
        {vagas.length > 0 ? (
          <div className="row">
            {vagas.map((vaga) => (
              <div key={vaga.id} className="col-md-6 col-lg-4 mb-3">
                <div className="card h-100 shadow-sm">
                  <div className="card-body d-flex flex-column justify-content-between">
                    <div>
                      <h6 className="card-title">
                        Tipo:{" "}
                        {Array.isArray(vaga.tipo)
                          ? vaga.tipo.join(", ")
                          : vaga.tipo}
                      </h6>
                      <p className="card-text">
                        Preço por hora:{" "}
                        {Intl.NumberFormat("pt-BR", {
                          style: "currency",
                          currency: "BRL",
                        }).format(vaga.preco)}
                      </p>
                    </div>
                    <div
                      className={`mt-3 d-flex ${
                        role === "PROPRIETARIO"
                          ? "justify-content-center gap-2"
                          : "justify-content-between"
                      }`}
                    >
                      {role === "PROPRIETARIO" && (
                        <>
                          <Link
                            to={`/vagas/${vaga.id}/editar`}
                            className="btn btn-outline-secondary btn-sm"
                          >
                            Editar
                          </Link>
                          <button
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => handleDeletarVaga(vaga.id)}
                          >
                            Excluir
                          </button>
                        </>
                      )}

                      {role === "CLIENTE" && (
                        <Link
                          to={`/vagas/${vaga.id}`}
                          className="btn btn-success btn-sm"
                        >
                          Reservar
                        </Link>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}

            {totalPaginas > 1 && (
              <div className="d-flex justify-content-between align-items-center mt-3">
                <button
                  className="btn btn-outline-primary btn-sm"
                  onClick={() => setPaginaAtual((prev) => prev - 1)}
                  disabled={paginaAtual === 0}
                >
                  Anterior
                </button>
                <span>
                  Página {paginaAtual + 1} de {totalPaginas}
                </span>
                <button
                  className="btn btn-outline-primary btn-sm"
                  onClick={() => setPaginaAtual((prev) => prev + 1)}
                  disabled={paginaAtual >= totalPaginas - 1}
                >
                  Próxima
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="alert alert-info">Não há vagas disponíveis</div>
        )}
      </div>
    </div>
  );
}
