import React, { useState } from 'react';
import api from '../../services/api';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

export default function RecuperarSenha() {
  const [email, setEmail] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await api.post('/api/auth/recuperar', { email });
      toast.success('E-mail de recuperação enviado com sucesso!');

      setTimeout(() => {
        navigate('/');
      }, 3000); // aguarda 3 segundos para exibir o toast
    } catch (err) {
      toast.error('Erro ao enviar e-mail. Verifique o endereço digitado.');
    }
  };

  return (
    <div className="container mt-5">
      <h3>Recuperar Senha</h3>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label text-dark">E-mail cadastrado</label>
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
    </div>
  );
}
