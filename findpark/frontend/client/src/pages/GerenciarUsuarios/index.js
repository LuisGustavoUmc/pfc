import React, { useEffect, useState } from "react";
import api from "../../services/api"; // axios configurado
import { Button, Spinner } from "react-bootstrap";

export default function AdminUsuariosTabela({ onEditar }) {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [size] = useState(12);
  const [totalPages, setTotalPages] = useState(1);

  // Carregar usuários da API
  async function carregarUsuarios(p = 0) {
    setLoading(true);
    try {
      const res = await api.get("/api/usuarios", {
        params: { page: p, size, direction: "asc" },
      });
      setUsuarios(res.data.content);
      setPage(res.data.number);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      alert("Erro ao carregar usuários");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregarUsuarios();
  }, []);

  // Deletar usuário com confirmação
  async function handleDeletar(id) {
    if (window.confirm("Confirma exclusão do usuário?")) {
      try {
        await api.delete(`/api/usuarios/${id}`);
        alert("Usuário deletado com sucesso");
        carregarUsuarios(page);
      } catch {
        alert("Erro ao deletar usuário");
      }
    }
  }

  return (
    <div>
      {loading ? (
        <Spinner animation="border" />
      ) : (
        <>
          <table className="table table-striped">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Email</th>
                <th>Telefone</th>
                <th>Role</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => (
                <tr key={usuario.id}>
                  <td>{usuario.nome}</td>
                  <td>{usuario.email}</td>
                  <td>{usuario.telefone}</td>
                  <td>{usuario.role}</td>
                  <td>
                    <Button
                      variant="warning"
                      size="sm"
                      className="me-2"
                      onClick={() => onEditar(usuario.id)}
                    >
                      Editar
                    </Button>
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => handleDeletar(usuario.id)}
                    >
                      Deletar
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="d-flex justify-content-between">
            <Button
              disabled={page === 0}
              onClick={() => carregarUsuarios(page - 1)}
            >
              Anterior
            </Button>
            <div>
              Página {page + 1} de {totalPages}
            </div>
            <Button
              disabled={page + 1 >= totalPages}
              onClick={() => carregarUsuarios(page + 1)}
            >
              Próxima
            </Button>
          </div>
        </>
      )}
    </div>
  );
}
