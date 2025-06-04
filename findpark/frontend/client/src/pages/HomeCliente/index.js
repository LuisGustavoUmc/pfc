import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";

const HomeCliente = () => {
  const [estacionamentos, setEstacionamentos] = useState([]);
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [nomeUsuario, setNomeUsuario] = useState("");
  const pageSize = 4;

  useEffect(() => {
    api
      .get("/api/usuarios/me")
      .then((res) => setNomeUsuario(res.data?.nome ?? ""))
      .catch((err) => console.error("Erro ao buscar usuário:", err));

    buscarEstacionamentos();
  }, [paginaAtual]);

  const buscarEstacionamentos = () => {
    api
      .get("/api/estacionamentos/disponiveis", {
        params: {
          page: paginaAtual,
          size: pageSize,
        },
      })
      .then((res) => {
        setEstacionamentos(res.data?.content ?? []);
        setTotalPaginas(res.data?.totalPages ?? 0);
      })
      .catch((err) => {
        console.error("Erro ao buscar estacionamentos:", err);
        setEstacionamentos([]);
      });
  };

  const renderPaginacao = () => (
    <div className="d-flex justify-content-center mt-4">
      <button
        className="btn btn-outline-primary me-2"
        disabled={paginaAtual === 0}
        onClick={() => setPaginaAtual(paginaAtual - 1)}
      >
        Anterior
      </button>
      <span className="align-self-center">
        Página {paginaAtual + 1} de {totalPaginas}
      </span>
      <button
        className="btn btn-outline-primary ms-2"
        disabled={paginaAtual + 1 >= totalPaginas}
        onClick={() => setPaginaAtual(paginaAtual + 1)}
      >
        Próxima
      </button>
    </div>
  );

  return (
    <div className="container-lg py-4">
      <div className="text-center mb-4">
        <h1 className="display-6">Olá, {nomeUsuario || "Cliente"}!</h1>
        <p className="text-muted">Escolha um estacionamento para reservar sua vaga.</p>
      </div>

      <h2 className="h4 text-center mb-4">Estacionamentos disponíveis</h2>
      <div className="row">
        {estacionamentos.length === 0 ? (
          <div className="col-12">
            <div className="alert alert-info" role="alert">
              Nenhum estacionamento com vagas disponíveis.
            </div>
          </div>
        ) : (
          estacionamentos.map((e) => (
            <div key={e.id} className="col-md-6 mb-4">
              <div className="card shadow-sm h-100">
                <div className="card-body">
                  <h5 className="card-title mb-2">
                    <Link
                      to={`/estacionamentos/${e.id}`}
                      className="text-decoration-none text-primary fw-semibold"
                    >
                      {e.nome}
                    </Link>
                  </h5>

                  <p className="card-text">
                    <strong>Endereço:</strong> {formatarEndereco(e.endereco)}
                    <br />
                    <strong>Vagas disponíveis:</strong>{" "}
                    <span className="badge bg-success">
                      {e.vagasDisponiveis}/{e.capacidade}
                    </span>
                    <br />
                    <strong>Horário:</strong>{" "}
                    {(e.horaAbertura || "").slice(0, 5)} às{" "}
                    {(e.horaFechamento || "").slice(0, 5)}
                  </p>

                  <div className="d-flex gap-2 mt-3">
                    <Link
                      to={`/estacionamentos/${e.id}`}
                      className="btn btn-outline-primary btn-sm"
                    >
                      Ver Detalhes
                    </Link>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {totalPaginas > 1 && renderPaginacao()}
    </div>
  );
};

export default HomeCliente;
