import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";
import Swal from "sweetalert2";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./vagaDetalhes.css";
import {
  formatarEndereco,
  gerarHorariosEntre,
  validarHorarioDentroFuncionamento,
  formatarDataHora,
  calcularDuracaoHoras,
  calcularTotal,
  formatarPreco,
  gerarResumoReservaHTML,
} from "../../utils/Utils";

import DatePicker, { registerLocale } from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import ptBR from "date-fns/locale/pt-BR";

registerLocale("pt-BR", ptBR);

export default function VagaDetalhes() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [vaga, setVaga] = useState(null);
  const [loading, setLoading] = useState(true);
  const [erroCarregamento, setErroCarregamento] = useState(false);
  const [estacionamento, setEstacionamento] = useState(null);
  const [dataEntrada, setDataEntrada] = useState(null);
  const [horaEntrada, setHoraEntrada] = useState("");
  const [dataSaida, setDataSaida] = useState(null);
  const [horaSaida, setHoraSaida] = useState("");

  const [placas, setPlacas] = useState([]);
  const [placaSelecionada, setPlacaSelecionada] = useState("");

  const [mensagem, setMensagem] = useState("");

  // Resetar data e hora de saída se dataEntrada for alterada e saída ficar inválida
  useEffect(() => {
    if (dataSaida && dataEntrada && dataSaida < dataEntrada) {
      setDataSaida(null);
      setHoraSaida("");
    }
  }, [dataEntrada, dataSaida]);

  const handleConfirmarClick = () => {
    setMensagem("");

    if (!vaga || !vaga.estacionamento) {
      toast.error("Informações da vaga ainda não carregadas.");
      return;
    }

    const { horaAbertura, horaFechamento } = vaga.estacionamento;

    // Validar horários dentro do funcionamento
    if (
      !validarHorarioDentroFuncionamento(
        dataEntrada,
        horaEntrada,
        horaAbertura,
        horaFechamento
      )
    ) {
      toast.error(
        "Horário de entrada fora do funcionamento do estacionamento."
      );
      return;
    }

    if (
      !validarHorarioDentroFuncionamento(
        dataSaida,
        horaSaida,
        horaAbertura,
        horaFechamento
      )
    ) {
      toast.error("Horário de saída fora do funcionamento do estacionamento.");
      return;
    }

    if (
      !dataEntrada ||
      !horaEntrada ||
      !dataSaida ||
      !horaSaida ||
      !placaSelecionada
    ) {
      toast.error("Preencha todas as informações e selecione uma placa.");
      return;
    }

    const entrada = new Date(
      dataEntrada.getFullYear(),
      dataEntrada.getMonth(),
      dataEntrada.getDate(),
      parseInt(horaEntrada.split(":")[0]),
      parseInt(horaEntrada.split(":")[1])
    );

    const saida = new Date(
      dataSaida.getFullYear(),
      dataSaida.getMonth(),
      dataSaida.getDate(),
      parseInt(horaSaida.split(":")[0]),
      parseInt(horaSaida.split(":")[1])
    );

    const agora = new Date();

    if (entrada < agora) {
      toast.error("Data/hora de entrada não pode ser no passado.");
      return;
    }

    if (saida <= entrada) {
      toast.error("Data/hora de saída deve ser após a entrada.");
      return;
    }

    const duracaoHoras = calcularDuracaoHoras(entrada, saida);
    const total = calcularTotal(duracaoHoras, vaga.preco);
    const precoHora = formatarPreco(vaga.preco);
    const totalFormatado = formatarPreco(total);
    const enderecoFormatado = formatarEndereco(vaga.estacionamento.endereco);
    const funcionamento =
      vaga.estacionamento.horaAbertura && vaga.estacionamento.horaFechamento
        ? `${vaga.estacionamento.horaAbertura.slice(0, 5)} - ${vaga.estacionamento.horaFechamento.slice(0, 5)}`
        : "Não informado";

    const htmlResumo = gerarResumoReservaHTML({
      nomeEstacionamento: vaga.estacionamento.nome,
      endereco: enderecoFormatado,
      funcionamento,
      telefone: vaga.estacionamento.telefone,
      idVaga: vaga.id,
      tipoVaga: vaga.tipo.join(", "),
      placa: placaSelecionada,
      entrada: `${dataEntrada.toLocaleDateString()} ${horaEntrada}`,
      saida: `${dataSaida.toLocaleDateString()} ${horaSaida}`,
      duracaoHoras,
      precoHora,
      total: totalFormatado,
    });

    Swal.fire({
      title: "Confirmação de Reserva",
      html: htmlResumo,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Confirmar Reserva",
      cancelButtonText: "Cancelar",
    }).then((result) => {
      if (result.isConfirmed) {
        const entrada = new Date(
          dataEntrada.getFullYear(),
          dataEntrada.getMonth(),
          dataEntrada.getDate(),
          parseInt(horaEntrada.split(":")[0]),
          parseInt(horaEntrada.split(":")[1])
        );

        const saida = new Date(
          dataSaida.getFullYear(),
          dataSaida.getMonth(),
          dataSaida.getDate(),
          parseInt(horaSaida.split(":")[0]),
          parseInt(horaSaida.split(":")[1])
        );

        const entradaISO = formatarDataHora(entrada);
        const saidaISO = formatarDataHora(saida);

        confirmarReserva(entradaISO, saidaISO);
      }
    });
  };

  const confirmarReserva = (entradaISO, saidaISO) => {
    const token = localStorage.getItem("accessToken");

    api
      .post(
        "/api/reservas",
        {
          vagaId: vaga.id,
          estacionamentoId: vaga.estacionamento.id,
          placaVeiculo: placaSelecionada,
          dataHoraInicio: entradaISO,
          dataHoraFim: saidaISO,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      .then(() => {
        toast.success("Reserva confirmada com sucesso!");
        navigate("/home-cliente");
      })
      .catch((error) => {
        const message = error?.response?.data?.mensagem;

        if (error.response?.status === 409) {
          toast.warn(
            message ||
              "Já existe uma reserva para essa vaga ou placa no período selecionado."
          );
        } else {
          toast.error(message || "Erro ao criar reserva.");
        }
      });
  };

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setLoading(true);
    setErroCarregamento(false);

    const carregarVaga = async () => {
      try {
        const response = await api.get(`/api/vagas/detalhes/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const vagaData = response.data;
        setVaga(vagaData);

        if (vagaData.estacionamentoId) {
          const estResponse = await api.get(
            `/api/estacionamentos/${vagaData.estacionamentoId}`,
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          );
          setEstacionamento(estResponse.data);
        }
      } catch (err) {
        console.error("Erro ao carregar vaga ou estacionamento", err);
        setErroCarregamento(true);
      } finally {
        setLoading(false);
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

  if (loading) return <div>Carregando detalhes da vaga...</div>;
  if (erroCarregamento)
    return (
      <div className="alert alert-danger">
        Erro ao carregar os dados da vaga. Tente recarregar a página.
      </div>
    );

  if (!vaga) return <div>Carregando...</div>;

  const hoje = new Date();
  console.log("Estacionamento recebido:", vaga.estacionamento);

  if (vaga && vaga.estacionamento) {
    console.log("horaAbertura:", vaga.estacionamento.horaAbertura);
    console.log("horaFechamento:", vaga.estacionamento.horaFechamento);
  }

  return (
    <div className="container my-5 vaga-detalhes-container">
      <h1 className="mb-4">Detalhes da Vaga</h1>

      <div className="card p-4 mb-5">
        <p>
          <strong>Estacionamento:</strong> {vaga.estacionamento.nome}
        </p>
        <p>
          <strong>Endereço:</strong>{" "}
          {formatarEndereco(vaga.estacionamento.endereco)}
        </p>

        <p>
          <strong>Funcionamento:</strong>{" "}
          {vaga.estacionamento.horaAbertura &&
          vaga.estacionamento.horaFechamento
            ? `${vaga.estacionamento.horaAbertura.slice(0, 5)} - ${vaga.estacionamento.horaFechamento.slice(0, 5)}`
            : "Horário não informado"}
        </p>

        <p>
          <strong>Telefone:</strong> {vaga.estacionamento.telefone}
        </p>
        <p>
          <strong>Preço por hora:</strong> R$ {vaga.preco.toFixed(2)}
        </p>
        <p>
          <strong>Tipo de vaga:</strong> {vaga.tipo.join(", ")}
        </p>
      </div>

      <h2 className="mb-4">Fazer Reserva</h2>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          handleConfirmarClick();
        }}
      >
        <div className="row mb-3">
          <div className="col-md-6">
            <label className="form-label">Data de Entrada:</label>
            <DatePicker
              selected={dataEntrada}
              onChange={(date) => setDataEntrada(date)}
              locale="pt-BR"
              dateFormat="dd/MM/yyyy"
              minDate={hoje}
              placeholderText="Selecione a data"
              className="form-control"
              required
            />
          </div>
          <div className="col-md-6">
            <label className="form-label">Hora de Entrada:</label>
            <select
              value={horaEntrada}
              onChange={(e) => setHoraEntrada(e.target.value)}
              className="form-control"
              required
              disabled={
                !dataEntrada ||
                !vaga.estacionamento.horaAbertura ||
                !vaga.estacionamento.horaFechamento
              }
            >
              <option value="">Selecione a hora</option>
              {dataEntrada &&
                vaga.estacionamento &&
                vaga.estacionamento.horaAbertura &&
                vaga.estacionamento.horaFechamento &&
                gerarHorariosEntre(
                  vaga.estacionamento.horaAbertura.slice(0, 5),
                  vaga.estacionamento.horaFechamento.slice(0, 5)
                ).map((hora) => (
                  <option key={hora} value={hora}>
                    {hora}
                  </option>
                ))}
            </select>
          </div>
        </div>

        <div className="row mb-3">
          <div className="col-md-6">
            <label className="form-label">Data de Saída:</label>
            <DatePicker
              selected={dataSaida}
              onChange={(date) => setDataSaida(date)}
              locale="pt-BR"
              dateFormat="dd/MM/yyyy"
              minDate={dataEntrada || hoje}
              placeholderText="Selecione a data"
              className="form-control"
              required
              disabled={!dataEntrada || !horaEntrada}
            />
          </div>
          <div className="col-md-6">
            <label className="form-label">Hora de Saída:</label>
            <select
              value={horaSaida}
              onChange={(e) => setHoraSaida(e.target.value)}
              className="form-control"
              required
              disabled={
                !dataSaida ||
                !horaEntrada ||
                !vaga.estacionamento.horaAbertura ||
                !vaga.estacionamento.horaFechamento
              }
            >
              <option value="">Selecione a hora</option>
              {dataSaida &&
                vaga.estacionamento &&
                vaga.estacionamento.horaAbertura &&
                vaga.estacionamento.horaFechamento &&
                gerarHorariosEntre(
                  vaga.estacionamento.horaAbertura.slice(0, 5),
                  vaga.estacionamento.horaFechamento.slice(0, 5)
                ).map((hora) => (
                  <option key={hora} value={hora}>
                    {hora}
                  </option>
                ))}
            </select>
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">Placa do Veículo:</label>
          <select
            value={placaSelecionada}
            onChange={(e) => setPlacaSelecionada(e.target.value)}
            className="form-control"
            required
          >
            <option value="">Selecione a placa</option>
            {Array.isArray(placas) &&
              placas.map((placa) => (
                <option key={placa} value={placa}>
                  {placa}
                </option>
              ))}
          </select>
        </div>

        {mensagem && <div className="alert alert-danger">{mensagem}</div>}

        <button type="submit" className="btn btn-success w-100">
          Confirmar Reserva
        </button>
      </form>
    </div>
  );
}
