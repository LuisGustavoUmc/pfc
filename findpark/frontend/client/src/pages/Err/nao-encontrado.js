import React from 'react';
import { Link } from 'react-router-dom';

const NotFound = () => (
  <div className="flex flex-col items-center justify-center h-screen text-center">
    <h1 className="text-4xl font-bold text-gray-800 mb-4">404 - Página não encontrada</h1>
    <p className="text-gray-600 mb-6">A página que você procura não existe.</p>
    <Link to="/" className="text-blue-500 hover:underline">Voltar à página inicial</Link>
  </div>
);

export default NotFound;
