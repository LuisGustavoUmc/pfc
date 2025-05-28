import React, { useState, useEffect } from "react";

export default function FiltroReservaProprietario({ onBuscar, valorInicial = "" }) {
  const [placa, setPlaca] = useState(valorInicial);

  // Atualiza o estado local se o valorInicial mudar no pai
  useEffect(() => {
    setPlaca(valorInicial);
  }, [valorInicial]);

  const filtrarPlacaInput = (valor) => {
    valor = valor.toUpperCase().replace(/[^A-Z0-9]/g, "");
    const letras = valor.match(/[A-Z]/g) || [];
    const numeros = valor.match(/\d/g) || [];

    if (valor.length > 7) {
      valor = valor.slice(0, 7);
    }

    if (letras.length > 4 && numeros.length < 2) {
      valor = valor.slice(0, -1);
    }

    return valor;
  };

  const handleInputChange = (e) => {
    const valorFiltrado = filtrarPlacaInput(e.target.value);
    setPlaca(valorFiltrado);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onBuscar({ placa });
  };

  return (
    <form onSubmit={handleSubmit} className="mb-4">
      <div className="row g-2 align-items-center">
        <div className="col-9 col-md-10">
          <input
            type="text"
            className="form-control"
            placeholder="Buscar por placa do veículo"
            value={placa}
            onChange={handleInputChange}
            maxLength={7}
          />
        </div>
        <div className="col-3 col-md-2 d-grid">
          <button type="submit" className="btn btn-secondary">
            <i className="bi bi-search me-1"></i> Filtrar
          </button>
        </div>
      </div>
    </form>
  );
}
