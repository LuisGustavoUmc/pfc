import React, { useEffect, useState } from "react";
import api from "../../services/api";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import "bootstrap/dist/css/bootstrap.min.css";
import "./styles.css";

export default function AdminDashboard() {
  const [dados, setDados] = useState(null);
  const [isSmallScreen, setIsSmallScreen] = useState(false);

  // Detectar tamanho da tela
  useEffect(() => {
    function handleResize() {
      setIsSmallScreen(window.innerWidth < 768); // breakpoint para mobile, pode ajustar
    }

    handleResize(); // checar tamanho inicial
    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    api
      .get("/api/admin/dashboard")
      .then((res) => setDados(res.data))
      .catch((err) => console.error("Erro ao carregar dashboard", err));
  }, []);

  if (!dados)
    return (
      <div className="p-3 text-center" style={{ minHeight: "100vh" }}>
        Carregando...
      </div>
    );

  const chartData = [
    { nome: "Clientes", valor: dados.totalClientes },
    { nome: "Proprietários", valor: dados.totalProprietarios },
    { nome: "Estacionamentos", valor: dados.totalEstacionamentos },
    { nome: "Vagas", valor: dados.totalVagas },
    { nome: "Reservas", valor: dados.totalReservas },
  ];

  return (
    <div
      className="container-fluid"
      style={{ minHeight: "100vh", paddingTop: "2rem", paddingBottom: "2rem" }}
    >
      <h1 className="text-center mb-5" style={{ fontWeight: "700" }}>
        Dashboard Admin
      </h1>

      <div className="row justify-content-center g-4 mb-5">
        <div className="col-12 col-sm-6 col-md-3">
          <div className="card shadow-sm p-4 h-100 d-flex flex-column justify-content-center align-items-center">
            <strong style={{ fontSize: "1.1rem" }}>Total Usuários:</strong>
            <span style={{ fontSize: "2.5rem", fontWeight: "700" }}>
              {dados.totalUsuarios}
            </span>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-md-3">
          <div className="card shadow-sm p-4 h-100 d-flex flex-column justify-content-center align-items-center">
            <strong style={{ fontSize: "1.1rem" }}>
              Total Estacionamentos:
            </strong>
            <span style={{ fontSize: "2.5rem", fontWeight: "700" }}>
              {dados.totalEstacionamentos}
            </span>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-md-3">
          <div className="card shadow-sm p-4 h-100 d-flex flex-column justify-content-center align-items-center">
            <strong style={{ fontSize: "1.1rem" }}>Total Vagas:</strong>
            <span style={{ fontSize: "2.5rem", fontWeight: "700" }}>
              {dados.totalVagas}
            </span>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-md-3">
          <div className="card shadow-sm p-4 h-100 d-flex flex-column justify-content-center align-items-center">
            <strong style={{ fontSize: "1.1rem" }}>Total Reservas:</strong>
            <span style={{ fontSize: "2.5rem", fontWeight: "700" }}>
              {dados.totalReservas}
            </span>
          </div>
        </div>
      </div>

      <div style={{ width: "100%", height: 250 }}>
        <ResponsiveContainer>
          <BarChart
            data={chartData}
            margin={{ top: 10, right: 30, left: 0, bottom: 10 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="nome"
              tick={isSmallScreen ? false : { fontSize: 14, fontWeight: "600" }}
              interval={0}
              {...(isSmallScreen
                ? {
                    angle: -45,
                    textAnchor: "end",
                    height: 0,
                  }
                : {})}
            />
            <YAxis
              allowDecimals={false}
              tick={{ fontSize: 14, fontWeight: "600" }}
              width={40}
            />
            <Tooltip
              contentStyle={{ fontSize: "1rem" }}
              cursor={{ fill: "rgba(136, 132, 216, 0.2)" }}
            />
            <Bar
              dataKey="valor"
              fill="#8884d8"
              barSize={40}
              radius={[6, 6, 0, 0]}
            />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
