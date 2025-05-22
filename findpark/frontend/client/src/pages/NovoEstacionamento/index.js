import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import { formatarEndereco } from "../../utils/Utils";

const NovoEstacionamento = () => {
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

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "numero") {
      // Atualiza apenas o número dentro do endereço
      setForm((prev) => ({
        ...prev,
        endereco: {
          ...prev.endereco,
          numero: value,
        },
      }));
    } else {
      // Atualiza qualquer outro campo fora do objeto endereco
      setForm((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleCepBlur = () => {
    const cepLimpo = form.cep.replace(/\D/g, "");
    console.log("Buscando endereço para CEP:", cepLimpo);

    if (cepLimpo.length !== 8) {
      console.warn("CEP inválido:", cepLimpo);
      return;
    }

    api
      .get(`/api/enderecos/${cepLimpo}`)
      .then((res) => {
        console.log("Resposta do endereço:", res.data);
        if (res.data?.erro) {
          alert("CEP não encontrado.");
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
        alert("Erro ao buscar endereço pelo CEP.");
      });
  };


  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Enviando dados do formulário:", form);

    api
      .post("/api/estacionamentos", form)
      .then(() => {
        console.log("Estacionamento cadastrado com sucesso!");
        navigate("/home-proprietario");
      })
      .catch((err) => {
        console.error("Erro ao cadastrar estacionamento:", err);
        alert("Erro ao cadastrar estacionamento.");
      });
  };

  console.log("form.endereco (render):", form.endereco);

  return (
    <div className="p-6 max-w-lg mx-auto">
      <h2 className="text-xl font-bold mb-4">Cadastrar novo estacionamento</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          name="nome"
          placeholder="Nome do Estacionamento"
          value={form.nome}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
        <input
          type="tel"
          name="telefone"
          placeholder="Telefone"
          value={form.telefone}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
        <input
          type="text"
          name="cep"
          placeholder="CEP"
          value={form.cep}
          onChange={handleChange}
          onBlur={handleCepBlur}
          required
          className="w-full border p-2 rounded"
        />
        <input
          type="text"
          name="numero"
          placeholder="Número"
          value={form.numero}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
        <input
          type="text"
          name="enderecoVisivel"
          placeholder="Endereço"
          value={formatarEndereco(form.endereco)}
          className="w-full border p-2 rounded bg-gray-100"
        />
        <label className="block text-sm">Capacidade</label>
        <input
          type="number"
          name="capacidade"
          placeholder="Capacidade"
          value={form.capacidade}
          onChange={handleChange}
          min={1}
          required
          className="w-full border p-2 rounded"
        />
        <div className="flex gap-4">
          <div>
            <label className="block text-sm">Hora de Abertura</label>
            <input
              type="time"
              name="horaAbertura"
              value={form.horaAbertura}
              onChange={handleChange}
              required
              className="border p-2 rounded"
            />
          </div>
          <div>
            <label className="block text-sm">Hora de Fechamento</label>
            <input
              type="time"
              name="horaFechamento"
              value={form.horaFechamento}
              onChange={handleChange}
              required
              className="border p-2 rounded"
            />
          </div>
        </div>
        <button type="submit" className="btn btn-success">
          Cadastrar
        </button>
      </form>
    </div>
  );
};

export default NovoEstacionamento;
