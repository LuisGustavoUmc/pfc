import React, { useState } from "react";
import "./css/filtroEstacionamento.css"

export default function FiltroEstacionamento({ onBuscar }) {
  const [termo, setTermo] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    onBuscar({ termo });
  };

  return (
    <div className="container my-4">
      <form onSubmit={handleSubmit} className="mx-auto w-100 w-md-75 w-lg-50">
        <div className="row g-2 align-items-center">
          <div className="col-9">
            <input
              type="text"
              className="form-control form-control-dark"
              placeholder="Buscar por cidade, bairro ou nome do estacionamento"
              value={termo}
              onChange={(e) => setTermo(e.target.value)}
            />
          </div>
          <div className="col-3 d-grid">
            <button type="submit" className="btn btn-primary">
              <i className="bi bi-search me-1"></i> Buscar
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
