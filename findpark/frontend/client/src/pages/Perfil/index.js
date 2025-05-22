import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../services/api";
import "./styles.css";

export default function Perfil() {
  const [usuario, setUsuario] = useState({
    nome: "",
    email: "",
    telefone: "",
  });

  const [mensagem, setMensagem] = useState(null);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    async function fetchUsuario() {
      try {
        const res = await api.get("api/usuarios/me");
        setUsuario({
          nome: res.data.nome,
          email: res.data.email,
          telefone: res.data.telefone || "",
        });
      } catch (error) {
        setErro("Erro ao carregar dados do usuário");
      }
    }
    fetchUsuario();
  }, []);

  async function handleSalvarDados(e) {
    e.preventDefault();
    setMensagem(null);
    setErro(null);
    try {
      await api.patch("/api/usuarios", usuario);
      setMensagem("Dados atualizados com sucesso!");
    } catch (error) {
      setErro("Erro ao atualizar dados");
    }
  }

  async function handleExcluirConta() {
    const confirma = window.confirm(
      "Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita."
    );
    if (!confirma) return;

    try {
      await api.delete("/api/usuarios");
      alert("Conta excluída com sucesso!");
      localStorage.removeItem("accessToken");
      window.location.href = "/";
    } catch (error) {
      alert("Erro ao excluir conta.");
    }
  }

  return (
    <div className="perfil-container">
      <h2>Perfil</h2>

      {mensagem && <div className="alert alert-success">{mensagem}</div>}
      {erro && <div className="alert alert-danger">{erro}</div>}

      <form onSubmit={handleSalvarDados} className="form-perfil">
        <div>
          <label>Nome:</label>
          <input
            type="text"
            value={usuario.nome}
            onChange={(e) => setUsuario({ ...usuario, nome: e.target.value })}
            required
          />
        </div>

        <div>
          <label>Email:</label>
          <input type="email" value={usuario.email} readOnly />
          <p>
            <Link to="/alterar-email">Alterar e-mail em outra página</Link>
          </p>
        </div>

        <div>
          <label>Telefone:</label>
          <input
            type="tel"
            value={usuario.telefone}
            onChange={(e) =>
              setUsuario({ ...usuario, telefone: e.target.value })
            }
          />
        </div>

        <button type="submit">Salvar Dados</button>
      </form>

      <p>
        <a href="/trocar-senha">Trocar senha em outra página</a>
      </p>

      <button onClick={handleExcluirConta} className="btn-excluir">
        Excluir Conta
      </button>
    </div>
  );
}
