import React from "react";
import Sidebar from "./Sidebar";
import { Outlet } from "react-router-dom";
import './css/styles.css'; 

export default function AppLayout() {
  return (
    <div className="wrapper">
      <Sidebar />
      <div className="main">
        <Outlet />
      </div>
    </div>
  );
}
