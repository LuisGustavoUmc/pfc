import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";

const HomeProprietario = () => {
  const [estacionamentos, setEstacionamentos] = useState([]);
  const [form, setForm] = useState({
    nome: "",
    endereco: "",
    capacidade: 1,
    horaAbertura: "08:00",
    horaFechamento: "18:00",
  });
  const [nomeUsuario, setNomeUsuario] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get("/api/usuarios/me")
      .then((res) => setNomeUsuario(res.data.nome))
      .catch((err) => console.error("Erro ao buscar usuário:", err));

    api
      .get("/api/estacionamentos/meus")
      .then((res) => setEstacionamentos(res.data))
      .catch((err) => {
        console.error("Erro ao buscar estacionamentos:", err);
        setEstacionamentos([]);
      });
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    api
      .post("/api/estacionamentos", form)
      .then((res) => {
        setEstacionamentos([...estacionamentos, res.data]);
        setForm({
          nome: "",
          endereco: "",
          capacidade: 1,
          horaAbertura: "08:00",
          horaFechamento: "18:00",
        });
      })
      .catch((err) => {
        console.error("Erro ao cadastrar estacionamento:", err);
        alert("Erro ao cadastrar estacionamento.");
      });
  };

  const handleCadastrarVaga = (estacionamentoId) => {
    navigate(`/estacionamentos/${estacionamentoId}/cadastrar-vaga`);
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">
        Bem-vindo, {nomeUsuario || "Proprietário"}!
      </h1>

      <h2 className="text-xl font-semibold mb-2">Cadastrar novo estacionamento</h2>
      <form onSubmit={handleSubmit} className="space-y-4 max-w-lg">
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
          type="text"
          name="endereco"
          placeholder="Endereço"
          value={form.endereco}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
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
        <button type="submit" className="btn btn-primary">
          Cadastrar Estacionamento
        </button>
      </form>

      <hr className="my-6" />

      <h2 className="text-xl font-semibold mb-4">Meus Estacionamentos</h2>
      <div className="grid gap-4">
        {estacionamentos.length === 0 ? (
          <p>Nenhum estacionamento cadastrado ainda.</p>
        ) : (
          estacionamentos.map((e) => (
            <div key={e.id} className="border rounded p-4 shadow">
              <h3 className="text-lg font-bold">{e.nome}</h3>
              <p><strong>Endereço:</strong> {e.endereco}</p>
              <p><strong>Capacidade:</strong> {e.capacidade}</p>
              <p><strong>Horário:</strong> {e.horaAbertura} às {e.horaFechamento}</p>
              <button
                className="btn btn-primary"
                onClick={() => handleCadastrarVaga(e.id)}
              >
                Cadastrar Vaga
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default HomeProprietario;