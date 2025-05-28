import React, { useEffect, useState } from "react";
import api from "../../services/api";
import Swal from "sweetalert2";
import "./styles.css";
import { formatarEndereco } from "../../utils/Utils";

const ReservaDetalhes = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState("ATIVA");
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const tamanhoPagina = 10; // Número de reservas por página

  useEffect(() => {
    buscarReservas();
  }, [abaAtiva, paginaAtual]);

  const buscarReservas = async () => {
    setLoading(true);
    try {
      const response = await api.get("/api/reservas", {
        params: {
          page: paginaAtual,
          size: tamanhoPagina,
          direction: "asc",
          status: abaAtiva,
        },
      });
      setReservas(response.data.content);
      setTotalPaginas(response.data.totalPages);
    } catch (error) {
      console.error("Erro ao buscar reservas:", error);
      setReservas([]);
      setTotalPaginas(0);
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

  const renderTabs = () => (
    <ul className="nav nav-tabs mb-4">
      {["ATIVA", "FINALIZADA", "CANCELADA"].map((status) => (
        <li className="nav-item" key={status}>
          <button
            className={`nav-link text-dark ${abaAtiva === status ? "active" : ""}`}
            onClick={() => {
              setPaginaAtual(0);
              setAbaAtiva(status);
            }}
            style={{ cursor: "pointer" }}
          >
            {status.charAt(0) + status.slice(1).toLowerCase()}
          </button>
        </li>
      ))}
    </ul>
  );

  const renderPaginacao = () => (
    <div className="d-flex justify-content-between align-items-center mt-4">
      <button
        className="btn btn-outline-primary btn-sm"
        onClick={() => setPaginaAtual((prev) => prev - 1)}
        disabled={paginaAtual === 0}
      >
        Anterior
      </button>
      <span>
        Página {paginaAtual + 1} de {totalPaginas}
      </span>
      <button
        className="btn btn-outline-primary btn-sm"
        onClick={() => setPaginaAtual((prev) => prev + 1)}
        disabled={paginaAtual + 1 >= totalPaginas}
      >
        Próxima
      </button>
    </div>
  );

  const renderReservasAccordion = () => (
    <>
      <div className="accordion" id="accordionReservas">
        {reservas.map((reserva, index) => {
          const duracaoHoras =
            (new Date(reserva.dataHoraFim) - new Date(reserva.dataHoraInicio)) / (1000 * 60 * 60);
          const totalEstimado = reserva.vaga?.preco
            ? (duracaoHoras * reserva.vaga.preco).toFixed(2)
            : "-";

          return (
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
                    {/* Bloco Estacionamento */}
                    <div className="reserva-bloco">
                      <h5>{reserva.estacionamento?.nome || "Estacionamento não informado"}</h5>
                      <p>
                        <strong>Endereço:</strong>{" "}
                        {reserva.estacionamento?.endereco
                          ? formatarEndereco(reserva.estacionamento.endereco)
                          : "-"}
                      </p>
                      <p>
                        <strong>Telefone:</strong> {reserva.estacionamento?.telefone || "-"}
                      </p>
                      <p>
                        <strong>Horário:</strong>{" "}
                        {reserva.estacionamento
                          ? `${reserva.estacionamento.horaAbertura} - ${reserva.estacionamento.horaFechamento}`
                          : "-"}
                      </p>
                    </div>

                    {/* Bloco Vaga */}
                    <div className="reserva-bloco">
                      <p>
                        <strong>Vaga ID:</strong> {reserva.vaga?.id || "-"}
                      </p>
                      <p>
                        <strong>Tipo:</strong> {reserva.vaga?.tipo?.join(", ") || "-"}
                      </p>
                      <p>
                        <strong>Preço/h:</strong>{" "}
                        {reserva.vaga?.preco !== undefined
                          ? `R$ ${reserva.vaga.preco.toFixed(2)}`
                          : "-"}
                      </p>
                      {reserva.dataHoraInicio &&
                        reserva.dataHoraFim &&
                        reserva.vaga?.preco !== undefined && (
                          <>
                            <p>
                              <strong>Duração:</strong> {duracaoHoras.toFixed(2)} hora(s)
                            </p>
                            <p>
                              <strong>Total estimado:</strong> R$ {totalEstimado}
                            </p>
                          </>
                        )}
                    </div>

                    {/* Bloco Dados da Reserva */}
                    <div className="reserva-bloco">
                      <p>
                        <strong>Placa:</strong> {reserva.placaVeiculo}
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
          );
        })}
      </div>

      {totalPaginas > 1 && renderPaginacao()}
    </>
  );

  if (loading) return <p>Carregando reservas...</p>;

  return (
    <div className="container mt-4">
      <h2>Minhas Reservas</h2>
      {renderTabs()}
      {reservas.length === 0 ? (
        <p>Nenhuma reserva {abaAtiva.toLowerCase()} encontrada.</p>
      ) : (
        renderReservasAccordion()
      )}
    </div>
  );
};

export default ReservaDetalhes;
