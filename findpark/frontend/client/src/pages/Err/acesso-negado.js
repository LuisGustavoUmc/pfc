import React from 'react';
import { Link } from 'react-router-dom';

const Forbidden = () => (
  <div className="flex flex-col items-center justify-center h-screen text-center">
    <h1 className="text-4xl font-bold text-red-600 mb-4">403 - Acesso negado</h1>
    <p className="text-gray-600 mb-6">Você não tem permissão para acessar esta página.</p>
    <Link to="/" className="text-blue-500 hover:underline">Voltar à página inicial</Link>
  </div>
);

export default Forbidden;
