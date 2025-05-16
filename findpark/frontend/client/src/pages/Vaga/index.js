import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./styles.css";

import api from "../../services/api";

export default function Vaga() {

  const [vagas, setVagas] = useState([]);

  const accessToken = localStorage.getItem("accessToken");

  const navigate = useNavigate();

  async function editarVaga(id) {
    try {
      navigate(`/vaga/novo/${id}`)
    } catch (err) {
      alert('Falha ao editar! Tente novamente.')
    }
  }

  async function deletarVaga(id) {
    try {
      await api.delete(`findpark/vaga/${id}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })

      setVagas(vagas.filter(vaga => vaga.id !== id))
    } catch (err) {
      alert('Falha ao deletar! Tente novamente.')
    }
  }

  useEffect(() => {
    api.get('findpark/vaga', {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }   //,
          //params: {
          // page: 1,
          // limit: 4,
          // direction:  'asc' 
          // }
    }).then(response => {
      setVagas(response.data) //Response Body no Postman
    })
  });

  return (
    <div className="p-3">
      <div className="text-center">
        <h1>Vagas</h1>
        <Link className="btn btn-primary" to="/vaga/novo/0">Adicionar Vaga</Link>
      </div>

      <ul>
        {vagas.map(vaga => (
          <li key={vaga.id}>
              <strong>Status:</strong>
              <p>{vaga.status}</p>
              <strong>tipo</strong>
              <p>{vaga.tipo}</p>
              <strong>preço</strong>
              <p>{Intl.NumberFormat('pt-BR', {style: 'currency', currency: 'BRL'}).format(vaga.preco)}</p>

              <button onClick={() => editarVaga(vaga.id)} className="btn btn-success"></button>
              <button onClick={() => deletarVaga(vaga.id)} className="btn btn-danger">Apagar</button>
          </li>        
        ))}
      </ul>
    </div>
  );
}
