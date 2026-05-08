const form = document.getElementById("clienteForm");
const clienteId = document.getElementById("clienteId");
const nome = document.getElementById("nome");
const email = document.getElementById("email");
const telefone = document.getElementById("telefone");
const cpf = document.getElementById("cpf");
const endereco = document.getElementById("endereco");
const observacoes = document.getElementById("observacoes");
const clientesBody = document.getElementById("clientesBody");
const statusText = document.getElementById("statusText");
const formTitle = document.getElementById("formTitle");
const submitButton = document.getElementById("submitButton");
const cancelEditButton = document.getElementById("cancelEditButton");
const searchForm = document.getElementById("searchForm");
const searchInput = document.getElementById("searchInput");
const statusFilter = document.getElementById("statusFilter");
const clearSearchButton = document.getElementById("clearSearchButton");
const totalClientes = document.getElementById("totalClientes");
const clientesAtivos = document.getElementById("clientesAtivos");
const clientesInativos = document.getElementById("clientesInativos");

function getClienteFromForm() {
    return {
        nome: nome.value,
        email: email.value,
        telefone: telefone.value,
        cpf: cpf.value,
        endereco: endereco.value,
        observacoes: observacoes.value,
        ativo: true
    };
}

function fillForm(cliente) {
    clienteId.value = cliente.id;
    nome.value = cliente.nome;
    email.value = cliente.email;
    telefone.value = cliente.telefone;
    cpf.value = cliente.cpf;
    endereco.value = cliente.endereco;
    observacoes.value = cliente.observacoes || "";
    formTitle.textContent = "Editar cliente";
    submitButton.textContent = "Atualizar cliente";
    cancelEditButton.classList.remove("hidden");
    nome.focus();
}

function clearForm() {
    form.reset();
    clienteId.value = "";
    formTitle.textContent = "Novo cliente";
    submitButton.textContent = "Salvar cliente";
    cancelEditButton.classList.add("hidden");
}

async function requestJson(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        },
        ...options
    });

    if (!response.ok) {
        const errorBody = await response.json().catch(() => null);
        const message = errorBody?.erros?.join("\n") || errorBody?.mensagem || `Erro HTTP ${response.status}`;

        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

async function loadClientes() {
    const query = searchInput.value.trim();
    const params = new URLSearchParams();

    if (query) {
        params.set("termo", query);
    }

    if (statusFilter.value) {
        params.set("ativo", statusFilter.value);
    }

    const url = params.toString() ? `/clientes?${params.toString()}` : "/clientes";

    statusText.textContent = "Carregando...";

    try {
        const clientes = await requestJson(url);
        renderClientes(clientes);
        statusText.textContent = `${clientes.length} cliente(s)`;
    } catch (error) {
        statusText.textContent = "Erro ao carregar";
    }
}

async function loadResumo() {
    const resumo = await requestJson("/clientes/resumo");

    totalClientes.textContent = resumo.total;
    clientesAtivos.textContent = resumo.ativos;
    clientesInativos.textContent = resumo.inativos;
}

function renderClientes(clientes) {
    clientesBody.innerHTML = "";

    if (clientes.length === 0) {
        const row = document.createElement("tr");
        row.innerHTML = `<td class="empty-row" colspan="9">Nenhum cliente cadastrado</td>`;
        clientesBody.appendChild(row);
        return;
    }

    clientes.forEach((cliente) => {
        const row = document.createElement("tr");
        const ativo = cliente.ativo !== false;
        const statusLabel = ativo ? "Ativo" : "Inativo";
        const statusClass = ativo ? "active" : "inactive";
        const updatedAt = cliente.atualizadoEm ? new Date(cliente.atualizadoEm).toLocaleString("pt-BR") : "";

        row.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.nome || ""}</td>
            <td>${cliente.email || ""}</td>
            <td>${cliente.telefone || ""}</td>
            <td>${cliente.cpf || ""}</td>
            <td>${cliente.endereco || ""}</td>
            <td><span class="badge ${statusClass}">${statusLabel}</span></td>
            <td>${updatedAt}</td>
            <td>
                <div class="actions">
                    <button type="button" class="secondary" data-action="edit">Editar</button>
                    <button type="button" class="secondary" data-action="toggle">${ativo ? "Desativar" : "Ativar"}</button>
                    <button type="button" class="danger" data-action="delete">Excluir</button>
                </div>
            </td>
        `;

        row.querySelector("[data-action='edit']").addEventListener("click", () => fillForm(cliente));
        row.querySelector("[data-action='toggle']").addEventListener("click", () => toggleCliente(cliente.id, ativo));
        row.querySelector("[data-action='delete']").addEventListener("click", () => deleteCliente(cliente.id));
        clientesBody.appendChild(row);
    });
}

async function saveCliente(event) {
    event.preventDefault();

    const id = clienteId.value;
    const isEditing = Boolean(id);
    const url = isEditing ? `/clientes/${id}` : "/clientes";
    const method = isEditing ? "PUT" : "POST";

    try {
        const payload = getClienteFromForm();

        if (isEditing) {
            delete payload.ativo;
        }

        await requestJson(url, {
            method,
            body: JSON.stringify(payload)
        });

        clearForm();
        await refresh();
    } catch (error) {
        window.alert(error.message);
    }
}

async function toggleCliente(id, ativo) {
    const action = ativo ? "desativar" : "ativar";

    await requestJson(`/clientes/${id}/${action}`, { method: "PATCH" });
    await refresh();
}

async function deleteCliente(id) {
    const confirmed = window.confirm("Deseja excluir este cliente?");

    if (!confirmed) {
        return;
    }

    await requestJson(`/clientes/${id}`, { method: "DELETE" });
    await refresh();
}

async function refresh() {
    await Promise.all([loadClientes(), loadResumo()]);
}

form.addEventListener("submit", saveCliente);
cancelEditButton.addEventListener("click", clearForm);
searchForm.addEventListener("submit", (event) => {
    event.preventDefault();
    loadClientes();
});
clearSearchButton.addEventListener("click", () => {
    searchInput.value = "";
    statusFilter.value = "";
    loadClientes();
});

statusFilter.addEventListener("change", loadClientes);

refresh();
