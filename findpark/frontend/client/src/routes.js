import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import Login from './pages/Login';
import Vaga from "./pages/Vaga";
import Estacionamento from './pages/Estacionamento'
import Admin from './pages/Admin'
import AppLayout from "./components/AppLayout";
import RecuperarSenha from "./pages/RecuperarSenha";
import AtualizarSenha from "./pages/AtualizarSenha";
import Confirmacao from "./pages/Confirmacao";
import NovaConta from "./pages/NovaConta";
import HomeCLiente from "./pages/HomeCliente"
import VagaDetalhes from "./pages/VagaDetalhes";
import GerenciarPlacas from "./pages/GerenciarPlacas";
import HomeProprietario from "./pages/HomeProprietario"
import DetalhesEstacionamento from "./pages/EstacionamentoDetalhes";
import CadastrarVaga from "./pages/CadastrarVaga";
import GerenciarVagas from "./pages/GerenciarVagas";
import ReservaDetalhes from "./pages/ReservaDetalhes";
import Perfil from "./pages/Perfil";
import TrocarSenha from "./pages/TrocarSenha"
import AlterarEmail from "./pages/AlterarEmail"; 
import NovoEstacionamento from "./pages/NovoEstacionamento";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login fora do layout */}
        <Route path="/" exact element={<Login />} />
        <Route path="/recuperar-senha" element={<RecuperarSenha />} />
        <Route path="/atualizar-senha/:token" element={<AtualizarSenha />} />
        <Route path="/confirmacao" element={<Confirmacao />} />
        <Route path="/nova-conta" element={<NovaConta />} />


        {/* Rotas protegidas com layout */}
        <Route element={<AppLayout />}>
          <Route path="/vagas" element={<Vaga />} />
          <Route path="/estacionamentos" element={<Estacionamento />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/home-cliente" element={<HomeCLiente />} />
          <Route path="/home-proprietario" element={<HomeProprietario />} />
          <Route path="/vagas/:id" element={<VagaDetalhes />} />
          <Route path="/cliente/placas" element={<GerenciarPlacas />} />
          <Route path="/estacionamentos/:id" element={<DetalhesEstacionamento />} />
          <Route path="/estacionamentos/:estacionamentoId/cadastrar-vaga" element={<CadastrarVaga />} />
          <Route path="/estacionamentos/:estacionamentoId/gerenciar-vagas" element={<GerenciarVagas />} />
          <Route path="/reservas" element={<ReservaDetalhes />} />
          <Route path="/perfil" element={<Perfil />} />
          <Route path="/trocar-senha" element={<TrocarSenha />} />
          <Route path="/alterar-email" element={<AlterarEmail />} />
          <Route path="/estacionamentos/novo" element={<NovoEstacionamento />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
