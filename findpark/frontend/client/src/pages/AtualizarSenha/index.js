import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../services/api';

export default function AtualizarSenha() {
  const { token } = useParams();
  const [senha, setSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const validarSenha = (senha) => {
    const regex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return regex.test(senha);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');

    if (senha !== confirmarSenha) {
      setErro('As senhas não coincidem.');
      return;
    }

    if (!validarSenha(senha)) {
      setErro('A senha deve conter ao menos 8 caracteres, incluindo letra maiúscula, minúscula, número e caractere especial.');
      return;
    }

    try {
      await api.patch(`/api/auth/atualizar-senha/${token}`, { senha });
      alert('Senha atualizada com sucesso!');
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
          <label className="form-label">Nova Senha</label>
          <input
            type="password"
            className="form-control"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Confirmar Senha</label>
          <input
            type="password"
            className="form-control"
            value={confirmarSenha}
            onChange={(e) => setConfirmarSenha(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn btn-success">Atualizar Senha</button>
      </form>
      {erro && <div className="alert alert-danger mt-3">{erro}</div>}
    </div>
  );
}
