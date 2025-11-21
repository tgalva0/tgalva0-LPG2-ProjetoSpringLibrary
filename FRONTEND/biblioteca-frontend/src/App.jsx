import { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API_URL = 'http://localhost:8080/api';

// Estados iniciais (para limpar formulários)
const initialNewBookData = { titulo: '', autor: '', isbn: '', quantidadeDisponivel: 1 };
const initialNewUserData = { username: '', password: '', nomeCompleto: '', role: 'ROLE_COMUM' };
const initialUserFormData = { nomeCompleto: '', roles: new Set() };
const initialPasswordFormData = { currentPassword: '', newPassword: '', confirmPassword: '' };
// --- 1. NOVOS STATES INICIAIS ---
const initialUserDashboard = { emprestimosAtivos: [] };
const initialAdminDashboard = { totalUsuarios: 0, totalLivros: 0, totalEmprestimosAtivos: 0 };


function App() {
    // --- States de Autenticação ---
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [token, setToken] = useState(null);
    const [userNome, setUserNome] = useState('');
    const [authError, setAuthError] = useState('');
    const [roles, setRoles] = useState([]);

    // --- States da Página Principal ---
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [searchError, setSearchError] = useState('');
    const [generalStatus, setGeneralStatus] = useState(null);
    const [refreshId, setRefreshId] = useState(0);

    // --- States de Admin ---
    const [adminView, setAdminView] = useState('none');
    const [editingBook, setEditingBook] = useState(null);
    const [adminFormData, setAdminFormData] = useState({ titulo: '', autor: '', isbn: '', quantidadeDisponivel: 0 });
    const [newBookData, setNewBookData] = useState(initialNewBookData);
    const [newUserData, setNewUserData] = useState(initialNewUserData);
    const [adminUserSearchTerm, setAdminUserSearchTerm] = useState('');
    const [adminUserSearchResults, setAdminUserSearchResults] = useState([]);
    const [adminUserSearchError, setAdminUserSearchError] = useState('');
    const [editingUser, setEditingUser] = useState(null);
    const [adminUserFormData, setAdminUserFormData] = useState(initialUserFormData);
    const [userRefreshId, setUserRefreshId] = useState(0);

    // --- States de Modais ---
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const [passwordFormData, setPasswordFormData] = useState(initialPasswordFormData);

    // --- 2. NOVOS STATES (Dashboards) ---
    const [userDashboard, setUserDashboard] = useState(initialUserDashboard);
    const [adminDashboard, setAdminDashboard] = useState(initialAdminDashboard);
    const [isAdminDashboardOpen, setIsAdminDashboardOpen] = useState(false);


    const isAdmin = roles.includes('ROLE_ADMIN');

    // --- Funções de Autenticação ---
    const handleSubmitLogin = async (e) => {
        e.preventDefault();
        setAuthError('');
        try {
            const response = await axios.post(`${API_URL}/auth/login`, { username, password });
            setToken(response.data.token);
            setUserNome(response.data.nomeCompleto);
            setRoles(response.data.roles);
        } catch (err) { setAuthError('Usuário ou senha inválidos.'); setToken(null); setUserNome(''); setRoles([]); }
    };
    const handleLogout = () => {
        setToken(null); setUserNome(''); setUsername(''); setPassword('');
        setSearchTerm(''); setSearchResults([]); setAuthError('');
        setSearchError(''); setGeneralStatus(null); setRoles([]);
        setNewBookData(initialNewBookData); setNewUserData(initialNewUserData);
        setAdminView('none');
        setAdminUserSearchTerm(''); setAdminUserSearchResults([]); setAdminUserSearchError('');
        // Limpa os dashboards
        setUserDashboard(initialUserDashboard);
        setAdminDashboard(initialAdminDashboard);
    };

    // --- Funções de Empréstimo e Busca (sem mudanças) ---
    const handleSearchChange = (e) => {
        const novoTermo = e.target.value;
        setSearchTerm(novoTermo);
        if (novoTermo.length < 3) { setSearchResults([]); setSearchError(''); }
    };
    const handleEmprestarLivro = async (livroId) => {
        setGeneralStatus(null);
        if (!window.confirm("Confirmar empréstimo deste livro?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.post(`${API_URL}/emprestimos`, { livroId }, config);
            setGeneralStatus("Livro emprestado com sucesso!");
            setRefreshId(id => id + 1); // Atualiza livros
            setUserRefreshId(id => id + 1); // Atualiza dashboard do usuário
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };
    const handleDevolverLivro = async (emprestimoId) => {
        setGeneralStatus(null);
        if (!window.confirm("Confirmar devolução deste livro?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.post(`${API_URL}/emprestimos/${emprestimoId}/devolucao`, null, config);
            setGeneralStatus("Livro devolvido com sucesso!");
            setRefreshId(id => id + 1); // Atualiza livros
            setUserRefreshId(id => id + 1); // Atualiza dashboard do usuário
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };

    // --- Funções de Admin (sem mudanças) ---
    const handleDeleteLivro = async (livroId) => {
        setGeneralStatus(null);
        if (!window.confirm("ATENÇÃO: Excluir este livro permanentemente?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.delete(`${API_URL}/livros/${livroId}`, config);
            setGeneralStatus("Livro excluído com sucesso!");
            setRefreshId(id => id + 1);
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };
    const handleEditClick = (livro) => {
        setEditingBook(livro);
        setAdminFormData({
            titulo: livro.titulo, autor: livro.autor, isbn: livro.isbn,
            quantidadeDisponivel: livro.quantidadeDisponivel
        });
    };
    const handleModalClose = () => { setEditingBook(null); setGeneralStatus(null); };
    const handleAdminFormChange = (e) => {
        const { name, value } = e.target;
        setAdminFormData(prev => ({ ...prev, [name]: name === 'quantidadeDisponivel' ? (parseInt(value) || 0) : value }));
    };
    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = { ...adminFormData, id: editingBook.id };
            await axios.put(`${API_URL}/livros/${editingBook.id}`, body, config);
            setGeneralStatus("Livro atualizado com sucesso!");
            setRefreshId(id => id + 1);
            handleModalClose();
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };
    const handleNewBookChange = (e) => {
        const { name, value } = e.target;
        setNewBookData(prev => ({ ...prev, [name]: name === 'quantidadeDisponivel' ? (parseInt(value) || 0) : value }));
    };
    const handleNewBookSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.post(`${API_URL}/livros`, { ...newBookData }, config);
            setGeneralStatus("Livro criado com sucesso!");
            setNewBookData(initialNewBookData);
            setRefreshId(id => id + 1);
            setAdminView('none');
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };
    const handleNewUserChange = (e) => {
        const { name, value } = e.target;
        setNewUserData(prev => ({ ...prev, [name]: value }));
    };
    const handleNewUserSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        if (!newUserData.role) { setGeneralStatus("Erro: O usuário deve ter um papel."); return; }
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = { ...newUserData, roles: [newUserData.role] };
            await axios.post(`${API_URL}/admin/usuarios`, body, config);
            setGeneralStatus("Usuário criado com sucesso!");
            setNewUserData(initialNewUserData);
            setAdminView('none');
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };
    const handleAdminUserSearchChange = (e) => { setAdminUserSearchTerm(e.target.value); };
    const handleDeleteUser = async (userId) => {
        setGeneralStatus(null);
        if (!window.confirm("ATENÇÃO: Excluir este usuário permanentemente?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.delete(`${API_URL}/admin/usuarios/${userId}`, config);
            setGeneralStatus("Usuário excluído com sucesso!");
            setUserRefreshId(id => id + 1);
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };
    const handleEditUserClick = (user) => {
        setEditingUser(user);
        setAdminUserFormData({ nomeCompleto: user.nomeCompleto, roles: new Set(user.roles) });
    };
    const handleUserModalClose = () => { setEditingUser(null); setGeneralStatus(null); };
    const handleUserFormChange = (e) => {
        const { name, value } = e.target;
        setAdminUserFormData(prev => ({ ...prev, [name]: value }));
    };
    const handleUserRoleChange = (e) => {
        const { value, checked } = e.target;
        setAdminUserFormData(prev => {
            const newRoles = new Set(prev.roles);
            if (checked) newRoles.add(value); else newRoles.delete(value);
            return { ...prev, roles: newRoles };
        });
    };
    const handleUpdateUserSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        if (adminUserFormData.roles.size === 0) { setGeneralStatus("Erro: O usuário deve ter um papel."); return; }
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = { ...adminUserFormData, roles: Array.from(adminUserFormData.roles) };
            await axios.put(`${API_URL}/admin/usuarios/${editingUser.id}`, body, config);
            setGeneralStatus("Usuário atualizado com sucesso!");
            setUserRefreshId(id => id + 1);
            handleUserModalClose();
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };

    // --- Funções de Mudar Senha (sem mudanças) ---
    const handleOpenPasswordModal = () => { setIsPasswordModalOpen(true); setGeneralStatus(null); };
    const handleClosePasswordModal = () => { setIsPasswordModalOpen(false); setPasswordFormData(initialPasswordFormData); };
    const handlePasswordFormChange = (e) => {
        const { name, value } = e.target;
        setPasswordFormData(prev => ({ ...prev, [name]: value }));
    };
    const handlePasswordSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        if (passwordFormData.newPassword !== passwordFormData.confirmPassword) {
            setGeneralStatus("Erro: As novas senhas não coincidem."); return;
        }
        if (passwordFormData.newPassword.length < 6) {
            setGeneralStatus("Erro: Nova senha deve ter no mínimo 6 caracteres."); return;
        }
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = { currentPassword: passwordFormData.currentPassword, newPassword: passwordFormData.newPassword };
            await axios.post(`${API_URL}/profile/change-password`, body, config);
            setGeneralStatus("Senha alterada com sucesso! Faça o login novamente.");
            handleClosePasswordModal();
            handleLogout();
        } catch (err) { const msg = err.response?.data?.erro || "Erro."; setGeneralStatus(`Erro: ${msg}`); }
    };

    // --- 3. NOVA FUNÇÃO (Abrir Modal de Stats do Admin) ---
    const handleOpenAdminStats = () => {
        setGeneralStatus(null);
        setIsAdminDashboardOpen(true);
        // (O useEffect abaixo cuidará de buscar os dados)
    };
    const handleCloseAdminStats = () => {
        setIsAdminDashboardOpen(false);
    };


    // --- Efeitos (Observadores) ---

    // Efeito 1: Busca Livros (sem mudanças)
    useEffect(() => {
        const fetchLivros = async () => {
            try {
                const config = { headers: { 'Authorization': `Bearer ${token}` } };
                const response = await axios.get(`${API_URL}/livros/search?q=${searchTerm}`, config);
                setSearchResults(response.data);
                setSearchError('');
            } catch (err) { setSearchError('Erro ao buscar livros.'); setSearchResults([]); }
        };
        if (searchTerm.length >= 3 && token) { fetchLivros(); }
    }, [searchTerm, token, refreshId]);

    // Efeito 2: Busca Usuários (Admin) (sem mudanças)
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const config = { headers: { 'Authorization': `Bearer ${token}` } };
                const response = await axios.get(`${API_URL}/admin/usuarios/search?q=${adminUserSearchTerm}`, config);
                setAdminUserSearchResults(response.data);
                setAdminUserSearchError('');
            } catch (err) { setAdminUserSearchError('Erro ao buscar usuários.'); setAdminUserSearchResults([]); }
        };
        if (isAdmin && adminUserSearchTerm.length >= 3 && token) { fetchUsers(); }
        else { setAdminUserSearchResults([]); }
    }, [adminUserSearchTerm, token, userRefreshId, isAdmin]);

    // --- 4. NOVO EFEITO: Busca Dashboard do Usuário (Roda ao logar) ---
    useEffect(() => {
        const fetchUserDashboard = async () => {
            if (!token) return; // Só roda se tiver token
            try {
                const config = { headers: { 'Authorization': `Bearer ${token}` } };
                const response = await axios.get(`${API_URL}/profile/dashboard`, config);
                setUserDashboard(response.data);
            } catch (err) {
                console.error("Erro ao buscar dashboard do usuário:", err);
            }
        };
        fetchUserDashboard();
        // Assiste 'userRefreshId' para atualizar após empréstimo/devolução
    }, [token, userRefreshId]);

    // --- 5. NOVO EFEITO: Busca Dashboard do Admin (Roda quando o modal abre) ---
    useEffect(() => {
        const fetchAdminDashboard = async () => {
            if (token && isAdmin && isAdminDashboardOpen) {
                try {
                    const config = { headers: { 'Authorization': `Bearer ${token}` } };
                    const response = await axios.get(`${API_URL}/admin/dashboard/stats`, config);
                    setAdminDashboard(response.data);
                } catch (err) {
                    console.error("Erro ao buscar stats do admin:", err);
                    setGeneralStatus("Erro ao carregar estatísticas.");
                }
            }
        };
        fetchAdminDashboard();
    }, [token, isAdmin, isAdminDashboardOpen]); // Roda quando o modal abre


    // --- RENDERIZAÇÃO ---

    if (!token) {
        // ... (Página de Login - sem mudanças) ...
        return (
            <div className="wrapper">
                <div className="card">
                    <h1>Começe sua Leitura</h1>
                    <form onSubmit={handleSubmitLogin}>
                        <div className="input-group">
                            <label htmlFor="username">Usuário:</label>
                            <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                        </div>
                        <div className="input-group">
                            <label htmlFor="password">Senha:</label>
                            <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                        </div>
                        <button type="submit" className="button-primary">Entrar</button>
                        {authError && <p className="error-message">{authError}</p>}
                    </form>
                </div>
            </div>
        );
    }

    // === PÁGINA LOGADA ===
    return (
        <div className="wrapper">
            <div className="card logged-in">

                <header className="app-header">
                    <h2>Biblioteca</h2>
                    <div className="user-session">
                        <span className="user-info">Olá, {userNome}! {isAdmin && "(Admin)"}</span>
                        <div className="user-session-buttons">
                            <button onClick={handleOpenPasswordModal} className="button-changepw">
                                Mudar senha
                            </button>
                            <button onClick={handleLogout} className="button-logout">
                                Sair
                            </button>
                        </div>
                    </div>
                </header>

                {generalStatus && (
                    <p style={{ textAlign: 'center', margin: '10px 0', fontWeight: 'bold' }}>
                        {generalStatus}
                    </p>
                )}
                <hr />

                <div className="main-content-area">

                    <div className="search-and-results">

                        {/* --- 6. NOVO DASHBOARD DO USUÁRIO --- */}
                        <div className="user-dashboard">
                            <h4>Meus Empréstimos Ativos</h4>
                            {userDashboard.emprestimosAtivos.length === 0 ? (
                                <p className="empty-message">Você não possui livros emprestados no momento.</p>
                            ) : (
                                <ul className="results-list">
                                    {userDashboard.emprestimosAtivos.map(emp => (
                                        <li key={emp.id}>
                                            <div>
                                                <strong>{emp.livroTitulo}</strong>
                                                <span>Devolver até: {emp.dataDevolucaoPrevista}</span>
                                            </div>
                                            <button
                                                className="button-devolver"
                                                onClick={() => handleDevolverLivro(emp.id)}
                                            >
                                                Devolver
                                            </button>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                        {/* --- FIM DO DASHBOARD DO USUÁRIO --- */}

                        <h3>Pesquisar Acervo</h3>
                        <div className="search-bar">
                            <input type="text" placeholder="Digite o título ou autor (3+ letras)..."
                                   value={searchTerm} onChange={handleSearchChange} />
                        </div>
                        {searchError && <p className="error-message">{searchError}</p>}

                        <ul className="results-list">
                            {searchResults.map((livro) => (
                                <li key={livro.id}>
                                    <div>
                                        <strong>{livro.titulo}</strong> (Autor: {livro.autor})
                                        <span>{livro.quantidadeDisponivel} em estoque</span>
                                    </div>
                                    <div className="button-group">
                                        {/* Lógica de Empréstimo/Devolução */}
                                        {livro.emprestimoId ? (
                                            <button className="button-devolver" onClick={() => handleDevolverLivro(livro.emprestimoId)}>Devolver</button>
                                        ) : (
                                            livro.quantidadeDisponivel > 0 && (
                                                <button className="button-emprestar" onClick={() => handleEmprestarLivro(livro.id)}>Emprestar</button>
                                            )
                                        )}
                                        {/* Botões de Admin */}
                                        {isAdmin && (
                                            <>
                                                <button className="button-editar" onClick={() => handleEditClick(livro)}>Editar</button>
                                                <button className="button-excluir" onClick={() => handleDeleteLivro(livro.id)}>Excluir</button>
                                            </>
                                        )}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {isAdmin && (
                        <div className="admin-panel">
                            <h3>Painel do Administrador</h3>

                            {adminView === 'none' && (
                                <div className="admin-forms">
                                    <button onClick={() => setAdminView('addBook')} className="button-primary">
                                        Inserir Novo Livro
                                    </button>
                                    <button onClick={() => setAdminView('addUser')} className="button-primary">
                                        Inserir Novo Usuário
                                    </button>
                                    <button onClick={() => setAdminView('manageUsers')} className="button-primary">
                                        Gerenciar Usuários
                                    </button>
                                    {/* --- 7. NOVO BOTÃO DE STATS --- */}
                                    <button onClick={handleOpenAdminStats} className="button-primary">
                                        Ver Estatísticas
                                    </button>
                                </div>
                            )}

                            {/* (Views de 'addBook', 'addUser', 'manageUsers' - sem mudanças) */}
                            {adminView === 'addBook' && (
                                <form onSubmit={handleNewBookSubmit} className="form-section">
                                    <button onClick={() => setAdminView('none')} className="button-return">&larr; Voltar</button>
                                    <h4>Adicionar Novo Livro</h4>
                                    <div className="input-group"><label htmlFor="titulo">Título:</label><input id="titulo" name="titulo" type="text" value={newBookData.titulo} onChange={handleNewBookChange} required /></div>
                                    <div className="input-group"><label htmlFor="autor">Autor:</label><input id="autor" name="autor" type="text" value={newBookData.autor} onChange={handleNewBookChange} required /></div>
                                    <div className="input-group"><label htmlFor="isbn">ISBN:</label><input id="isbn" name="isbn" type="text" value={newBookData.isbn} onChange={handleNewBookChange} required /></div>
                                    <div className="input-group"><label htmlFor="quantidadeDisponivel">Quantidade:</label><input id="quantidadeDisponivel" name="quantidadeDisponivel" type="number" min="1" value={newBookData.quantidadeDisponivel} onChange={handleNewBookChange} required /></div>
                                    <button type="submit" className="button-primary">Adicionar Livro</button>
                                </form>
                            )}
                            {adminView === 'addUser' && (
                                <form onSubmit={handleNewUserSubmit} className="form-section">
                                    <button onClick={() => setAdminView('none')} className="button-return">&larr; Voltar</button>
                                    <h4>Adicionar Novo Usuário</h4>
                                    <div className="input-group"><label htmlFor="username">Usuário (login):</label><input id="username" name="username" type="text" value={newUserData.username} onChange={handleNewUserChange} required /></div>
                                    <div className="input-group"><label htmlFor="password">Senha:</label><input id="password" name="password" type="password" value={newUserData.password} onChange={handleNewUserChange} required /></div>
                                    <div className="input-group"><label htmlFor="nomeCompleto">Nome Completo:</label><input id="nomeCompleto" name="nomeCompleto" type="text" value={newUserData.nomeCompleto} onChange={handleNewUserChange} required /></div>
                                    <div className="input-group"><label>Papel:</label><div className="role-radio-group"><label><input type="radio" value="ROLE_COMUM" name="role" onChange={handleNewUserChange} checked={newUserData.role === 'ROLE_COMUM'} /> Comum</label><label><input type="radio" value="ROLE_ADMIN" name="role" onChange={handleNewUserChange} checked={newUserData.role === 'ROLE_ADMIN'} /> Admin</label></div></div>
                                    <button type="submit" className="button-primary">Adicionar Usuário</button>
                                </form>
                            )}
                            {adminView === 'manageUsers' && (
                                <div className="form-section">
                                    <button onClick={() => setAdminView('none')} className="button-return">&larr; Voltar</button>
                                    <h4>Gerenciar Usuários</h4>
                                    <div className="search-bar"><input type="text" placeholder="Buscar usuário por username (3+ letras)..." value={adminUserSearchTerm} onChange={handleAdminUserSearchChange} /></div>
                                    {adminUserSearchError && <p className="error-message">{adminUserSearchError}</p>}
                                    <ul className="admin-user-list">
                                        {adminUserSearchResults.map((user) => (
                                            <li key={user.id}>
                                                <div className="user-details">
                                                    <strong>{user.nomeCompleto}</strong>
                                                    <span>@{user.username} (Papéis: {user.roles.join(', ')})</span>
                                                </div>
                                                <div className="button-group">
                                                    <button className="button-editar" onClick={() => handleEditUserClick(user)}>Editar</button>
                                                    <button className="button-excluir" onClick={() => handleDeleteUser(user.id)}>Excluir</button>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>

            {/* --- MODAL DE EDIÇÃO (LIVRO) --- */}
            {editingBook && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h3>Editando: {editingBook.titulo}</h3>
                            <button onClick={handleModalClose} className="modal-close-button">&times;</button>
                        </div>
                        <form className="modal-body" onSubmit={handleUpdateSubmit}>
                            <div className="input-group"><label htmlFor="titulo">Título:</label><input id="titulo" name="titulo" type="text" value={adminFormData.titulo} onChange={handleAdminFormChange} /></div>
                            <div className="input-group"><label htmlFor="autor">Autor:</label><input id="autor" name="autor" type="text" value={adminFormData.autor} onChange={handleAdminFormChange} /></div>
                            <div className="input-group"><label htmlFor="isbn">ISBN:</label><input id="isbn" name="isbn" type="text" value={adminFormData.isbn} onChange={handleAdminFormChange} /></div>
                            <div className="input-group"><label htmlFor="quantidadeDisponivel">Quantidade:</label><input id="quantidadeDisponivel" name="quantidadeDisponivel" type="number" min="1" value={adminFormData.quantidadeDisponivel} onChange={handleAdminFormChange} /></div>
                            <button type="submit" className="button-primary">Salvar Alterações</button>
                        </form>
                    </div>
                </div>
            )}

            {/* --- MODAL DE EDIÇÃO (USUÁRIO) --- */}
            {editingUser && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h3>Editando: {editingUser.nomeCompleto}</h3>
                            <button onClick={handleUserModalClose} className="modal-close-button">&times;</button>
                        </div>
                        <form className="modal-body" onSubmit={handleUpdateUserSubmit}>
                            <div className="input-group"><label htmlFor="nomeCompleto">Nome Completo:</label><input id="nomeCompleto" name="nomeCompleto" type="text" value={adminUserFormData.nomeCompleto} onChange={handleUserFormChange} /></div>
                            <div className="input-group">
                                <label>Papéis (Roles):</label>
                                <div className="role-checkboxes">
                                    <label><input type="checkbox" value="ROLE_COMUM" onChange={handleUserRoleChange} checked={adminUserFormData.roles.has('ROLE_COMUM')} /> Comum</label>
                                    <label><input type="checkbox" value="ROLE_ADMIN" onChange={handleUserRoleChange} checked={adminUserFormData.roles.has('ROLE_ADMIN')} /> Admin</label>
                                </div>
                            </div>
                            <button type="submit" className="button-primary">Salvar Alterações</button>
                        </form>
                    </div>
                </div>
            )}

            {/* --- MODAL MUDAR SENHA --- */}
            {isPasswordModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h3>Mudar Senha</h3>
                            <button onClick={handleClosePasswordModal} className="modal-close-button">&times;</button>
                        </div>
                        <form className="modal-body" onSubmit={handlePasswordSubmit}>
                            <div className="input-group"><label htmlFor="currentPassword">Senha Atual:</label><input id="currentPassword" name="currentPassword" type="password" value={passwordFormData.currentPassword} onChange={handlePasswordFormChange} required /></div>
                            <div className="input-group"><label htmlFor="newPassword">Nova Senha (mín. 6 caracteres):</label><input id="newPassword" name="newPassword" type="password" value={passwordFormData.newPassword} onChange={handlePasswordFormChange} required /></div>
                            <div className="input-group"><label htmlFor="confirmPassword">Confirmar Nova Senha:</label><input id="confirmPassword" name="confirmPassword" type="password" value={passwordFormData.confirmPassword} onChange={handlePasswordFormChange} required /></div>
                            <button type="submit" className="button-primary">Salvar Nova Senha</button>
                        </form>
                    </div>
                </div>
            )}

            {/* --- 8. NOVO MODAL (STATS DO ADMIN) --- */}
            {isAdminDashboardOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h3>Estatísticas Gerais</h3>
                            <button onClick={handleCloseAdminStats} className="modal-close-button">&times;</button>
                        </div>
                        <div className="modal-body">
                            <div className="stats-grid">
                                <div className="stat-item">
                                    <div className="stat-value">{adminDashboard.totalUsuarios}</div>
                                    <div className="stat-label">Usuários Totais</div>
                                </div>
                                <div className="stat-item">
                                    <div className="stat-value">{adminDashboard.totalLivros}</div>
                                    <div className="stat-label">Títulos na Biblioteca</div>
                                </div>
                                <div className="stat-item">
                                    <div className="stat-value">{adminDashboard.totalEmprestimosAtivos}</div>
                                    <div className="stat-label">Empréstimos Ativos</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}

export default App;