import React, { useState } from "react";
import api from "../../services/api";
import { useNavigate } from "react-router-dom";
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
    <div className="form-container">
      <h2>Alterar Senha</h2>

      <form onSubmit={handleSubmit}>
        <div>
          <label>Senha Atual:</label>
          <input
            type="password"
            name="senhaAtual"
            value={form.senhaAtual}
            onChange={handleChange}
            required
          />
        </div>

        <div>
          <label>Nova Senha:</label>
          <input
            type="password"
            name="novaSenha"
            value={form.novaSenha}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit">Alterar Senha</button>
      </form>

      {mensagem && <p className="success">{mensagem}</p>}
      {erro && <p className="error">{erro}</p>}
    </div>
  );
}
