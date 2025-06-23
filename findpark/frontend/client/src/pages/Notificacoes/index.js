import { useEffect, useState } from "react";
import api from "../../services/api";

const ListaNotificacoes = () => {
  const [notificacoes, setNotificacoes] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  const buscarNotificacoes = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      const res = await api.get("/api/notificacoes", {
        params: { page: pagina, size: 10, direction: "desc" },
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotificacoes(res.data.content);
      setTotalPaginas(res.data.totalPages);
    } catch (error) {
      console.error("Erro ao buscar notificações:", error);
    }
  };

  useEffect(() => {
    buscarNotificacoes();
  }, [pagina]);

  const marcarComoLida = async (id) => {
    try {
      const token = localStorage.getItem("accessToken");
      await api.post(`/api/notificacoes/${id}/ler`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      buscarNotificacoes();
    } catch (error) {
      console.error("Erro ao marcar como lida:", error);
    }
  };

  return (
    <div className="container-lg py-4">
      <h2>Minhas Notificações</h2>
      <ul className="list-group">
        {notificacoes.length === 0 && (
          <li className="list-group-item">Nenhuma notificação.</li>
        )}
        {notificacoes.map((n) => (
          <li
            key={n.id}
            className={`list-group-item d-flex justify-content-between align-items-center ${
              n.lida ? "" : "fw-bold"
            }`}
          >
            {n.mensagem}
            {!n.lida && (
              <button
                className="btn btn-sm btn-outline-primary"
                onClick={() => marcarComoLida(n.id)}
              >
                Marcar como lida
              </button>
            )}
          </li>
        ))}
      </ul>

      <div className="d-flex justify-content-between mt-3">
        <button
          className="btn btn-outline-primary"
          disabled={pagina === 0}
          onClick={() => setPagina(pagina - 1)}
        >
          Anterior
        </button>
        <span>
          Página {pagina + 1} de {totalPaginas === 0 ? 1 : totalPaginas}
        </span>
        <button
          className="btn btn-outline-primary"
          disabled={pagina + 1 >= totalPaginas}
          onClick={() => setPagina(pagina + 1)}
        >
          Próxima
        </button>
      </div>
    </div>
  );
};

export default ListaNotificacoes;
