import React, { useState, useEffect } from "react";
import { useLocation, useNavigate, Link } from "react-router-dom";
import "./login.css";
import "bootstrap/dist/css/bootstrap.min.css";
import logo from "../../assets/logo-findpark.png";
import imgTelaLogin from "../../assets/img-telalogin-esquerda.jpg";
import api from "../../services/api";
import { toast } from "react-toastify";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [errors, setErrors] = useState({ email: "", senha: "" });
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();
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

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get("sessionExpired") === "true") {
      toast.warn("Sua sessão expirou. Faça login novamente.");
      navigate("/", { replace: true });
    }
  }, [location, navigate]);

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
        navigate("/admin");
      } else if (role === "CLIENTE") {
        navigate("/home-cliente");
      } else if (role === "PROPRIETARIO") {
        navigate("/home-proprietario");
      }
    } catch (err) {
      if (err.response) {
        const status = err.response.status;

        if (status === 401 || status === 400 || status === 404) {
          setLoginError("Usuário ou senha incorretos. Tente novamente.");
        } else if (status === 403) {
          navigate("/acesso-negado");
        } else {
          setLoginError("Erro inesperado. Tente novamente.");
        }
      } else {
        setLoginError("Erro de conexão com o servidor.");
      }
    } finally {
      setIsLoading(false); // <-- garante que o botão volte ao normal
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
