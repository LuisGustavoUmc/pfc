import React, { useState } from 'react';
import api from '../../services/api';

export default function RecuperarSenha() {
  const [email, setEmail] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensagem('');
    setErro('');

    try {
      await api.post('/api/auth/recuperar', { email });
      setMensagem('E-mail de recuperação enviado com sucesso!');
    } catch (err) {
      setErro('Erro ao enviar e-mail. Verifique o endereço digitado.');
    }
  };

  return (
    <div className="container mt-5">
      <h3>Recuperar Senha</h3>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">E-mail cadastrado</label>
          <input
            type="email"
            className="form-control"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary">Recuperar Conta</button>
      </form>
      {mensagem && <div className="alert alert-success mt-3">{mensagem}</div>}
      {erro && <div className="alert alert-danger mt-3">{erro}</div>}
    </div>
  );
}
