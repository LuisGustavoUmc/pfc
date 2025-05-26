import React, { useState } from "react";
import api from "../../services/api";
import { useNavigate } from "react-router-dom";

export default function AlterarEmail() {
  const [novoEmail, setNovoEmail] = useState("");
  const [confirmarEmail, setConfirmarEmail] = useState("");
  const [mensagem, setMensagem] = useState(null);
  const [erro, setErro] = useState(null);
  const [errors, setErrors] = useState({ novoEmail: "", confirmarEmail: "" });
  const navigate = useNavigate();

  const validateEmail = () => {
    const newErrors = { novoEmail: "", confirmarEmail: "" };

    // Verifica se os emails foram preenchidos
    if (!novoEmail) {
      newErrors.novoEmail = "Novo e-mail é obrigatório";
    } else if (!/\S+@\S+\.\S+/.test(novoEmail)) {
      newErrors.novoEmail = "Formato de e-mail inválido";
    }

    if (!confirmarEmail) {
      newErrors.confirmarEmail = "Confirmação do e-mail é obrigatória";
    } else if (!/\S+@\S+\.\S+/.test(confirmarEmail)) {
      newErrors.confirmarEmail = "Formato de e-mail inválido";
    }

    // Verifica se os emails coincidem
    if (novoEmail && confirmarEmail && novoEmail !== confirmarEmail) {
      newErrors.confirmarEmail = "Os e-mails não coincidem";
    }

    setErrors(newErrors);

    // Retorna true se não tiver erros
    return Object.values(newErrors).every((msg) => msg === "");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensagem(null);
    setErro(null);

    if (!validateEmail()) return;

    try {
      await api.patch("/api/usuarios", { email: novoEmail });

      // Deslogar o usuário
      localStorage.removeItem("accessToken");
      localStorage.removeItem("email");
      localStorage.removeItem("userRole");

      // Mensagem de sucesso temporária
      setMensagem(
        "E-mail atualizado com sucesso. Você será redirecionado para o login."
      );

      setTimeout(() => {
        navigate("/");
      }, 2500); // 2,5 segundos para o usuário ler a mensagem
    } catch (error) {
      setErro("Erro ao atualizar o e-mail.");
    }
  };

  return (
    <div className="container py-4" style={{ maxWidth: "480px" }}>
      <h2 className="mb-4 text-center text-dark fs-4">Alterar E-mail</h2>

      {mensagem && (
        <div className="alert alert-success text-center">{mensagem}</div>
      )}
      {erro && <div className="alert alert-danger text-center">{erro}</div>}

      <form onSubmit={handleSubmit} className="d-flex flex-column gap-3">
        <div className="form-group">
          <label htmlFor="novoEmail" className="form-label text-dark small">
            <i className="fas fa-envelope me-2"></i>Novo e-mail:
          </label>
          <input
            id="novoEmail"
            type="email"
            className="form-control form-control-lg"
            value={novoEmail}
            onChange={(e) => setNovoEmail(e.target.value)}
            required
            autoComplete="email"
          />
          {errors.novoEmail && (
            <div className="alert alert-danger mt-2">{errors.novoEmail}</div>
          )}
        </div>

        <div className="form-group">
          <label
            htmlFor="confirmarEmail"
            className="form-label text-dark small"
          >
            <i className="fas fa-envelope-open me-2"></i>Confirmar e-mail:
          </label>
          <input
            id="confirmarEmail"
            type="email"
            className="form-control form-control-lg"
            value={confirmarEmail}
            onChange={(e) => setConfirmarEmail(e.target.value)}
            required
            autoComplete="email"
          />
          {errors.confirmarEmail && (
            <div className="alert alert-danger mt-2">
              {errors.confirmarEmail}
            </div>
          )}
        </div>

        <button type="submit" className="btn btn-primary btn-lg mt-3">
          Salvar novo e-mail
        </button>
      </form>
    </div>
  );
}
