import React, { useState } from "react";
import "./css/filtroEstacionamento.css";

export default function FiltroEstacionamento({ onBuscar }) {
  const [termo, setTermo] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    onBuscar({ termo });
  };

  const handleLimpar = () => {
    setTermo("");
    onBuscar({ termo: "" });
  };

  return (
    <div className="container my-4">
      <form onSubmit={handleSubmit} className="mx-auto w-100 w-md-75 w-lg-50">
        <div className="row g-2 align-items-center">
          <div className="col-12 col-md-9">
            <input
              type="text"
              className="form-control form-control-dark"
              placeholder="Buscar por cidade, bairro ou estacionamento"
              value={termo}
              onChange={(e) => setTermo(e.target.value)}
            />
          </div>
          <div className="col-6 col-md-2 d-grid">
            <button type="submit" className="btn btn-primary w-100">
              <i className="fas fa-search me-1"></i>
            </button>
          </div>
          <div className="col-6 col-md-1 d-grid">
            <button
              type="button"
              className="btn btn-outline-secondary w-100"
              onClick={handleLimpar}
              title="Limpar filtro"
            >
              <i className="fas fa-times"></i>
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
