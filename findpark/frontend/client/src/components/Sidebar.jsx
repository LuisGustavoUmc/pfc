import React, { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import "./css/styles.css";

export default function Sidebar() {
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);

  const { estacionamentoId } = useParams();

  function toggleSidebar() {
    setExpanded(!expanded);
  }

  const role = localStorage.getItem("userRole");

  if (!role) {
    return (
      <div className="sidebar bg-dark text-white p-3">
        <p>Você não tem permissão para acessar esta área. Faça login primeiro.</p>
      </div>
    );
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
  if (role === "CLIENTE") {
    homePath = "/home-cliente";
  } else if (role === "PROPRIETARIO") {
    homePath = "/home-proprietario";
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
                <Link to={homePath} className="sidebar-link">
                  <i className="fa-solid fa-square-parking"></i>
                  <span>Vagas</span>
                </Link>
              </li>
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
            </>
          )}

          {role === "PROPRIETARIO" && (
            <>
              <li className="sidebar-item">
                <Link to="/home-proprietario" className="sidebar-link">
                  <i className="lni lni-map-marker"></i>
                  <span>Meus Estacionamentos</span>
                </Link>
              </li>
              <li className="sidebar-item">
                <Link to={`/estacionamentos/${estacionamentoId}/gerenciar-vagas`} className="sidebar-link">
                  <i className="lni lni-parking"></i>
                  <span>Gerenciar Vagas</span>
                </Link>
              </li>
            </>
          )}

          {role === "ADMIN" && (
            <li className="sidebar-item">
              <Link to="/admin" className="sidebar-link">
                <i className="lni lni-cog"></i>
                <span>Painel Admin</span>
              </Link>
            </li>
          )}

          <li className="sidebar-item">
            <Link to="/perfil" className="sidebar-link">
              <i className="fa-solid fa-user"></i>
              <span>Perfil</span>
            </Link>
          </li>

          <li className="sidebar-item">
            <Link to="#" className="sidebar-link">
              <i className="fa-solid fa-bell"></i>
              <span>Notificações</span>
            </Link>
          </li>

          <li className="sidebar-item">
            <Link to="#" className="sidebar-link">
              <i className="fa-solid fa-gear"></i>
              <span>Configurações</span>
            </Link>
          </li>
        </ul>

        <div className="sidebar-footer">
          <Link href="#" className="sidebar-link" onClick={handleLogoutClick}>
            <i className="fa-solid fa-arrow-right-from-bracket"></i>
            <span>Sair</span>
          </Link>
        </div>
      </aside>
    </div>
  );
}
