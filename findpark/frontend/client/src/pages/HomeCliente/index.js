import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";

export default function HomeCliente() {
  const [usuario, setUsuario] = useState(null);
  const [vagas, setVagas] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
  const token = localStorage.getItem("accessToken");

  const fetchUsuario = async () => {
    try {
      const response = await api.get("/api/usuarios/me", {
        headers: { Authorization: `Bearer ${token}` }
      });
      setUsuario(response.data);
    } catch (err) {
      console.error("Erro ao buscar dados do usuário", err);
    }
  };

  const fetchVagas = async () => {
    try {
      const response = await api.get("/api/vagas/disponiveis", {
        headers: { Authorization: `Bearer ${token}` }
      });
      setVagas(response.data);
    } catch (err) {
      console.error("Erro ao buscar vagas", err);
    }
  };

  fetchUsuario();
  fetchVagas();
}, []);

  return (
    <div className="container mt-4">
      {usuario && (
        <h4 className="mb-4">Olá, {usuario.nome}! Veja as vagas disponíveis:</h4>
      )}

      <div className="row">
        {vagas.map((vaga) => (
          <div className="col-md-4 mb-4" key={vaga.id}>
            <div className="card shadow-sm">
              <div className="card-body">
                <h5 className="card-title">
                  <a href={`/estacionamentos/${vaga.estacionamento.id}`} className="text-decoration-none">
                    {vaga.estacionamento.nome}
                  </a>
                </h5>

                <p className="card-text">
                  <strong>Tipo:</strong> {vaga.tipo.join(", ")}
                </p>
                <p className="card-text">
                  <strong>Preço: </strong>{Intl.NumberFormat('pt-BR', {style: 'currency', currency: 'BRL'}).format(vaga.preco)}
                </p>
                <p className="card-text">
                  <strong>Endereço:</strong> {vaga.estacionamento.endereco}
                </p>

                <button className="btn btn-primary" onClick={() => navigate(`/vagas/${vaga.id}`)}>
                  Ver detalhes
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
