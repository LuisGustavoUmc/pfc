import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";
import { toast } from "react-toastify";

const CadastrarOuEditarVaga = () => {
  const { estacionamentoId, vagaId } = useParams(); // <- pega ambos os IDs
  const [vaga, setVaga] = useState({
    status: "LIVRE",
    tipo: [],
    preco: "",
    estacionamentoId: "",
  });

  const navigate = useNavigate();
  const editando = Boolean(vagaId);

  // Carregar dados da vaga para edição
  useEffect(() => {
    if (editando) {
      api
        .get(`/api/vagas/${vagaId}`)
        .then((res) => {
          const dados = res.data;
          const tipos =
            typeof dados.tipo === "string" ? dados.tipo.split(",") : dados.tipo;
          setVaga({
            ...dados,
            tipo: Array.isArray(tipos)
              ? tipos.map((t) => t.trim().toUpperCase())
              : [],
          });
        })
        .catch((err) => {
          console.error("Erro ao carregar vaga:", err);
          toast.error("Erro ao carregar dados da vaga.");
        });
    } else if (estacionamentoId) {
      setVaga((prev) => ({ ...prev, estacionamentoId }));
    }
  }, [vagaId, estacionamentoId, editando]);

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

    const metodo = editando ? api.put : api.post;
    const url = editando ? `/api/vagas/${vagaId}` : "/api/vagas";

    metodo(url, vaga)
      .then(() => {
        toast.success(
          `Vaga ${editando ? "atualizada" : "cadastrada"} com sucesso!`
        );
        navigate("/home-proprietario");
      })
      .catch((err) => {
        console.error("Erro ao salvar vaga:", err);
        const msg = err.response?.data?.message || "Erro ao salvar vaga.";
        toast.error(msg);
      });
  };

  return (
    <div className="container py-4" style={{ maxWidth: "600px" }}>
      <h1 className="mb-4 text-center text-dark fs-4">
        <i className="fas fa-parking me-2"></i>
        {editando ? "Editar Vaga" : "Cadastrar Nova Vaga"}
      </h1>

      <form onSubmit={handleSubmit} className="d-flex flex-column gap-4">
        <div>
          <label htmlFor="preco" className="form-label text-dark small">
            <i className="fas fa-dollar-sign me-2"></i>Preço
          </label>
          <input
            type="number"
            name="preco"
            id="preco"
            value={vaga.preco}
            onChange={handleChange}
            required
            min="0"
            step="0.01"
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
                  checked={Array.isArray(vaga.tipo) && vaga.tipo.includes(tipo)}
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

        <div>
          <label htmlFor="status" className="form-label text-dark small">
            <i className="fas fa-toggle-on me-2"></i>Status
          </label>
          <select
            name="status"
            id="status"
            value={vaga.status}
            onChange={handleChange}
            required
            className="form-select form-select-md"
          >
            <option value="LIVRE">LIVRE</option>
            <option value="OCUPADA">OCUPADA</option>
          </select>
        </div>

        <button type="submit" className="btn btn-primary btn-lg w-100 mt-3">
          <i className={`fas fa-${editando ? "save" : "plus"} me-2`}></i>
          {editando ? "Salvar Alterações" : "Cadastrar Vaga"}
        </button>
      </form>
    </div>
  );
};

export default CadastrarOuEditarVaga;
