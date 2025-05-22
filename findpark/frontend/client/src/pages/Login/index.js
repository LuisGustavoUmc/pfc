import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./login.css";
import "bootstrap/dist/css/bootstrap.min.css";
import logo from "../../assets/logo-findpark.png";
import imgTelaLogin from "../../assets/img-telalogin-esquerda.jpg";
import api from "../../services/api";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [errors, setErrors] = useState({ email: "", senha: "" });
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const [loginError, setLoginError] = useState("");

  // Função de Validação
  const validate = () => {
    const newErrors = { email: "", senha: "" };
    if (!email) {
      newErrors.email = "Email é obrigatório";
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = "Email inválido";
    }

    if (!senha) {
      newErrors.senha = "Senha é obrigatória";
    }

    setErrors(newErrors);
    return Object.values(newErrors).every((error) => error === "");
  };

  async function login(e) {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    setIsLoading(true);
    setLoginError("");

    const data = {
      email,
      senha,
    };

    try {
      const response = await api.post("api/auth/login", data);

      const role = response.data.role;

      localStorage.setItem("email", email);
      localStorage.setItem("accessToken", response.data.accessToken);
      localStorage.setItem("userRole", response.data.role);

      if (role === "ADMIN") {
        navigate("/admin"); // Página do admin
      } else if (role === "CLIENTE") {
        navigate("/home-cliente"); // Página do cliente
      } else if (role === "PROPRIETARIO") {
        navigate("/home-proprietario"); // Página do proprietário
      }
    } catch (err) {
      setLoginError("Usuário ou senha incorretos. Tente novamente.");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="row vh-100 g-0">
      {/* Lado esquerdo */}
      <div className="col-lg-6 position-relative d-none d-lg-block">
        <div
          className="bg-holder"
          style={{ backgroundImage: `url(${imgTelaLogin})` }}
        ></div>
      </div>

      {/* Lado direito */}
      <div className="col-lg-6">
        <div className="row align-items-center justify-content-center h-100 g-0 px-4 px-sm-0">
          <div className="col col-sm-6 col-lg-7 col-xl-6">
            {/* Logo */}
            <Link className="d-flex justify-content-center mb-4">
              <img src={logo} alt="FindPark" width={100} />
            </Link>

            <div className="text-center mb-5">
              <h3 className="fw-bold">Entrar</h3>
              <p className="text-secondary">Acesse sua conta</p>
            </div>

            {/* Formulário */}
            <form onSubmit={login}>
              <div className="input-group mb-3">
                <span className="input-group-text">
                  <i className="fa-solid fa-envelope"></i>
                </span>
                <input
                  type="email"
                  className={`form-control form-control-lg fs-6 ${errors.email ? "is-invalid" : ""}`}
                  placeholder="E-mail"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  onFocus={() => setLoginError("")}
                />
                {errors.email && (
                  <div className="invalid-feedback">{errors.email}</div>
                )}
              </div>
              <div className="input-group mb-3">
                <span className="input-group-text">
                  <i className="fa-solid fa-lock"></i>
                </span>
                <input
                  type="password"
                  className={`form-control form-control-lg fs-6 ${errors.senha ? "is-invalid" : ""}`}
                  placeholder="Senha"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  onFocus={() => setLoginError("")}
                />
                {errors.senha && (
                  <div className="invalid-feedback">{errors.senha}</div>
                )}
              </div>
              <div className="input-group mb-3 d-flex justify-content-between">
                <div className="form-check">
                  <input
                    type="checkbox"
                    className="form-check-input"
                    id="formCheck"
                  />
                  <label
                    htmlFor="formCheck"
                    className="form-check-label text-secondary"
                  >
                    <small>Lembrar de mim</small>
                  </label>
                </div>
                <div>
                  <small>
                    <Link to="/recuperar-senha">Esqueceu a senha?</Link>
                  </small>
                </div>
              </div>
              <button
                type="submit"
                className="btn btn-primary btn-lg w-100 mb-3"
                disabled={isLoading}
              >
                {isLoading ? "Carregando..." : "Entrar"}
              </button>

              {loginError && (
                <div className="text-danger text-center mt-2">{loginError}</div>
              )}
            </form>

            {/* Fomulário */}

            <div className="text-center">
              <small>
                Não tem uma conta?{" "}
                <Link to={"/nova-conta"}>Criar nova conta</Link>
              </small>
            </div>
          </div>
        </div>
      </div>
      {/* Lado direito */}
    </div>
  );
}
