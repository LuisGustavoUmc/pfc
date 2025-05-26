// src/utils/formatarEndereco.js

/**
 * Formata o endereço completo com número manual.
 *
 * @param {Object} endereco - Objeto de endereço retornado pela API de CEP.
 * @param {string | number} [numero] - Número informado manualmente pelo usuário (opcional).
 * @returns {string} Endereço formatado para exibição.
 */
export const formatarEndereco = (endereco) => {
  if (!endereco || !endereco.logradouro) return "";
  const numeroStr = endereco.numero ? `, ${endereco.numero}` : "";
  return `${endereco.logradouro}${numeroStr}, ${endereco.bairro} - ${endereco.localidade}/${endereco.uf}`;
};

// Gera lista de horários em intervalos de 30 minutos (00:00 a 23:30)
export const gerarHorariosEntre = (horaAbertura, horaFechamento) => {
  const horarios = [];

  const [ha, ma] = horaAbertura.split(":").map(Number);
  const [hf, mf] = horaFechamento.split(":").map(Number);

  if (
    isNaN(ha) || isNaN(ma) ||
    isNaN(hf) || isNaN(mf)
  ) {
    console.error("Formato de hora inválido:", horaAbertura, horaFechamento);
    return [];
  }

  let totalMinutos = ha * 60 + ma;
  const fimMinutos = hf * 60 + mf;

  while (totalMinutos < fimMinutos) {
    const horas = String(Math.floor(totalMinutos / 60)).padStart(2, "0");
    const minutos = String(totalMinutos % 60).padStart(2, "0");
    horarios.push(`${horas}:${minutos}`);
    totalMinutos += 30;
  }

  return horarios;
};

export const validarHorarioDentroFuncionamento = (
  data,
  hora,
  abertura,
  fechamento
) => {
  if (!data || !hora) return false;

  const [h, m] = hora.split(":").map(Number);
  const dataComHora = new Date(data);
  dataComHora.setHours(h, m, 0, 0);

  const [ha, ma] = abertura.split(":").map(Number);
  const [hf, mf] = fechamento.split(":").map(Number);

  const inicioFuncionamento = new Date(data);
  inicioFuncionamento.setHours(ha, ma, 0, 0);

  const fimFuncionamento = new Date(data);
  fimFuncionamento.setHours(hf, mf, 0, 0);

  return (
    dataComHora >= inicioFuncionamento && dataComHora <= fimFuncionamento
  );
};

export const formatarDataHora = (data) => {
  return `${data.getFullYear()}-${(data.getMonth() + 1)
    .toString()
    .padStart(2, "0")}-${data.getDate().toString().padStart(2, "0")}T${data
    .getHours()
    .toString()
    .padStart(2, "0")}:${data.getMinutes().toString().padStart(2, "0")}:00`;
};

export const calcularDuracaoHoras = (entrada, saida) => {
  return (saida - entrada) / (1000 * 60 * 60);
};

export const calcularTotal = (duracaoHoras, precoPorHora) => {
  return duracaoHoras * precoPorHora;
};

export const formatarPreco = (valor) => {
  return `R$ ${valor.toFixed(2)}`;
};

export const gerarResumoReservaHTML = ({
  nomeEstacionamento,
  endereco,
  funcionamento,
  telefone,
  idVaga,
  tipoVaga,
  placa,
  entrada,
  saida,
  duracaoHoras,
  precoHora,
  total,
}) => {
  return `
    <h5>Resumo da Reserva</h5>
    <p><strong>Estacionamento:</strong> ${nomeEstacionamento}</p>
    <p><strong>Endereço:</strong> ${endereco}</p>
    <p><strong>Funcionamento:</strong> ${funcionamento}</p>
    <p><strong>Telefone:</strong> ${telefone}</p>
    <p><strong>Vaga ID:</strong> ${idVaga}</p>
    <p><strong>Tipo:</strong> ${tipoVaga}</p>
    <p><strong>Placa:</strong> ${placa}</p>
    <p><strong>Entrada:</strong> ${entrada}</p>
    <p><strong>Saída:</strong> ${saida}</p>
    <p><strong>Duração:</strong> ${duracaoHoras} hora(s)</p>
    <p><strong>Preço por hora:</strong> ${precoHora}</p>
    <hr>
    <p><strong>Total:</strong> ${total}</p>
  `;
};