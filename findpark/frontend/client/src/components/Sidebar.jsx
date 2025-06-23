import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import "./css/styles.css";

export default function Sidebar() {
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);
  const [naoLidas, setNaoLidas] = useState(0);

  const role = localStorage.getItem("userRole");

  function toggleSidebar() {
    setExpanded(!expanded);
  }

  function logout() {
    localStorage.clear();
    navigate("/");
  }

  function handleLogoutClick(e) {
    e.preventDefault();
    logout();
  }

  let homePath = "/";
  if (role === "CLIENTE") homePath = "/home-cliente";
  else if (role === "PROPRIETARIO") homePath = "/home-proprietario";
  else if (role === "ADMIN") homePath = "/admin";

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
    if (role === "CLIENTE") {
      buscarNaoLidas();

      const interval = setInterval(() => {
        buscarNaoLidas();
      }, 30000); // Atualiza a cada 30 segundos

      return () => clearInterval(interval);
    }
  }, [role]);

  if (!role) {
    return (
      <div className="sidebar bg-dark text-white p-3">
        <p>
          Você não tem permissão para acessar esta área. Faça login primeiro.
        </p>
      </div>
    );
  }

  return (
    <div className="wrapper">
      <aside id="sidebar" className={expanded ? "expand" : ""}>
        <div className="d-flex">
          <button id="toggle-btn" type="button" onClick={toggleSidebar}>
            <i className="fa-solid fa-bars"></i>
          </button>
          <div className="sidebar-logo">
            <Link to={homePath}>FindPark</Link>
          </div>
        </div>

        <ul className="sidebar-nav">
          <li className="sidebar-item">
            <Link to={homePath} className="sidebar-link">
              <i className="fa-solid fa-house"></i>
              <span>Início</span>
            </Link>
          </li>

          {role === "CLIENTE" && (
            <>
              <li className="sidebar-item">
                <Link to="/cliente/placas" className="sidebar-link">
                  <i className="fa-solid fa-car"></i>
                  <span>Placas</span>
                </Link>
              </li>
              <li className="sidebar-item">
                <Link to="/reservas" className="sidebar-link">
                  <i className="fa-solid fa-calendar-days"></i>
                  <span>Reservas</span>
                </Link>
              </li>
              <li className="sidebar-item">
                <Link
                  to="/notificacoes"
                  className="sidebar-link"
                >
                  <div>
                    <i className="fa-solid fa-bell"></i>
                    <span>Notificações</span>
                  </div>

                  {naoLidas > 0 && (
                    <span className="badge bg-danger rounded-pill">
                      {naoLidas}
                    </span>
                  )}
                </Link>
              </li>
            </>
          )}

          {role === "PROPRIETARIO" && (
            <>
              <li className="sidebar-item">
                <Link to="/reservas-proprietario" className="sidebar-link">
                  <i className="fa-solid fa-calendar-days"></i>
                  <span>Reservas</span>
                </Link>
              </li>
            </>
          )}

          {role === "ADMIN" && (
            <>
              <li className="sidebar-item">
                <Link to="/usuarios" className="sidebar-link">
                  <i className="fa-solid fa-users"></i>
                  <span>Gerenciar Usuários</span>
                </Link>
              </li>
              <li className="sidebar-item">
                <Link to="/logs" className="sidebar-link">
                  <i className="fa-solid fa-file"></i>
                  <span>Logs</span>
                </Link>
              </li>
            </>
          )}

          <li className="sidebar-item">
            <Link to="/perfil" className="sidebar-link">
              <i className="fa-solid fa-user"></i>
              <span>Perfil</span>
            </Link>
          </li>
        </ul>

        <div className="sidebar-footer">
          <Link to="#" className="sidebar-link" onClick={handleLogoutClick}>
            <i className="fa-solid fa-arrow-right-from-bracket"></i>
            <span>Sair</span>
          </Link>
        </div>
      </aside>
    </div>
  );
}
