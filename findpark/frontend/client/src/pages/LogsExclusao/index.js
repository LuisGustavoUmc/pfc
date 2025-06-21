import { useEffect, useState } from "react";
import api from "../../services/api";

const LogsDeExclusao = () => {
  const [logs, setLogs] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  useEffect(() => {
    buscarLogs();
  }, [pagina]);

  const buscarLogs = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      const response = await api.get("/api/logs", {
        params: { page: pagina, size: 10, direction: "desc" },
        headers: { Authorization: `Bearer ${token}` },
      });
      setLogs(response.data.content);
      setTotalPaginas(response.data.totalPages);
    } catch (error) {
      console.error("Erro ao buscar logs:", error);
    }
  };

  return (
    <div className="container-lg">
      <h2 className="mb-4">Logs de Exclusões</h2>

      <div className="table-responsive">
        <table className="table table-striped table-bordered">
          <thead className="table-dark">
            <tr>
              <th>Tipo</th>
              <th>ID Referenciado</th>
              <th>Usuário</th>
              <th>Descrição</th>
              <th>Data e Hora</th>
            </tr>
          </thead>
          <tbody>
            {logs.length > 0 ? (
              logs.map((log) => (
                <tr key={log.id}>
                  <td>{log.entidade}</td>
                  <td>{log.entidadeId}</td>
                  <td>{log.usuarioResponsavelId}</td>
                  <td>{log.descricao}</td>
                  <td>{new Date(log.dataHora).toLocaleString()}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className="text-center">
                  Nenhum log encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="d-flex justify-content-between align-items-center">
        <button
          className="btn btn-primary"
          disabled={pagina === 0}
          onClick={() => setPagina(pagina - 1)}
        >
          Anterior
        </button>

        <span>
          Página {totalPaginas === 0 ? 1 : pagina + 1} de{" "}
          {totalPaginas === 0 ? 1 : totalPaginas}
        </span>

        <button
          className="btn btn-primary"
          disabled={logs.length < 10}
          onClick={() => setPagina(pagina + 1)}
        >
          Próxima
        </button>
      </div>
    </div>
  );
};

export default LogsDeExclusao;
