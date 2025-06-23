import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import Login from './pages/Login';
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
import ReservaDetalhes from "./pages/ReservaDetalhes";
import Perfil from "./pages/Perfil";
import TrocarSenha from "./pages/TrocarSenha"
import AlterarEmail from "./pages/AlterarEmail"; 
import NovoEstacionamento from "./pages/NovoEstacionamento";
import ReservasProprietario from "./pages/ReservasProprietario";
import EditarVaga from "./pages/EditarVaga";
import GerenciarUsuarios from "./pages/GerenciarUsuarios"
import EditarUsuario from "./pages/EditarUsuario";
import AcessoNegado from "./pages/Err/acesso-negado"
import ErroInterno from "./pages/Err/erro-interno"
import LogsDeExclusao from "./pages/LogsExclusao";
import Notificacoes from "./pages/Notificacoes";
// import NaoEncotrado from "./pages/Err/nao-encontrado"

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
          <Route path="/admin" element={<Admin />} />
          <Route path="/home-cliente" element={<HomeCLiente />} />
          <Route path="/home-proprietario" element={<HomeProprietario />} />
          <Route path="/vagas/:id" element={<VagaDetalhes />} />
          <Route path="/cliente/placas" element={<GerenciarPlacas />} />
          <Route path="/estacionamentos/:id" element={<DetalhesEstacionamento />} />
          <Route path="/estacionamentos/:estacionamentoId/cadastrar-vaga" element={<CadastrarVaga />} />
          <Route path="/reservas" element={<ReservaDetalhes />} />
          <Route path="/perfil" element={<Perfil />} />
          <Route path="/usuarios/trocar-senha/:id" element={<TrocarSenha />} />
          <Route path="/alterar-email" element={<AlterarEmail />} />
          <Route path="/estacionamentos/novo" element={<NovoEstacionamento />} />
          <Route path="/estacionamentos/editar/:id" element={<NovoEstacionamento />} />
          <Route path="/reservas-proprietario" element={<ReservasProprietario />} />
          <Route path="/vagas/:vagaId/editar" element={<EditarVaga />} />
          <Route path="/usuarios" element={<GerenciarUsuarios />} />
          <Route path="/editar-usuario/:id" element={<EditarUsuario />} />
          <Route path="/logs" element={<LogsDeExclusao />} />
          <Route path="/notificacoes" element={<Notificacoes />} />
          {/* <Route path="*" element={<NaoEncotrado/>} /> */}
          <Route path="/acesso-negado" element={<AcessoNegado/>} />
          <Route path="/erro-interno" element={<ErroInterno/>} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
