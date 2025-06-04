import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";
import { toast } from "react-toastify";

const CadastrarVaga = () => {
  const { estacionamentoId } = useParams(); // <- capturando o id da URL
  const [vaga, setVaga] = useState({
    status: "LIVRE",
    tipo: [],
    preco: "",
    estacionamentoId: "",
  });

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

    api
      .post("/api/vagas", vaga)
      .then(() => {
        toast.success("Vaga cadastrada com sucesso!");
        setVaga({
          status: "LIVRE",
          tipo: [],
          preco: "",
          estacionamentoId: estacionamentoId || "",
        });
      })
      .catch((err) => {
        console.error("Erro ao cadastrar vaga:", err);
        const mensagem =
          err.response?.data?.message || "Erro ao cadastrar vaga.";
        toast.error(mensagem);
      });
  };

  return (
    <div className="container py-4" style={{ maxWidth: "600px" }}>
      <h1 className="mb-4 text-center text-dark fs-4">
        <i className="fas fa-parking me-2"></i>Cadastrar Nova Vaga
      </h1>

      <form onSubmit={handleSubmit} className="d-flex flex-column gap-4">
        <div>
          <label htmlFor="preco" className="form-label text-dark small">
            <i className="fas fa-dollar-sign me-2"></i>Preço por hora
          </label>
          <input
            type="number"
            name="preco"
            id="preco"
            value={vaga.preco}
            onChange={handleChange}
            required
            min="0"
            step="0.00"
            className="form-control form-control-lg"
          />
        </div>

        <div>
          <label className="form-label text-dark small">
            <i className="fas fa-list me-2"></i>Tipo de Vaga
          </label>
          <div className="d-flex flex-wrap gap-3">
            {["COBERTA", "DESCOBERTA", "DEFICIENTE", "IDOSO"].map((tipo) => (
              <div key={tipo} className="form-check form-check-inline">
                <input
                  type="checkbox"
                  value={tipo}
                  checked={vaga.tipo.includes(tipo)}
                  onChange={handleTipoChange}
                  className="form-check-input"
                  id={tipo}
                />
                <label className="form-check-label" htmlFor={tipo}>
                  {tipo.charAt(0) + tipo.slice(1).toLowerCase()}
                </label>
              </div>
            ))}
          </div>
        </div>

        <button type="submit" className="btn btn-primary btn-lg w-100 mt-3">
          <i className="fas fa-plus me-2"></i>Cadastrar Vaga
        </button>
      </form>
    </div>
  );
};

export default CadastrarVaga;
