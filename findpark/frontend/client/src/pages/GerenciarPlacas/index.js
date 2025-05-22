// imports no topo
import React, { useEffect, useState } from "react";
import api from "../../services/api";
import { toast } from "react-toastify";
import Swal from "sweetalert2";
import "./gerenciarPlacas.css";

export default function GerenciarPlacas() {
  const [placas, setPlacas] = useState([]);
  const [novaPlaca, setNovaPlaca] = useState("");
  const [editandoPlaca, setEditandoPlaca] = useState(null);
  const [placaEditada, setPlacaEditada] = useState("");

  const regexPlacaMercosul = /^[A-Z0-9]{1,7}$/;

  const carregarPlacas = () => {
    api
      .get("/api/clientes/placas")
      .then((response) => {
        const dados = response.data;
        setPlacas(Array.isArray(dados) ? dados : []);
      })
      .catch(() => toast.error("Erro ao carregar placas"));
  };

  useEffect(() => {
    carregarPlacas();
  }, []);

  const validarPlaca = (placa) => regexPlacaMercosul.test(placa);

  const adicionarPlaca = () => {
    const placa = novaPlaca.trim().toUpperCase();

    if (!validarPlaca(placa)) {
      toast.error("Placa inválida. Use até 7 caracteres (letras e números).");
      return;
    }

    if (placas.some((p) => p.toUpperCase() === placa)) {
      toast.warn("Esta placa já está cadastrada.");
      return;
    }

    Swal.fire({
      title: `Deseja adicionar a placa ${placa}?`,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Adicionar",
      cancelButtonText: "Cancelar",
      customClass: {
        popup: "swal-custom",
        confirmButton: "swal-button-confirm",
        cancelButton: "swal-button-cancel",
      },
    }).then((result) => {
      if (!result.isConfirmed) return;

      api
        .post(
          "/api/clientes/placas",
          { placa },
          {
            headers: { "Content-Type": "application/json" },
          }
        )
        .then(() => {
          Swal.fire({
            icon: "success",
            title: "Placa adicionada!",
            timer: 2000,
            showConfirmButton: false,
            customClass: {
              popup: "swal-custom",
            },
          });
          setNovaPlaca("");
          carregarPlacas();
        })
        .catch(() => toast.error("Erro ao adicionar placa."));
    });
  };

  const removerPlaca = (placa) => {
    Swal.fire({
      title: `Remover a placa ${placa}?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sim",
      cancelButtonText: "Cancelar",
      customClass: {
        popup: "swal-custom",
        confirmButton: "swal-button-confirm",
        cancelButton: "swal-button-cancel",
      },
    }).then((result) => {
      if (!result.isConfirmed) return;

      api
        .delete(`/api/clientes/placas?placa=${encodeURIComponent(placa)}`)
        .then(() => {
          toast.success("Placa removida com sucesso!");
          carregarPlacas();
        })
        .catch(() => toast.error("Erro ao remover placa."));
    });
  };

  const iniciarEdicao = (placa) => {
    setEditandoPlaca(placa);
    setPlacaEditada(placa);
  };

  const cancelarEdicao = () => {
    setEditandoPlaca(null);
    setPlacaEditada("");
  };

  const salvarEdicao = () => {
    const nova = placaEditada.trim().toUpperCase();
    if (!validarPlaca(nova)) {
      toast.error("Placa inválida. Use até 7 caracteres (letras e números).");
      return;
    }

    Swal.fire({
      title: `Atualizar placa ${editandoPlaca} para ${nova}?`,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Atualizar",
      cancelButtonText: "Cancelar",
      customClass: {
        popup: "swal-custom",
        confirmButton: "swal-button-confirm",
        cancelButton: "swal-button-cancel",
      },
    }).then((result) => {
      if (!result.isConfirmed) return;

      api
        .put("/api/clientes/placas", {
          antiga: editandoPlaca,
          nova: nova,
        })
        .then(() => {
          toast.success("Placa atualizada com sucesso!");
          cancelarEdicao();
          carregarPlacas();
        })
        .catch(() => toast.error("Erro ao atualizar placa."));
    });
  };

  const filtrarPlacaInput = (valor) => {
    valor = valor.toUpperCase().replace(/[^A-Z0-9]/g, "");
    const letras = valor.match(/[A-Z]/g) || [];
    const numeros = valor.match(/\d/g) || [];

    // impede mais de 7 caracteres
    if (valor.length > 7) {
      valor = valor.slice(0, 7);
    }

    // impede adicionar mais letras se já houver 4 letras e ainda não houver 2 números
    if (letras.length > 4 && numeros.length < 2) {
      // remove o último caractere (assumido como letra inválida)
      valor = valor.slice(0, -1);
    }

    return valor;
  };

  return (
    <div className="container mt-4">
      <h3 className="titulo">Gerenciar Placas</h3>

      <div className="mb-3 d-flex gap-2">
        <input
          type="text"
          className="form-control"
          placeholder="Digite a nova placa (ex: AAA1234 ou AAAA123)"
          value={novaPlaca}
          onChange={(e) => setNovaPlaca(filtrarPlacaInput(e.target.value))}
          maxLength={7}
        />
        <button className="btn btn-azul" onClick={adicionarPlaca}>
          Adicionar
        </button>
      </div>

      <ul className="list-group">
        {placas.map((placa, index) => (
          <li key={index} className="list-group-item placa-item">
            <div className="d-flex justify-content-between align-items-center">
              {editandoPlaca === placa ? (
                <>
                  <input
                    type="text"
                    value={placaEditada}
                    onChange={(e) =>
                      setPlacaEditada(filtrarPlacaInput(e.target.value))
                    }
                    maxLength={7}
                    className="form-control me-2"
                  />
                  <div className="btn-group">
                    <button
                      className="btn btn-success btn-sm"
                      onClick={salvarEdicao}
                    >
                      Salvar
                    </button>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={cancelarEdicao}
                    >
                      Cancelar
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <span>
                    <strong>Placa {index + 1}:</strong> {placa}
                  </span>
                  <div className="btn-group">
                    <button
                      className="btn btn-icon"
                      onClick={() => iniciarEdicao(placa)}
                    >
                      <i className="fas fa-edit"></i>
                    </button>
                    <button
                      className="btn btn-icon"
                      onClick={() => removerPlaca(placa)}
                    >
                      <i className="fas fa-trash-alt"></i>
                    </button>
                  </div>
                </>
              )}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
