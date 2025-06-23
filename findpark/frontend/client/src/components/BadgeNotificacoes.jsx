import { useEffect, useState } from "react";
import api from "../services/api";
import { Link } from "react-router-dom";

const BadgeNotificacoes = () => {
  const [naoLidas, setNaoLidas] = useState(0);

  const buscarNaoLidas = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      const res = await api.get("/api/notificacoes/nao-lidas", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNaoLidas(res.data);
    } catch (error) {
      console.error("Erro ao buscar notificações não lidas:", error);
    }
  };

  useEffect(() => {
    buscarNaoLidas();
    const interval = setInterval(buscarNaoLidas, 30000); // Atualiza a cada 30s
    return () => clearInterval(interval);
  }, []);

  return (
    <Link to="/notificacoes" className="btn btn-light position-relative">
      <i className="fa-solid fa-bell"></i>
      {naoLidas > 0 && (
        <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
          {naoLidas}
        </span>
      )}
    </Link>
  );
};

export default BadgeNotificacoes;
