import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../services/api";
import FiltroEstacionamento from "../../components/FiltroEstacionamento";
import { formatarEndereco } from "../../utils/Utils";
import "./styles.css"

export default function HomeCliente() {
  const [usuario, setUsuario] = useState(null);
  const [vagas, setVagas] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [filtros, setFiltros] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    const fetchUsuario = async () => {
      try {
        const response = await api.get("/api/usuarios/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUsuario(response.data);
      } catch (err) {
        console.error("Erro ao buscar dados do usuário", err);
      }
    };

    const fetchVagas = async () => {
      try {
        const response = await api.get("/api/vagas/disponiveis", {
          headers: { Authorization: `Bearer ${token}` },
          params: {
            page: page - 1,
            size: 6,
            direction: "asc",
          },
        });

        if (response.data && Array.isArray(response.data.content)) {
          setVagas(response.data.content);
          setTotalPages(response.data.totalPages);
        } else {
          setVagas([]);
        }
      } catch (err) {
        console.error("Erro ao buscar vagas", err);
      }
    };

    fetchUsuario();
    fetchVagas();
  }, [page]);

  const handleBuscar = async (filtros) => {
    const token = localStorage.getItem("accessToken");

    setFiltros(filtros);
    setPage(1);

    try {
      const response = await api.get("/api/vagas/filtrar", {
        headers: { Authorization: `Bearer ${token}` },
        params: {
          ...filtros,
          page: 0,
          size: 6,
          direction: "asc",
        },
      });

      // Verifica se os dados de vagas existem
      if (response.data && Array.isArray(response.data.content)) {
        setVagas(response.data.content);
        setTotalPages(response.data.totalPages);
      } else {
        setVagas([]); // Caso contrário, assegure-se de que o estado de vagas seja vazio
      }
    } catch (err) {
      console.error("Erro ao buscar vagas filtradas", err);
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage > 0 && newPage <= totalPages) {
      setPage(newPage);
    }
  };

  return (
    <div className="container-lg py-4">
      {usuario && (
        <div className="text-center mb-4">
          <h1 className="display-6">👋 Olá, {usuario.nome || "Cliente"}!</h1>
          <p className="text-muted">Veja as vagas disponíveis para você:</p>
        </div>
      )}

      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div></div>
        {/* Você pode adicionar outros filtros ou ações aqui futuramente */}
      </div>

      <div className="mb-4">
        <FiltroEstacionamento onBuscar={handleBuscar} />
      </div>

      <h2 className="h4 text-center mb-4">Vagas Disponíveis</h2>

      <div className="row">
        {vagas.length === 0 ? (
          <div className="col-12">
            <div className="alert alert-info" role="alert">
              Nenhuma vaga disponível no momento.
            </div>
          </div>
        ) : (
          vagas.map((vaga) => (
            <div className="col-md-6 col-lg-4 mb-4" key={vaga.id}>
              <div className="card shadow-sm h-100 vaga-card">
                <div className="card-body d-flex flex-column">
                  <h5 className="card-title mb-2">
                    <Link
                      to={`/estacionamentos/${vaga.estacionamento.id}`}
                      className="text-decoration-none text-primary fw-semibold"
                    >
                      {vaga.estacionamento.nome}
                    </Link>
                  </h5>

                  <p className="card-text">
                    <strong>Endereço:</strong>{" "}
                    <i className="bi bi-geo-alt-fill me-1"></i>
                    {formatarEndereco(vaga.estacionamento.endereco)}
                  </p>

                  <p className="card-text mb-1">
                    <strong>Tipo:</strong>{" "}
                    {vaga.tipo?.join(", ") || "Não informado"}
                  </p>

                  <p className="card-text mb-3">
                    <strong>Preço:</strong>{" "}
                    {Intl.NumberFormat("pt-BR", {
                      style: "currency",
                      currency: "BRL",
                    }).format(vaga.preco)}
                  </p>

                  <div className="mt-auto">
                    <button
                      className="btn btn-primary"
                      onClick={() => navigate(`/vagas/${vaga.id}`)}
                    >
                      <i className="bi bi-info-circle me-1"></i> Ver detalhes
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {totalPages > 1 && (
        <div className="d-flex justify-content-center mt-4">
          <nav>
            <ul className="pagination">
              <li className={`page-item ${page === 1 ? "disabled" : ""}`}>
                <button
                  className="page-link"
                  onClick={() => handlePageChange(page - 1)}
                  disabled={page === 1}
                >
                  Anterior
                </button>
              </li>
              {[...Array(totalPages)].map((_, index) => (
                <li
                  key={index}
                  className={`page-item ${page === index + 1 ? "active" : ""}`}
                >
                  <button
                    className="page-link"
                    onClick={() => handlePageChange(index + 1)}
                  >
                    {index + 1}
                  </button>
                </li>
              ))}
              <li
                className={`page-item ${page === totalPages ? "disabled" : ""}`}
              >
                <button
                  className="page-link"
                  onClick={() => handlePageChange(page + 1)}
                  disabled={page === totalPages}
                >
                  Próxima
                </button>
              </li>
            </ul>
          </nav>
        </div>
      )}
    </div>
  );
}
