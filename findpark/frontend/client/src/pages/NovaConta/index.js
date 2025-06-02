import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import { toast } from "react-toastify";
import Swal from "sweetalert2";
import { mostrarTermos } from '../../utils/Utils'; 

export default function NovaConta() {
  const [form, setForm] = useState({
    nome: "",
    email: "",
    senha: "",
    telefone: "",
    role: "CLIENTE",
  });
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [erro, setErro] = useState("");
  const [senhaFocada, setSenhaFocada] = useState(false);
  const navigate = useNavigate();

  const requisitos = {
    tamanho: (senha) => senha.length >= 8,
    maiuscula: (senha) => /[A-Z]/.test(senha),
    minuscula: (senha) => /[a-z]/.test(senha),
    numero: (senha) => /\d/.test(senha),
    especial: (senha) => /[@$!%*?&]/.test(senha),
  };

  const validarSenha = (senha) => {
    return Object.values(requisitos).every((r) => r(senha));
  };

  const validarTelefone = (telefone) => {
    const numeros = telefone.replace(/\D/g, "");
    return numeros.length === 10 || numeros.length === 11;
  };

  const formatarTelefone = (valor) => {
    const numeros = valor.replace(/\D/g, "").slice(0, 11);

    if (numeros.length <= 2) return `(${numeros}`;
    if (numeros.length <= 7)
      return `(${numeros.slice(0, 2)}) ${numeros.slice(2)}`;
    if (numeros.length <= 11)
      return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7)}`;

    return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7, 11)}`;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    const novoValor = name === "telefone" ? formatarTelefone(value) : value;
    setForm({ ...form, [name]: novoValor });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    mostrarTermos().then(async (result) => {
      if (result.isConfirmed) {
        setErro("");

        if (!form.nome || !form.email || !form.telefone || !form.senha) {
          setErro("Todos os campos são obrigatórios.");
          return;
        }

        if (!validarTelefone(form.telefone)) {
          setErro(
            "Telefone inválido. Use o formato (99) 99999-9999 ou (99) 9999-9999."
          );
          return;
        }

        if (form.senha !== confirmarSenha) {
          setErro("As senhas não coincidem.");
          return;
        }

        if (!validarSenha(form.senha)) {
          setErro("A senha não atende aos requisitos.");
          return;
        }

        try {
          await api.post("/api/usuarios/registrar", form);
          toast.success(
            "Cadastro realizado com sucesso! Verifique seu e-mail para ativar a conta."
          );
          navigate("/");
        } catch (error) {
          toast.error("Erro ao cadastrar usuário. Tente novamente.");
        }
      }
      // Se cancelar, não faz nada
    });
  };

  const senha = form.senha;
  const requisitosStatus = {
    tamanho: requisitos.tamanho(senha),
    maiuscula: requisitos.maiuscula(senha),
    minuscula: requisitos.minuscula(senha),
    numero: requisitos.numero(senha),
    especial: requisitos.especial(senha),
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "700px" }}>
      <h2 className="mb-4 text-center text-dark">Criar Conta</h2>
      <form onSubmit={handleSubmit}>
        {/* Nome */}
        <div className="mb-3">
          <label className="form-label text-dark">Nome</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-user"></i>
            </span>
            <input
              className="form-control"
              name="nome"
              value={form.nome}
              onChange={handleChange}
              required
              placeholder="Seu nome completo"
            />
          </div>
        </div>

        {/* Email */}
        <div className="mb-3">
          <label className="form-label text-dark">Email</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-envelope"></i>
            </span>
            <input
              className="form-control"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              placeholder="email@exemplo.com"
            />
          </div>
        </div>

        {/* Telefone */}
        <div className="mb-3">
          <label className="form-label text-dark">Telefone</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-phone"></i>
            </span>
            <input
              className="form-control"
              name="telefone"
              value={form.telefone}
              onChange={handleChange}
              required
              placeholder="(99) 99999-9999"
            />
          </div>
        </div>

        {/* Senha */}
        <div className="mb-3">
          <label className="form-label text-dark">Senha</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-lock"></i>
            </span>
            <input
              className="form-control"
              type="password"
              name="senha"
              value={form.senha}
              onChange={handleChange}
              onFocus={() => setSenhaFocada(true)}
              onBlur={() => setSenhaFocada(false)}
              required
            />
          </div>
          {senhaFocada && (
            <ul className="mt-2 small">
              <li
                className={
                  requisitosStatus.tamanho ? "text-success" : "text-danger"
                }
              >
                Mínimo 8 caracteres
              </li>
              <li
                className={
                  requisitosStatus.maiuscula ? "text-success" : "text-danger"
                }
              >
                Letra maiúscula
              </li>
              <li
                className={
                  requisitosStatus.minuscula ? "text-success" : "text-danger"
                }
              >
                Letra minúscula
              </li>
              <li
                className={
                  requisitosStatus.numero ? "text-success" : "text-danger"
                }
              >
                Número
              </li>
              <li
                className={
                  requisitosStatus.especial ? "text-success" : "text-danger"
                }
              >
                Caractere especial (@$!%*?&)
              </li>
            </ul>
          )}
        </div>

        {/* Confirmar senha */}
        <div className="mb-3">
          <label className="form-label text-dark">Confirmar Senha</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-lock"></i>
            </span>
            <input
              className="form-control"
              type="password"
              value={confirmarSenha}
              onChange={(e) => setConfirmarSenha(e.target.value)}
              required
            />
          </div>
        </div>

        {/* Tipo de Conta */}
        <div className="mb-4">
          <label className="form-label text-dark d-block">Tipo de Conta</label>
          <div className="form-check form-check-inline mt-2 me-3">
            <input
              type="radio"
              id="cliente"
              name="role"
              value="CLIENTE"
              checked={form.role === "CLIENTE"}
              onChange={handleChange}
              className="form-check-input"
            />
            <label htmlFor="cliente" className="form-check-label ms-1">
              Cliente
            </label>
          </div>
          <div className="form-check form-check-inline mt-2">
            <input
              type="radio"
              id="proprietario"
              name="role"
              value="PROPRIETARIO"
              checked={form.role === "PROPRIETARIO"}
              onChange={handleChange}
              className="form-check-input"
            />
            <label htmlFor="proprietario" className="form-check-label ms-1">
              Proprietário
            </label>
          </div>
        </div>

        {/* Botão */}
        <button type="submit" className="btn btn-primary w-100">
          <i className="fas fa-user-plus me-2"></i> Cadastrar
        </button>
      </form>

      {erro && <div className="alert alert-danger mt-3">{erro}</div>}
    </div>
  );
}
