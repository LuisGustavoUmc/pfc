import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";

const CadastrarVaga = () => {
  const { estacionamentoId } = useParams(); // <- capturando o id da URL
  const [vaga, setVaga] = useState({
    status: "LIVRE",
    tipo: [],
    preco: 0,
    estacionamentoId: "",
  });

  const navigate = useNavigate();

  // Atualiza o estado da vaga com o id do estacionamento
  useEffect(() => {
    if (estacionamentoId) {
      setVaga((prev) => ({ ...prev, estacionamentoId }));
    }
  }, [estacionamentoId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setVaga({ ...vaga, [name]: value });
  };

  const handleTipoChange = (e) => {
    const { value, checked } = e.target;
    const tiposAtualizados = checked
      ? [...vaga.tipo, value]
      : vaga.tipo.filter((t) => t !== value);

    setVaga({ ...vaga, tipo: tiposAtualizados });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Envia os dados da vaga para o backend
    api
      .post("/api/vagas", vaga)
      .then(() => {
        alert("Vaga cadastrada com sucesso!");
        navigate("/home-proprietario"); // Redireciona para a home do proprietário
      })
      .catch((err) => {
        console.error("Erro ao cadastrar vaga:", err);
        const mensagem =
          err.response?.data?.message || "Erro ao cadastrar vaga.";
        alert(mensagem);
      });
  };

  return (
    <div className="p-6 max-w-xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">Cadastrar Nova Vaga</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <label className="block">
          Preço:
          <input
            type="number"
            name="preco"
            value={vaga.preco}
            onChange={handleChange}
            required
            min="0"
            step="0.01"
            className="w-full border p-2 rounded mt-1"
          />
        </label>

        <div>
          <label className="block mb-1">Tipo de Vaga:</label>
          <div className="d-flex flex-wrap gap-3">
            {[
              "COMUM",
              "DEFICIENTE",
              "IDOSO",
              "ELETRICO",
              "MOTO",
              "COBERTA",
              "DESCOBERTA",
              "RAPIDA",
            ].map((tipo) => (
              <label key={tipo} className="form-check-label me-3">
                <input
                  type="checkbox"
                  value={tipo}
                  checked={vaga.tipo.includes(tipo)}
                  onChange={handleTipoChange}
                  className="form-check-input me-1"
                />
                {tipo.charAt(0) + tipo.slice(1).toLowerCase()}
              </label>
            ))}
          </div>
        </div>

        <label className="block">
          Status:
          <select
            name="status"
            value={vaga.status}
            onChange={handleChange}
            required
            className="w-full border p-2 rounded mt-1"
          >
            <option value="LIVRE">LIVRE</option>
            <option value="OCUPADA">OCUPADA</option>
            <option value="RESERVADA">RESERVADA</option>
            <option value="BLOQUEADA">BLOQUEADA</option>
          </select>
        </label>

        <button type="submit" className="btn btn-primary">
          Cadastrar Vaga
        </button>
      </form>
    </div>
  );
};

export default CadastrarVaga;
