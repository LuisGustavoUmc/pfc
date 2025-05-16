import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const GerenciarVagas = () => {
  const { estacionamentoId } = useParams();
  const [vagas, setVagas] = useState([]);

  useEffect(() => {
    console.log("Estacionamento ID:", estacionamentoId); // Verifique o valor
    const token = localStorage.getItem("accessToken");

    const fetchVagas = async () => {
      try {
        const response = await axios.get(`/api/vagas/estacionamento/${estacionamentoId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setVagas(response.data);
      } catch (error) {
        console.error("Erro ao buscar vagas:", error);
      }
    };

    if (estacionamentoId) {
      fetchVagas();
    } else {
      console.error("Estacionamento ID não fornecido.");
    }
  }, [estacionamentoId]);

  return (
    <div>
      <h1>Gerenciar Vagas do Estacionamento</h1>
      <ul>
        {vagas.map(vaga => (
          <li key={vaga.id}>
            {vaga.nome} - {vaga.status}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default GerenciarVagas;
