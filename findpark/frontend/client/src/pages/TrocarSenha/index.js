import React, { useState } from "react";
import api from "../../services/api";
import { Link, useNavigate } from "react-router-dom";
import "./styles.css"; // se quiser estilizar

export default function TrocarSenha() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    senhaAtual: "",
    novaSenha: "",
  });
  const [erro, setErro] = useState(null);
  const [mensagem, setMensagem] = useState(null);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const token = localStorage.getItem("accessToken"); // ajuste conforme seu storage
      await api.patch("/api/auth/trocar-senha", form, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setMensagem("Senha alterada com sucesso!");
      setErro(null);
      setForm({ senhaAtual: "", novaSenha: "" });

      // Redireciona depois de 2 segundos (opcional)
      setTimeout(() => navigate("/home-cliente"), 2000);
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Erro ao alterar senha");
      setMensagem(null);
    }
  };

  return (
    <div className="container py-4" style={{ maxWidth: "480px" }}>
      <Link
        to={`/perfil`}
        className="btn btn-outline-secondary mb-3"
      >
        <i className="fas fa-arrow-left me-2"></i>Voltar
      </Link>
      <h2 className="mb-4 text-center text-dark fs-4">Alterar Senha</h2>

      <form onSubmit={handleSubmit} className="d-flex flex-column gap-3">
        <div className="form-group">
          <label htmlFor="senhaAtual" className="form-label text-dark small">
            <i className="fas fa-lock me-2"></i>Senha Atual:
          </label>
          <input
            id="senhaAtual"
            type="password"
            name="senhaAtual"
            className="form-control form-control-lg"
            value={form.senhaAtual}
            onChange={handleChange}
            required
            autoComplete="current-password"
          />
        </div>

        <div className="form-group">
          <label htmlFor="novaSenha" className="form-label text-dark small">
            <i className="fas fa-key me-2"></i>Nova Senha:
          </label>
          <input
            id="novaSenha"
            type="password"
            name="novaSenha"
            className="form-control form-control-lg"
            value={form.novaSenha}
            onChange={handleChange}
            required
            autoComplete="new-password"
          />
        </div>

        <button type="submit" className="btn btn-primary btn-lg mt-3">
          Alterar Senha
        </button>
      </form>

      {mensagem && (
        <p className="text-success mt-3 text-center small">{mensagem}</p>
      )}
      {erro && <p className="text-danger mt-3 text-center small">{erro}</p>}
    </div>
  );
}
