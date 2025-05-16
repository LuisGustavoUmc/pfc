import React from "react";
import './global.css';
import Routes from "./routes";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function App() {
  return (
    <>
      <Routes />
      <ToastContainer />
    </>
  );
}
