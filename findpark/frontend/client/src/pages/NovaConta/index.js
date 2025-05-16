import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";

export default function NovaConta() {
  const [form, setForm] = useState({
    nome: '',
    email: '',
    senha: '',
    telefone: '',
    role: 'CLIENTE',
  });
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [erro, setErro] = useState('');
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');

    if (!form.nome || !form.email || !form.telefone || !form.senha) {
      setErro("Todos os campos são obrigatórios.");
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
      alert("Cadastro realizado com sucesso! Verifique seu e-mail para ativar a conta.");
      navigate("/");
    } catch (error) {
      alert("Erro ao cadastrar usuário. Tente novamente.");
    }
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
    <div className="container mt-5" style={{ maxWidth: "600px" }}>
      <h3>Criar Nova Conta</h3>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label>Nome</label>
          <input className="form-control" name="nome" value={form.nome} onChange={handleChange} required />
        </div>
        <div className="mb-3">
          <label>Email</label>
          <input className="form-control" type="email" name="email" value={form.email} onChange={handleChange} required />
        </div>
        <div className="mb-3">
          <label>Telefone</label>
          <input className="form-control" name="telefone" value={form.telefone} onChange={handleChange} required />
        </div>
        <div className="mb-3">
          <label>Senha</label>
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
          {senhaFocada && (
            <ul className="mt-2">
              <li className={requisitosStatus.tamanho ? 'text-success' : 'text-danger'}>Mínimo 8 caracteres</li>
              <li className={requisitosStatus.maiuscula ? 'text-success' : 'text-danger'}>Letra maiúscula</li>
              <li className={requisitosStatus.minuscula ? 'text-success' : 'text-danger'}>Letra minúscula</li>
              <li className={requisitosStatus.numero ? 'text-success' : 'text-danger'}>Número</li>
              <li className={requisitosStatus.especial ? 'text-success' : 'text-danger'}>Caractere especial (@$!%*?&)</li>
            </ul>
          )}
        </div>
        <div className="mb-3">
          <label>Confirmar Senha</label>
          <input
            className="form-control"
            type="password"
            value={confirmarSenha}
            onChange={(e) => setConfirmarSenha(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label>Tipo de Conta</label>
          <div>
            <input
              type="radio"
              id="cliente"
              name="role"
              value="CLIENTE"
              checked={form.role === "CLIENTE"}
              onChange={handleChange}
            />
            <label htmlFor="cliente" className="ms-1 me-3">Cliente</label>
            <input
              type="radio"
              id="proprietario"
              name="role"
              value="PROPRIETARIO"
              checked={form.role === "PROPRIETARIO"}
              onChange={handleChange}
            />
            <label htmlFor="proprietario" className="ms-1">Proprietário</label>
          </div>
        </div>
        <button type="submit" className="btn btn-primary">Cadastrar</button>
      </form>
      {erro && <div className="alert alert-danger mt-3">{erro}</div>}
    </div>
  );
}
