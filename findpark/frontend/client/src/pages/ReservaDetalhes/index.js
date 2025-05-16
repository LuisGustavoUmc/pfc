import React, { useEffect, useState } from "react";
import api from "../../services/api";

const ReservaDetalhes = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const buscarReservas = async () => {
      try {
        const response = await api.get("/api/reservas");
        setReservas(response.data);
      } catch (error) {
        console.error("Erro ao buscar reservas:", error);
        alert("Erro ao carregar reservas. Verifique se você está logado.");
      } finally {
        setLoading(false);
      }
    };

    buscarReservas();
  }, []);

  if (loading) return <p>Carregando reservas...</p>;

  return (
    <div className="container mt-4">
      <h2>Minhas Reservas</h2>
      {reservas.length === 0 ? (
        <p>Você ainda não possui reservas.</p>
      ) : (
        <div className="list-group">
          {reservas.map((reserva) => (
            <div key={reserva.id} className="list-group-item">
              <h5>Estacionamento ID: {reserva.estacionamentoId}</h5>
              <p><strong>Vaga:</strong> {reserva.vagaId}</p>
              <p><strong>Placa:</strong> {reserva.placaVeiculo}</p>
              <p><strong>Início:</strong> {new Date(reserva.dataHoraInicio).toLocaleString()}</p>
              <p><strong>Fim:</strong> {new Date(reserva.dataHoraFim).toLocaleString()}</p>
              <p><strong>Status:</strong> {reserva.status}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ReservaDetalhes;
