import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080"
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status } = error.response;

      if (status === 401) {
        // Remove tokens antigos
        localStorage.removeItem("accessToken");

        // Redireciona para login com query param opcional
        window.location.href = "/?sessionExpired=true";
      } else if (status === 403) {
        window.location.href = "/acesso-negado";
      } else if (status === 500) {
        window.location.href = "/erro-interno";
      } 
    }
    return Promise.reject(error);
  }
);


export default api;
