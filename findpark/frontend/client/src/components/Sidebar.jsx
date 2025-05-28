import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./css/styles.css";

export default function Sidebar() {
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);

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
  } else if (role === "ADMIN") {
    homePath = "/admin";
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
                  <i class="fa-solid fa-square-parking"></i>
                  <span>Estacionamentos</span>
                </Link>
              </li>
              <li className="sidebar-item">
                <Link to="/reservas-proprietario"  className="sidebar-link">
                  <i className="fa-solid fa-calendar-days"></i>
                  <span>Reservas</span>
                </Link>
              </li>
            </>
          )}

          {role === "ADMIN" && (
            <>
            <li className="sidebar-item">
              <Link to="/admin" className="sidebar-link">
                <i class="fa-solid fa-gauge"></i>
                <span>Dashboard</span>
              </Link>
            </li>

            <li className="sidebar-item">
              <Link to="/usuarios" className="sidebar-link">
                <i class="fa-solid fa-users"></i>
                <span>Gerenciar Usuários</span>
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
          <Link href="#" className="sidebar-link" onClick={handleLogoutClick}>
            <i className="fa-solid fa-arrow-right-from-bracket"></i>
            <span>Sair</span>
          </Link>
        </div>
      </aside>
    </div>
  );
}
