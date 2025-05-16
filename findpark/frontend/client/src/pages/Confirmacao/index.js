import { useEffect, useRef } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import api from "../../services/api";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function Confirmacao() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const token = params.get("token");
  const executed = useRef(false);

  useEffect(() => {
    if (!token) {
      toast.error("Token não informado.");
      setTimeout(() => navigate("/"), 3000);
      return;
    }

    if (executed.current) return;
    executed.current = true;

    const confirmar = async () => {
      try {
        const { data } = await api.get(`/api/auth/confirmar-cadastro/${token}`);
        const { sucesso, mensagem } = data;

        console.log("Resposta da API:", data); // Verifique a resposta da API no console

        if (sucesso) {
          // Exibir o toast de sucesso com a mensagem correta
          toast.success(mensagem || "Cadastro confirmado com sucesso! Faça login.", {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            theme: "colored",
            style: {
              backgroundColor: "#4CAF50", 
              color: "#fff",
              fontWeight: "bold",
            },
          });
        } else {
          // Caso o backend retorne erro
          toast.error(mensagem || "Token inválido ou já utilizado.", {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            theme: "colored",
            style: {
              backgroundColor: "#f44336", // Cor vermelha
              color: "#fff",
              fontWeight: "bold",
            },
          });
        }
      } catch (error) {
        console.error("Erro de rede ou inesperado:", error);
        toast.error("Erro ao confirmar cadastro. Tente novamente.", {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          theme: "colored",
          style: {
            backgroundColor: "#f44336", // Cor vermelha
            color: "#fff",
            fontWeight: "bold",
          },
        });
      } finally {
        setTimeout(() => navigate("/"), 3000);
      }
    };

    confirmar();
  }, [token, navigate]);

  return <ToastContainer position="top-center" autoClose={5000} />;
}
