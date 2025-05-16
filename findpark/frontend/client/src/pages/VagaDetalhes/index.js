import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";
import Swal from "sweetalert2";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./vagaDetalhes.css";

export default function VagaDetalhes() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [vaga, setVaga] = useState(null);
  const [entrada, setEntrada] = useState("");
  const [saida, setSaida] = useState("");
  const [placas, setPlacas] = useState([]);
  const [placaSelecionada, setPlacaSelecionada] = useState("");
  const [mensagem, setMensagem] = useState("");

  const agora = new Date().toISOString().slice(0, 16);

  const anoValido = (dataStr) => /^\d{4}$/.test(dataStr?.substring(0, 4));

  const handleConfirmarClick = () => {
    setMensagem("");

    if (!entrada || !saida || !placaSelecionada) {
      toast.error("Preencha todas as informações e selecione uma placa.");
      return;
    }

    if (!anoValido(entrada) || !anoValido(saida)) {
      toast.error("Data inválida. Ano deve ter 4 dígitos.");
      return;
    }

    const dataEntrada = new Date(entrada);
    const dataSaida = new Date(saida);
    const agoraDate = new Date();

    if (dataEntrada < agoraDate) {
      toast.error("Data/hora de entrada não pode ser no passado.");
      return;
    }

    if (dataSaida <= dataEntrada) {
      toast.error("Data/hora de saída deve ser após a entrada.");
      return;
    }

    const duracaoHoras = Math.ceil(
      (dataSaida - dataEntrada) / (1000 * 60 * 60)
    );
    const total = duracaoHoras * vaga.preco;

    Swal.fire({
      title: "Confirmação de Reserva",
      html: `
        <h5>Resumo da Reserva</h5>
        <p><strong>Estacionamento:</strong> ${vaga.estacionamento.nome}</p>
        <p><strong>Endereço:</strong> ${vaga.estacionamento.endereco}</p>
        <p><strong>Funcionamento:</strong> ${
          vaga.estacionamento.horaAbertura && vaga.estacionamento.horaFechamento
            ? `${vaga.estacionamento.horaAbertura.slice(0, 5)} - ${vaga.estacionamento.horaFechamento.slice(0, 5)}`
            : "Não informado"
        }</p>
        <p><strong>Vaga ID:</strong>${vaga.id}</p>
        <p><strong>Tipo:</strong> ${vaga.tipo.join(", ")}</p>
        <p><strong>Placa:</strong> ${placaSelecionada}</p>
        <p><strong>Entrada:</strong> ${entrada.replace("T", " ")}</p>
        <p><strong>Saída:</strong> ${saida.replace("T", " ")}</p>
        <p><strong>Duração:</strong> ${duracaoHoras} hora(s)</p>
        <p><strong>Preço por hora:</strong> R$ ${vaga.preco.toFixed(2)}</p>
        <hr>
        <p><strong>Total:</strong> R$ ${total.toFixed(2)}</p>
      `,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Confirmar Reserva",
      cancelButtonText: "Cancelar",
      customClass: {
        popup: "swal-custom",
        confirmButton: "swal-button-confirm",
        cancelButton: "swal-button-cancel",
      },
    }).then((result) => {
      if (result.isConfirmed) {
        confirmarReserva();
      }
    });
  };

  const confirmarReserva = () => {
    const token = localStorage.getItem("accessToken");

    api
      .post(
        "/api/reservas",
        {
          vagaId: vaga.id,
          estacionamentoId: vaga.estacionamento.id,
          placaVeiculo: placaSelecionada,
          dataHoraInicio: new Date(entrada).toISOString(),
          dataHoraFim: new Date(saida).toISOString(),
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      )
      .then(() => {
        toast.success("Reserva confirmada com sucesso!");
      })
      .catch((err) => {
        toast.error("Erro ao confirmar reserva.");
      });
  };

  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    const carregarVaga = async () => {
      try {
        const response = await api.get(`/api/vagas/detalhes/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setVaga(response.data);
      } catch (err) {
        console.error("Erro ao carregar vaga", err);
      }
    };

    const carregarPlacas = async () => {
      try {
        const response = await api.get("/api/clientes/placas", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPlacas(response.data);
      } catch (err) {
        console.error("Erro ao carregar placas", err);
      }
    };

    carregarVaga();
    carregarPlacas();
  }, [id]);

  if (!vaga) return <div>Carregando detalhes da vaga...</div>;

  const estacionamento = vaga.estacionamento;

  return (
    <div className="container mt-4">
      <h3 className="titulo">{estacionamento.nome}</h3>
      <div className="vaga-detalhes-grid">
        <p>
          <strong>ID da Vaga:</strong> {vaga.id}
        </p>
        <p>
          <strong>Tipo da Vaga:</strong> {vaga.tipo.join(", ")}
        </p>
        <p>
          <strong>Endereço:</strong> {estacionamento.endereco}
        </p>
        <p>
          <strong>Funcionamento:</strong>{" "}
          {estacionamento.horaAbertura && estacionamento.horaFechamento
            ? `${estacionamento.horaAbertura.slice(0, 5)} - ${estacionamento.horaFechamento.slice(0, 5)}`
            : "Não informado"}
        </p>

        <p>
          <strong>Status:</strong> {vaga.status}
        </p>
        <p>
          <strong>Preço por hora:</strong>{" "}
          {Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL",
          }).format(vaga.preco)}
        </p>
      </div>

      <div className="input-linha">
        <label className="form-label">Data e Hora de Entrada:</label>
        <input
          type="datetime-local"
          className="form-control"
          min={agora}
          value={entrada}
          onChange={(e) => setEntrada(e.target.value)}
        />
      </div>

      <div className="input-linha">
        <label className="form-label">Data e Hora de Saída:</label>
        <input
          type="datetime-local"
          className="form-control"
          min={entrada || agora}
          value={saida}
          onChange={(e) => setSaida(e.target.value)}
        />
      </div>

      <div className="input-linha">
        <label className="form-label">Selecione a Placa:</label>
        <select
          className="form-select"
          value={placaSelecionada}
          onChange={(e) => setPlacaSelecionada(e.target.value)}
        >
          <option value="">-- Selecione uma placa --</option>
          {placas.map((placa, index) => (
            <option key={index} value={placa}>
              {placa}
            </option>
          ))}
        </select>
      </div>

      {mensagem && <div className="text-success mt-2">{mensagem}</div>}

      <div className="actions">
        <button className="btn btn-azul" onClick={handleConfirmarClick}>
          Confirmar Reserva
        </button>
        <button
          className="btn btn-secondary"
          onClick={() => navigate("/home-cliente")}
        >
          Voltar para Home
        </button>
      </div>
    </div>
  );
}
