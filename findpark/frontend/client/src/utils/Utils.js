// src/utils/formatarEndereco.js
import Swal from "sweetalert2";


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

  // Tratamento para funcionamento 24h (00:00 às 00:00)
  const funcionamento24h = ha === 0 && ma === 0 && hf === 0 && mf === 0;
  if (funcionamento24h) return true;

  const inicioFuncionamento = new Date(data);
  inicioFuncionamento.setHours(ha, ma, 0, 0);

  const fimFuncionamento = new Date(data);
  fimFuncionamento.setHours(hf, mf, 0, 0);

  return dataComHora >= inicioFuncionamento && dataComHora <= fimFuncionamento;
};

export const gerarHorariosEntre = (
  horaAbertura,
  horaFechamento,
  ehHorarioSaida = false,
  horaMinima = null,
  dataEntrada = null,
  dataSaida = null
) => {
  console.log("=== gerarHorariosEntre chamado ===");
  console.log("horaAbertura:", horaAbertura);
  console.log("horaFechamento:", horaFechamento);
  console.log("ehHorarioSaida:", ehHorarioSaida);
  console.log("horaMinima:", horaMinima);
  console.log("dataEntrada:", dataEntrada);
  console.log("dataSaida:", dataSaida);

  const horarios = [];

  horaAbertura = horaAbertura.slice(0, 5);
  horaFechamento = horaFechamento.slice(0, 5);

  const [ha, ma] = horaAbertura.split(":").map(Number);
  const [hf, mf] = horaFechamento.split(":").map(Number);

  if (isNaN(ha) || isNaN(ma) || isNaN(hf) || isNaN(mf)) {
    console.error("Formato de hora inválido:", horaAbertura, horaFechamento);
    return [];
  }

  const funcionamento24h = ha === 0 && ma === 0 && hf === 0 && mf === 0;
  console.log("funcionamento24h:", funcionamento24h);

  let totalMinutos = funcionamento24h ? 0 : ha * 60 + ma;
  let fimMinutos = funcionamento24h ? 24 * 60 : hf * 60 + mf;

  if (!ehHorarioSaida && !funcionamento24h) {
    fimMinutos -= 30;
    console.log("fimMinutos ajustado para entrada (-30min):", fimMinutos);
  }

  const hoje = new Date();
  const mesmoDia =
    dataEntrada && dataSaida
      ? new Date(dataEntrada).toDateString() ===
        new Date(dataSaida).toDateString()
      : true;

  // Determinar a data de referência
  const dataRef = ehHorarioSaida
    ? dataSaida instanceof Date
      ? dataSaida
      : null
    : dataEntrada instanceof Date
      ? dataEntrada
      : null;

  // Verificar se a data (ou o "null" da entrada, assumindo hoje) é hoje
  const ehHoje =
    dataRef?.toDateString?.() === hoje.toDateString() ||
    (!dataRef && dataEntrada === null && !ehHorarioSaida);

  console.log("mesmoDia:", mesmoDia);
  console.log("dataRef (ehHorarioSaida ? dataSaida : dataEntrada):", dataRef);
  console.log("agora:", hoje);
  console.log("ehHoje:", ehHoje);

  let minMinutos = null;

  if (ehHoje) {
    const minutosAgora =
      Math.ceil((hoje.getHours() * 60 + hoje.getMinutes()) / 30) * 30;
    console.log("minutosAgora (arredondado ao próximo 30min):", minutosAgora);
    minMinutos = minutosAgora;
  }

  if (horaMinima && mesmoDia) {
    const [hmin, mmin] = horaMinima.split(":").map(Number);
    const minConfigurado = hmin * 60 + mmin;
    console.log("horaMinima convertida para minMinutos:", minConfigurado);
    minMinutos =
      minMinutos !== null
        ? Math.max(minMinutos, minConfigurado)
        : minConfigurado;
  }

  if (minMinutos !== null && minMinutos > totalMinutos) {
    console.log(`Ajustando totalMinutos de ${totalMinutos} para ${minMinutos}`);
    totalMinutos = minMinutos;
  }

  console.log("totalMinutos inicial:", totalMinutos);
  console.log("fimMinutos:", fimMinutos);

  while (
    funcionamento24h ? totalMinutos < fimMinutos : totalMinutos <= fimMinutos
  ) {
    if (!(funcionamento24h && ehHorarioSaida && totalMinutos === 0)) {
      const horas = String(Math.floor(totalMinutos / 60)).padStart(2, "0");
      const minutos = String(totalMinutos % 60).padStart(2, "0");
      horarios.push(`${horas}:${minutos}`);
    }
    totalMinutos += 30;
  }

  if (funcionamento24h) {
    if (ehHorarioSaida && horarios[horarios.length - 1] !== "00:00") {
      horarios.push("00:00");
    } else if (
      !ehHorarioSaida &&
      horarios[0] !== "00:00" &&
      !(ehHoje && minMinutos > 0) // impede "00:00" se já passou
    ) {
      horarios.unshift("00:00");
    }
  }

  console.log("horarios gerados:", horarios);
  return horarios;
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

export function mostrarTermos() {
  return Swal.fire({
    title: 'Termos e Condições de Uso',
    html: `
      <p>Bem-vindo ao <strong>FindPark</strong>! Ao utilizar nosso aplicativo, você concorda em cumprir e aceitar os seguintes termos e condições. Leia atentamente.</p>

      <h3>1. Uso do Serviço</h3>
      <p>O FindPark oferece uma plataforma para facilitar o cadastro e reserva de vagas. O usuário concorda em utilizar o aplicativo apenas para fins legais e conforme as regras definidas.</p>

      <h3>2. Cadastro e Segurança</h3>
      <p>Para utilizar o FindPark, o usuário deve realizar cadastro com informações verdadeiras. É responsabilidade do usuário manter a confidencialidade de sua senha e dados de acesso.</p>

      <h3>3. Reservas e Cancelamentos</h3>
      <p>As reservas podem ser feitas pelo cliente e a política de cancelamento, incluindo o prazo de tolerância para atraso, será definida pelo proprietário do estacionamento. Reservas não utilizadas podem ser canceladas conforme definido pelo proprietário.</p>

      <h3>4. Responsabilidades</h3>
      <p>O FindPark não se responsabiliza por danos, perdas ou extravios ocorridos dentro dos estacionamentos cadastrados. A responsabilidade é do proprietário do estacionamento.</p>

      <h3>5. Modificações dos Termos</h3>
      <p>O FindPark reserva-se o direito de alterar estes termos a qualquer momento. As alterações serão comunicadas e o uso continuado do aplicativo implica na aceitação dos novos termos.</p>

      <h3>6. Contato</h3>
      <p>Em caso de dúvidas, entre em contato conosco pelo e-mail: <a href="mailto:findparkumc@gmail.com">findparkumc@gmail.com</a></p>

      <p class="text-center text-muted small mt-4 mb-0">© 2025 FindPark. Todos os direitos reservados.</p>
    `,
    width: '600px',
    showCancelButton: true,
    confirmButtonText: 'Aceito e Quero Me Cadastrar',
    cancelButtonText: 'Cancelar',
    scrollbarPadding: false,
    buttonsStyling: false,
    customClass: {
      popup: 'p-4',
      title: 'fs-4 fw-bold',
      htmlContainer: 'text-start',
      confirmButton: 'btn btn-success me-2',
      cancelButton: 'btn btn-danger', 
    },
  });
}
