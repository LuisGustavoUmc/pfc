import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function AtualizarSenha() {
  const { token } = useParams();
  const [senha, setSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [erro, setErro] = useState('');
  const [senhaFocada, setSenhaFocada] = useState(false);
  const navigate = useNavigate();

  const requisitos = {
    tamanho: (s) => s.length >= 8,
    maiuscula: (s) => /[A-Z]/.test(s),
    minuscula: (s) => /[a-z]/.test(s),
    numero: (s) => /\d/.test(s),
    especial: (s) => /[@$!%*?&]/.test(s),
  };

  const requisitosStatus = {
    tamanho: requisitos.tamanho(senha),
    maiuscula: requisitos.maiuscula(senha),
    minuscula: requisitos.minuscula(senha),
    numero: requisitos.numero(senha),
    especial: requisitos.especial(senha),
  };

  const senhaValida = Object.values(requisitosStatus).every(Boolean);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');

    if (senha !== confirmarSenha) {
      setErro('As senhas não coincidem.');
      return;
    }

    if (!senhaValida) {
      setErro('A senha não atende aos requisitos de segurança.');
      return;
    }

    try {
      await api.patch(`/api/auth/atualizar-senha/${token}`, { senha });
      toast.success('Senha atualizada com sucesso!');
      navigate('/');
    } catch (err) {
      setErro('Erro ao atualizar a senha. Link inválido ou expirado.');
    }
  };

  return (
    <div className="container mt-5">
      <h3>Atualizar Senha</h3>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label text-dark">Nova Senha</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-lock"></i>
            </span>
            <input
              type="password"
              className="form-control"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              onFocus={() => setSenhaFocada(true)}
              onBlur={() => setSenhaFocada(false)}
              required
            />
          </div>
          {senhaFocada && (
            <ul className="mt-2 small">
              <li className={requisitosStatus.tamanho ? 'text-success' : 'text-danger'}>
                Mínimo 8 caracteres
              </li>
              <li className={requisitosStatus.maiuscula ? 'text-success' : 'text-danger'}>
                Letra maiúscula
              </li>
              <li className={requisitosStatus.minuscula ? 'text-success' : 'text-danger'}>
                Letra minúscula
              </li>
              <li className={requisitosStatus.numero ? 'text-success' : 'text-danger'}>
                Número
              </li>
              <li className={requisitosStatus.especial ? 'text-success' : 'text-danger'}>
                Caractere especial (@$!%*?&)
              </li>
            </ul>
          )}
        </div>

        <div className="mb-3">
          <label className="form-label text-dark">Confirmar Senha</label>
          <div className="input-group">
            <span className="input-group-text">
              <i className="fas fa-lock"></i>
            </span>
            <input
              type="password"
              className="form-control"
              value={confirmarSenha}
              onChange={(e) => setConfirmarSenha(e.target.value)}
              required
            />
          </div>
        </div>

        <button type="submit" className="btn btn-success">Atualizar Senha</button>
      </form>
      {erro && <div className="alert alert-danger mt-3">{erro}</div>}
    </div>
  );
}
