import React from 'react';
import { Link } from 'react-router-dom';

const InternalServerError = () => (
  <div className="flex flex-col items-center justify-center h-screen text-center">
    <h1 className="text-4xl font-bold text-red-700 mb-4">500 - Erro interno do servidor</h1>
    <p className="text-gray-600 mb-6">Algo deu errado. Tente novamente mais tarde.</p>
    <Link to="/" className="text-blue-500 hover:underline">Voltar à página inicial</Link>
  </div>
);

export default InternalServerError;
