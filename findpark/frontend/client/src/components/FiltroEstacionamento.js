import React, { useState } from "react";

export default function FiltroEstacionamento({ onBuscar }) {
  const [termo, setTermo] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    onBuscar({ termo });
  };

  return (
    <form onSubmit={handleSubmit} className="mb-4">
      <div className="row g-2 align-items-center">
        <div className="col-md-10 col-sm-9">
          <input
            type="text"
            className="form-control"
            placeholder="Buscar por cidade, bairro ou nome do estacionamento"
            value={termo}
            onChange={(e) => setTermo(e.target.value)}
          />
        </div>
        <div className="col-md-2 col-sm-3 d-grid">
          <button type="submit" className="btn btn-primary">
            <i className="bi bi-search me-1"></i> Buscar
          </button>
        </div>
      </div>
    </form>
  );
}
