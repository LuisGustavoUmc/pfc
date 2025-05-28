import React, { useState, useEffect } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";
import { toast } from "react-toastify";

const EditarEstacionamento = () => {
  const { id } = useParams(); // pega o id da URL (se existir)
  const navigate = useNavigate();

  const [form, setForm] = useState({
    nome: "",
    telefone: "",
    cep: "",
    endereco: {
      cep: "",
      logradouro: "",
      numero: "",
      complemento: "",
      unidade: "",
      bairro: "",
      localidade: "",
      uf: "",
      estado: "",
      regiao: "",
      ibge: "",
      gia: "",
      ddd: "",
      siafi: "",
    },
    capacidade: 1,
    horaAbertura: "08:00",
    horaFechamento: "18:00",
  });

  useEffect(() => {
    if (id) {
      api
        .get(`/api/estacionamentos/${id}`)
        .then((res) => {
          const e = res.data;
          setForm({
            nome: e.nome || "",
            telefone: e.telefone || "",
            cep: e.endereco?.cep || "",
            endereco: e.endereco || {
              cep: "",
              logradouro: "",
              numero: "",
              complemento: "",
              unidade: "",
              bairro: "",
              localidade: "",
              uf: "",
              estado: "",
              regiao: "",
              ibge: "",
              gia: "",
              ddd: "",
              siafi: "",
            },
            capacidade: e.capacidade || 1,
            horaAbertura: e.horaAbertura || "08:00",
            horaFechamento: e.horaFechamento || "18:00",
          });
        })
        .catch((err) => {
          console.error("Erro ao carregar estacionamento:", err);
          toast.error("Erro ao carregar dados do estacionamento.");
        });
    }
  }, [id]);

  const formatarTelefone = (valor) => {
    const numeros = valor.replace(/\D/g, "").slice(0, 11); // Só até 11 dígitos

    if (numeros.length <= 2) return `(${numeros}`;
    if (numeros.length <= 7) return `(${numeros.slice(0, 2)}) ${numeros.slice(2)}`;
    if (numeros.length <= 11)
      return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7)}`;

    return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7, 11)}`;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "telefone") {
      const telefoneFormatado = formatarTelefone(value);
      setForm((prev) => ({ ...prev, telefone: telefoneFormatado }));
    } else if (name === "numero") {
      setForm((prev) => ({
        ...prev,
        endereco: {
          ...prev.endereco,
          numero: value,
        },
      }));
    } else if (name === "cep") {
      const cepLimpo = value.replace(/\D/g, "").slice(0, 8);
      setForm((prev) => ({ ...prev, cep: cepLimpo }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleCepBlur = () => {
    const cepLimpo = form.cep.replace(/\D/g, "");

    if (cepLimpo.length !== 8) {
      toast.error("CEP inválido.");
      return;
    }

    api
      .get(`/api/enderecos/${cepLimpo}`)
      .then((res) => {
        if (res.data?.erro) {
          toast.warn("CEP não encontrado.");
          return;
        }

        setForm((prev) => ({
          ...prev,
          cep: res.data.cep || prev.cep,
          endereco: {
            ...prev.endereco,
            cep: res.data.cep || "",
            logradouro: res.data.logradouro || "",
            complemento: res.data.complemento || "",
            unidade: res.data.unidade || "",
            bairro: res.data.bairro || "",
            localidade: res.data.localidade || "",
            uf: res.data.uf || "",
            estado: res.data.estado || "",
            regiao: res.data.regiao || "",
            ibge: res.data.ibge || "",
            gia: res.data.gia || "",
            ddd: res.data.ddd || "",
            siafi: res.data.siafi || "",
          },
        }));
      })
      .catch((err) => {
        console.error("Erro ao buscar endereço pelo CEP:", err);
        toast.error("Erro ao buscar endereço pelo CEP.");
      });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (id) {
      api
        .put(`/api/estacionamentos/${id}`, form)
        .then(() => {
          toast.success("Estacionamento atualizado com sucesso!");
          navigate("/home-proprietario");
        })
        .catch((err) => {
          console.error("Erro ao atualizar estacionamento:", err);
          toast.error("Erro ao atualizar estacionamento.");
        });
    } else {
      api
        .post("/api/estacionamentos", form)
        .then(() => {
          toast.success("Estacionamento cadastrado com sucesso!");
          navigate("/home-proprietario");
        })
        .catch((err) => {
          console.error("Erro ao cadastrar estacionamento:", err);
          toast.error("Erro ao cadastrar estacionamento.");
        });
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "700px" }}>
      <Link
        to={`/estacionamentos/${id}`}
        className="btn btn-outline-secondary mb-3"
      >
        <i className="fas fa-arrow-left me-2"></i>Voltar
      </Link>
      <h2 className="mb-4 text-center text-dark">
        {id ? "Editar Estacionamento" : "Cadastrar Novo Estacionamento"}
      </h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label text-dark">Nome</label>
          <input
            type="text"
            name="nome"
            value={form.nome}
            onChange={handleChange}
            required
            className="form-control"
            placeholder="Nome do Estacionamento"
          />
        </div>

        <div className="mb-3">
          <label className="form-label text-dark">Telefone</label>
          <input
            type="tel"
            name="telefone"
            value={form.telefone}
            onChange={handleChange}
            required
            className="form-control"
            placeholder="(00) 00000-0000"
            maxLength={15}
          />
        </div>

        <div className="mb-3">
          <label className="form-label text-dark">CEP</label>
          <input
            type="text"
            name="cep"
            value={form.cep}
            onChange={handleChange}
            onBlur={handleCepBlur}
            required
            className="form-control"
            placeholder="Digite o CEP"
          />
        </div>

        <div className="mb-3">
          <label className="form-label text-dark">Número</label>
          <input
            type="text"
            name="numero"
            value={form.endereco.numero || ""}
            onChange={handleChange}
            required
            className="form-control"
            placeholder="Número"
          />
        </div>

        <div className="mb-3">
          <label className="form-label text-dark">Endereço</label>
          <input
            type="text"
            name="enderecoVisivel"
            value={formatarEndereco(form.endereco)}
            readOnly
            className="form-control bg-light"
            placeholder="Endereço automático via CEP"
          />
        </div>

        <div className="mb-3">
          <label className="form-label text-dark">Capacidade</label>
          <input
            type="number"
            name="capacidade"
            value={form.capacidade}
            onChange={handleChange}
            min={1}
            required
            className="form-control"
          />
        </div>

        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label text-dark">Hora de Abertura</label>
            <input
              type="time"
              name="horaAbertura"
              value={form.horaAbertura}
              onChange={handleChange}
              required
              className="form-control"
            />
          </div>

          <div className="col-md-6 mb-3">
            <label className="form-label text-dark">Hora de Fechamento</label>
            <input
              type="time"
              name="horaFechamento"
              value={form.horaFechamento}
              onChange={handleChange}
              required
              className="form-control"
            />
          </div>
        </div>

        <button type="submit" className="btn btn-success w-100 mt-3">
          <i className={`fas fa-${id ? "save" : "plus"} me-2`}></i>
          {id ? "Salvar Alterações" : "Cadastrar"}
        </button>
      </form>
    </div>
  );
};

export default EditarEstacionamento;
