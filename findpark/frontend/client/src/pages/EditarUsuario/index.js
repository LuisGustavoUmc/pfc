import React, { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import api from "../../services/api";
import "./styles.css";
import { toast } from "react-toastify";

export default function EditarUsuario() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [usuario, setUsuario] = useState({
    nome: "",
    email: "",
    telefone: "",
  });

  const [mensagem, setMensagem] = useState(null);
  const [erro, setErro] = useState(null);

  const formatarTelefone = (valor) => {
    const numeros = valor.replace(/\D/g, "").slice(0, 11);

    if (numeros.length <= 2) return `(${numeros}`;
    if (numeros.length <= 7)
      return `(${numeros.slice(0, 2)}) ${numeros.slice(2)}`;
    if (numeros.length <= 11)
      return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7)}`;

    return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(
      7,
      11
    )}`;
  };

  useEffect(() => {
    async function fetchUsuario() {
      try {
        const res = await api.get(`/api/usuarios/${id}`);
        setUsuario({
          nome: res.data.nome,
          email: res.data.email,
          telefone: res.data.telefone || "",
        });
      } catch (error) {
        setErro("Erro ao carregar dados do usuário");
      }
    }
    if (id) {
      fetchUsuario();
    }
  }, [id]);

  async function handleSalvarDados(e) {
    e.preventDefault();
    setMensagem(null);
    setErro(null);

    const telefoneNumeros = usuario.telefone.replace(/\D/g, "");
    if (telefoneNumeros.length < 10 || telefoneNumeros.length > 11) {
      setErro("Telefone inválido. Informe um número com DDD e até 11 dígitos.");
      return;
    }

    try {
      // A API atual espera patch em /api/usuarios com o token do usuário logado
      // Se o backend suportar atualizar outro usuário (admin), envie o id como parte do payload
      // Ajuste conforme sua API permite
      await api.patch("/api/usuarios", usuario);
      setMensagem("Dados atualizados com sucesso!");
      // Opcional: redirecionar após salvar
      // navigate("/usuarios");
    } catch (error) {
      setErro("Erro ao atualizar dados");
    }
  }

  async function handleExcluirConta() {
    const confirma = window.confirm(
      "Tem certeza que deseja excluir este usuário? Esta ação não pode ser desfeita."
    );
    if (!confirma) return;

    try {
      // Se o endpoint delete só excluir o usuário logado, ajuste seu backend para suportar exclusão de usuário por id (admin)
      await api.delete(`/api/usuarios/${id}`);
      toast.success("Usuário excluído com sucesso!");
      navigate("/usuarios");
    } catch (error) {
      toast.error("Erro ao excluir usuário.");
    }
  }

  return (
    <div className="container py-5">
      <h2 className="mb-4 fs-4 text-dark">
        <i className="fas fa-user-circle me-2"></i>Editar Usuário
      </h2>

      {mensagem && <div className="alert alert-success">{mensagem}</div>}
      {erro && <div className="alert alert-danger">{erro}</div>}

      <form onSubmit={handleSalvarDados} className="w-100">
        <div className="mb-3">
          <label className="form-label text-dark fs-6">
            <i className="fas fa-user me-2"></i>Nome
          </label>
          <input
            type="text"
            className="form-control form-control-md"
            value={usuario.nome}
            onChange={(e) => setUsuario({ ...usuario, nome: e.target.value })}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label text-dark fs-6">
            <i className="fas fa-envelope me-2"></i>Email
          </label>
          <input
            type="email"
            className="form-control form-control-md"
            value={usuario.email}
            onChange={(e) => setUsuario({ ...usuario, email: e.target.value })}
            required
          />
          {/* Se quiser link para alterar email, descomente: */}
          {/* <small className="form-text text-dark fs-6">
            <Link to={`/usuarios/alterar-email/${id}`}>Alterar e-mail</Link>
          </small> */}
        </div>

        <div className="mb-4">
          <label className="form-label text-dark fs-6">
            <i className="fas fa-phone me-2"></i>Telefone
          </label>
          <input
            type="tel"
            className="form-control form-control-md"
            value={usuario.telefone}
            onChange={(e) =>
              setUsuario({
                ...usuario,
                telefone: formatarTelefone(e.target.value),
              })
            }
          />
        </div>

        <div className="d-grid gap-2">
          <button type="submit" className="btn btn-primary btn-md">
            <i className="fas fa-save me-2"></i>Salvar Dados
          </button>
        </div>
      </form>

      <div className="mt-5 d-flex gap-3 justify-content-center">
        {/* Link para trocar senha - ajuste conforme necessidade */}
        <Link
          to={`/usuarios/trocar-senha/${id}`}
          className="btn btn-outline-secondary d-flex align-items-center fs-6 text-dark"
        >
          <i className="fas fa-key me-2"></i> Trocar senha
        </Link>

        <button
          onClick={handleExcluirConta}
          className="btn btn-outline-danger d-flex align-items-center fs-6"
        >
          <i className="fas fa-trash-alt me-2"></i> Excluir Usuário
        </button>
      </div>
    </div>
  );
}
