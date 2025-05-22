import React, { useEffect, useState } from "react";
import api from "../../services/api";
import Swal from "sweetalert2";
import "./styles.css";
import { formatarEndereco } from "../../utils/Utils";

const ReservaDetalhes = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState("ATIVA");

  useEffect(() => {
    buscarReservas();
  }, []);

  const buscarReservas = async () => {
    try {
      const response = await api.get("/api/reservas");
      setReservas(response.data);
    } catch (error) {
      console.error("Erro ao buscar reservas:", error);
    } finally {
      setLoading(false);
    }
  };

  const cancelarReserva = async (id) => {
    const confirm = await Swal.fire({
      title: "Cancelar reserva?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#aaa",
      confirmButtonText: "Sim, cancelar",
      cancelButtonText: "Não",
    });

    if (confirm.isConfirmed) {
      try {
        await api.delete(`/api/reservas/${id}`);
        Swal.fire({
          toast: true,
          position: "top-end",
          icon: "success",
          title: "Reserva cancelada com sucesso",
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true,
        });
        buscarReservas();
      } catch (error) {
        console.error("Erro ao cancelar reserva:", error);
        Swal.fire({
          toast: true,
          position: "top-end",
          icon: "error",
          title: "Erro ao cancelar reserva",
          showConfirmButton: false,
          timer: 3000,
        });
      }
    }
  };

  const reservasFiltradas = reservas.filter(
    (reserva) => reserva.status === abaAtiva
  );

  const renderTabs = () => (
    <ul className="nav nav-tabs mb-4">
      {["ATIVA", "FINALIZADA", "CANCELADA"].map((status) => (
        <li className="nav-item" key={status}>
          <button
            className={`nav-link ${abaAtiva === status ? "active" : ""}`}
            aria-current={abaAtiva === status ? "page" : undefined}
            onClick={() => setAbaAtiva(status)}
            style={{ cursor: "pointer" }}
          >
            {status.charAt(0) + status.slice(1).toLowerCase()}
          </button>
        </li>
      ))}
    </ul>
  );

  const renderReservasAccordion = () => (
    <div className="accordion" id="accordionReservas">
      {reservasFiltradas.map((reserva, index) => (
        <div className="accordion-item" key={reserva.id}>
          <h2 className="accordion-header" id={`heading-${index}`}>
            <button
              className="accordion-button collapsed"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target={`#collapse-${index}`}
              aria-expanded="false"
              aria-controls={`collapse-${index}`}
            >
              {reserva.estacionamento?.nome || "Reserva"} -{" "}
              {new Date(reserva.dataHoraInicio).toLocaleDateString()}
            </button>
          </h2>
          <div
            id={`collapse-${index}`}
            className="accordion-collapse collapse"
            aria-labelledby={`heading-${index}`}
            data-bs-parent="#accordionReservas"
          >
            <div className="accordion-body">
              <div className="reserva-card" data-status={reserva.status}>
                <div className="reserva-bloco">
                  <h5>
                    {reserva.estacionamento?.nome ||
                      "Estacionamento não informado"}
                  </h5>
                  <p>
                    <strong>Endereço:</strong>{" "}
                    {reserva.estacionamento?.endereco
                      ? formatarEndereco(reserva.estacionamento.endereco)
                      : "-"}
                  </p>
                  <p>
                    <strong>Telefone:</strong>{" "}
                    {reserva.estacionamento?.telefone || "-"}
                  </p>
                  <p>
                    <strong>Horário de funcionamento:</strong>
                    {reserva.estacionamento
                      ? ` ${reserva.estacionamento.horaAbertura} - ${reserva.estacionamento.horaFechamento}`
                      : " -"}
                  </p>
                </div>

                <div className="reserva-bloco">
                  <p>
                    <strong>Vaga ID:</strong> {reserva.vaga?.id || "-"}
                  </p>
                  <p>
                    <strong>Tipo de vaga:</strong>{" "}
                    {reserva.vaga?.tipo ? reserva.vaga.tipo.join(", ") : "-"}
                  </p>
                  <p>
                    <strong>Preço por hora:</strong>
                    {reserva.vaga?.preco !== undefined
                      ? ` R$ ${reserva.vaga.preco.toFixed(2)}`
                      : " -"}
                  </p>

                  {reserva.dataHoraInicio &&
                    reserva.dataHoraFim &&
                    reserva.vaga?.preco !== undefined && (
                      <>
                        <p>
                          <strong>Duração estimada:</strong>{" "}
                          {(new Date(reserva.dataHoraFim) -
                            new Date(reserva.dataHoraInicio)) /
                            (1000 * 60 * 60)}{" "}
                          hora(s)
                        </p>
                        <p>
                          <strong>Total estimado:</strong> R${" "}
                          {(
                            ((new Date(reserva.dataHoraFim) -
                              new Date(reserva.dataHoraInicio)) /
                              (1000 * 60 * 60)) *
                            reserva.vaga.preco
                          ).toFixed(2)}
                        </p>
                      </>
                    )}
                </div>

                <div className="reserva-bloco">
                  <p>
                    <strong>Placa do veículo:</strong> {reserva.placaVeiculo}
                  </p>
                  <p>
                    <strong>Início:</strong>{" "}
                    {new Date(reserva.dataHoraInicio).toLocaleString()}
                  </p>
                  <p>
                    <strong>Fim:</strong>{" "}
                    {new Date(reserva.dataHoraFim).toLocaleString()}
                  </p>
                  <p>
                    <strong>Status:</strong> {reserva.status}
                  </p>

                  {reserva.status === "ATIVA" && (
                    <button
                      className="btn btn-danger btn-sm mt-2"
                      onClick={() => cancelarReserva(reserva.id)}
                    >
                      Cancelar Reserva
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );

  if (loading) return <p>Carregando reservas...</p>;

  return (
    <div className="container mt-4">
      <h2>Minhas Reservas</h2>
      {renderTabs()}

      {reservasFiltradas.length === 0 ? (
        <p>Nenhuma reserva {abaAtiva.toLowerCase()} encontrada.</p>
      ) : (
        renderReservasAccordion()
      )}
    </div>
  );
};

export default ReservaDetalhes;
