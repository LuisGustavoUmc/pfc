import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import FiltroEstacionamento from "../../components/FiltroEstacionamento";
import { formatarEndereco } from "../../utils/Utils";

export default function HomeCliente() {
  const [usuario, setUsuario] = useState(null);
  const [vagas, setVagas] = useState([]);
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
        });
        setVagas(response.data);
      } catch (err) {
        console.error("Erro ao buscar vagas", err);
      }
    };

    fetchUsuario();
    fetchVagas();
  }, []);

  const handleBuscar = async (filtros) => {
    const token = localStorage.getItem("accessToken");

    try {
      const response = await api.get("/api/vagas/filtrar", {
        headers: { Authorization: `Bearer ${token}` },
        params: filtros,
      });
      setVagas(response.data);
    } catch (err) {
      console.error("Erro ao buscar vagas filtradas", err);
    }
  };

  return (
    <div className="container py-4">
      {usuario && (
        <div className="text-center mb-5 border-bottom pb-3">
          <h2 className="fw-bold">Olá, {usuario.nome}!</h2>
          <p className="text-muted">Veja as vagas disponíveis para você:</p>
        </div>
      )}

      <div className="mb-4">
        <FiltroEstacionamento onBuscar={handleBuscar} />
      </div>

      <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
        {vagas.map((vaga) => (
          <div className="col" key={vaga.id}>
            <div className="card h-100 shadow-sm border-0">
              <div className="card-body d-flex flex-column">
                <h5 className="card-title mb-2">
                  <a
                    href={`/estacionamentos/${vaga.estacionamento.id}`}
                    className="text-decoration-none text-primary fw-semibold"
                  >
                    {vaga.estacionamento.nome}
                  </a>
                </h5>

                <p className="card-text mb-1">
                  <strong>Endereço:</strong>
                  <i className="bi bi-geo-alt-fill me-1"></i>
                  {formatarEndereco(vaga.estacionamento.endereco)}
                </p>

                <p className="card-text mb-1">
                  <strong>Tipo:</strong> {vaga.tipo?.join(", ") || "Não informado"}
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
                    className="btn btn-outline-primary w-100"
                    onClick={() => navigate(`/vagas/${vaga.id}`)}
                  >
                    <i className="bi bi-info-circle me-1"></i> Ver detalhes
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
