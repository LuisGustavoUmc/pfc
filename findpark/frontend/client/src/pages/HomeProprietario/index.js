import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";

const HomeProprietario = () => {
  const [estacionamentos, setEstacionamentos] = useState([]);
  const [nomeUsuario, setNomeUsuario] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get("/api/usuarios/me")
      .then((res) => setNomeUsuario(res.data.nome))
      .catch((err) => console.error("Erro ao buscar usuário:", err));

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

  const handleNovoEstacionamento = () => {
    navigate("/estacionamentos/novo");
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">
        Bem-vindo, {nomeUsuario || "Proprietário"}!
      </h1>

      <button
        onClick={handleNovoEstacionamento}
        className="btn btn-primary mb-6"
      >
        Cadastrar novo estacionamento
      </button>

      <h2 className="text-xl font-semibold mb-4">Meus Estacionamentos</h2>
      <div className="grid gap-4">
        {estacionamentos.length === 0 ? (
          <p>Nenhum estacionamento cadastrado ainda.</p>
        ) : (
          estacionamentos.map((e) => (
            <div key={e.id} className="border rounded p-4 shadow">
              <h3 className="text-lg font-bold">{e.nome}</h3>
              <p>
                <strong>Endereço:</strong> {formatarEndereco(e.endereco, e.numero)}
              </p>

              <p>
                <strong>Capacidade:</strong> {e.capacidade}
              </p>
              <p>
                <strong>Horário:</strong> {e.horaAbertura} às {e.horaFechamento}
              </p>
              <button
                className="btn btn-primary mt-2"
                onClick={() => handleCadastrarVaga(e.id)}
              >
                Cadastrar Vaga
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default HomeProprietario;
