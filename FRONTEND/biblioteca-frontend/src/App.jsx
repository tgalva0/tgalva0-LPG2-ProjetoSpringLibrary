import { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API_URL = 'http://localhost:8080/api';

// --- CORREÇÃO 2 (A): Estado inicial do livro com quantidade 1 ---
const initialNewBookData = { titulo: '', autor: '', isbn: '', quantidadeDisponivel: 1 };
const initialNewUserData = { username: '', password: '', nomeCompleto: '', role: 'ROLE_COMUM' };

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
    const [editingBook, setEditingBook] = useState(null);
    const [adminFormData, setAdminFormData] = useState({ titulo: '', autor: '', isbn: '', quantidadeDisponivel: 0 });

    const [newBookData, setNewBookData] = useState(initialNewBookData);
    const [newUserData, setNewUserData] = useState(initialNewUserData);

    const [adminView, setAdminView] = useState('none');

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
        } catch (err) {
            setAuthError('Usuário ou senha inválidos.');
            setToken(null);
            setUserNome('');
            setRoles([]);
        }
    };

    const handleLogout = () => {
        setToken(null); setUserNome(''); setUsername(''); setPassword('');
        setSearchTerm(''); setSearchResults([]); setAuthError('');
        setSearchError(''); setGeneralStatus(null); setRoles([]);
        setNewBookData(initialNewBookData); setNewUserData(initialNewUserData);
        setAdminView('none');
    };

    // --- Função: Atualizar Barra de Busca ---
    const handleSearchChange = (e) => {
        const novoTermo = e.target.value;
        setSearchTerm(novoTermo);
        if (novoTermo.length < 3) {
            setSearchResults([]);
            setSearchError('');
        }
    };

    // --- Funções de Empréstimo (sem mudanças) ---
    const handleEmprestarLivro = async (livroId) => {
        setGeneralStatus(null);
        if (!window.confirm("Confirmar empréstimo deste livro?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = { livroId: livroId };
            await axios.post(`${API_URL}/emprestimos`, body, config);
            setGeneralStatus("Livro emprestado com sucesso!");
            setRefreshId(id => id + 1);
        } catch (err) {
            const msg = err.response?.data?.erro || "Erro ao realizar empréstimo.";
            setGeneralStatus(`Erro: ${msg}`);
        }
    };

    const handleDevolverLivro = async (emprestimoId) => {
        setGeneralStatus(null);
        if (!window.confirm("Confirmar devolução deste livro?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.post(`${API_URL}/emprestimos/${emprestimoId}/devolucao`, null, config);
            setGeneralStatus("Livro devolvido com sucesso!");
            setRefreshId(id => id + 1);
        } catch (err) {
            const msg = err.response?.data?.erro || "Erro ao realizar devolução.";
            setGeneralStatus(`Erro: ${msg}`);
        }
    };

    // --- Funções de Admin (Excluir/Editar) ---
    const handleDeleteLivro = async (livroId) => {
        setGeneralStatus(null);
        if (!window.confirm("ATENÇÃO: Excluir este livro permanentemente?")) return;
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            await axios.delete(`${API_URL}/livros/${livroId}`, config);
            setGeneralStatus("Livro excluído com sucesso!");
            setRefreshId(id => id + 1);
        } catch (err) {
            const msg = err.response?.data?.erro || "Erro ao excluir livro.";
            setGeneralStatus(`Erro: ${msg}`);
        }
    };

    const handleEditClick = (livro) => {
        setEditingBook(livro);
        setAdminFormData({
            titulo: livro.titulo,
            autor: livro.autor,
            isbn: livro.isbn,
            quantidadeDisponivel: livro.quantidadeDisponivel
        });
    };

    const handleModalClose = () => { setEditingBook(null); setGeneralStatus(null); };

    const handleAdminFormChange = (e) => {
        const { name, value } = e.target;
        setAdminFormData(prev => ({
            ...prev,
            [name]: name === 'quantidadeDisponivel' ? (parseInt(value) || 0) : value // Deixa 0 ser digitado, o HTML impede < 1
        }));
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
        } catch (err) {
            const msg = err.response?.data?.erro || "Erro ao atualizar livro.";
            setGeneralStatus(`Erro: ${msg}`);
        }
    };

    // --- Funções: CRIAR LIVRO E USUÁRIO ---

    const handleNewBookChange = (e) => {
        const { name, value } = e.target;
        setNewBookData(prev => ({
            ...prev,
            [name]: name === 'quantidadeDisponivel' ? (parseInt(value) || 0) : value // Deixa 0 ser digitado, o HTML impede < 1
        }));
    };

    const handleNewBookSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = { ...newBookData };
            await axios.post(`${API_URL}/livros`, body, config);
            setGeneralStatus("Livro criado com sucesso!");
            setNewBookData(initialNewBookData);
            setRefreshId(id => id + 1);
            setAdminView('none');
        } catch (err) {
            const msg = err.response?.data?.erro || "Erro ao criar livro.";
            setGeneralStatus(`Erro: ${msg}`);
        }
    };

    const handleNewUserChange = (e) => {
        const { name, value } = e.target;
        setNewUserData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleNewUserSubmit = async (e) => {
        e.preventDefault();
        setGeneralStatus(null);
        if (!newUserData.role) {
            setGeneralStatus("Erro: O usuário deve ter um papel.");
            return;
        }
        try {
            const config = { headers: { 'Authorization': `Bearer ${token}` } };
            const body = {
                username: newUserData.username,
                password: newUserData.password,
                nomeCompleto: newUserData.nomeCompleto,
                roles: [newUserData.role]
            };
            await axios.post(`${API_URL}/admin/usuarios`, body, config);
            setGeneralStatus("Usuário criado com sucesso!");
            setNewUserData(initialNewUserData);
            setAdminView('none');
        } catch (err) {
            const msg = err.response?.data?.erro || "Erro ao criar usuário.";
            setGeneralStatus(`Erro: ${msg}`);
        }
    };


    // --- Efeito: Buscar Livros (sem mudanças) ---
    useEffect(() => {
        const fetchLivros = async () => {
            try {
                const config = { headers: { 'Authorization': `Bearer ${token}` } };
                const response = await axios.get(`${API_URL}/livros/search?q=${searchTerm}`, config);
                setSearchResults(response.data);
                setSearchError('');
            } catch (err) {
                setSearchError('Erro ao buscar livros.');
                setSearchResults([]);
            }
        };
        if (searchTerm.length >= 3 && token) {
            fetchLivros();
        }
    }, [searchTerm, token, refreshId]);


    // --- RENDERIZAÇÃO ---

    if (!token) {
        // ... (Página de Login) ...
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
                        <button onClick={handleLogout} className="button-logout">
                            Sair
                        </button>
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
                        <h3>Pesquisar Acervo</h3>
                        <div className="search-bar">
                            <input
                                type="text"
                                placeholder="Digite o título ou autor (3+ letras)..."
                                value={searchTerm}
                                onChange={handleSearchChange}
                            />
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
                                        {livro.emprestimoId ? (
                                            <button className="button-devolver" onClick={() => handleDevolverLivro(livro.emprestimoId)}>
                                                Devolver
                                            </button>
                                        ) : (
                                            livro.quantidadeDisponivel > 0 && (
                                                <button className="button-emprestar" onClick={() => handleEmprestarLivro(livro.id)}>
                                                    Emprestar
                                                </button>
                                            )
                                        )}
                                        {isAdmin && (
                                            <>
                                                <button className="button-editar" onClick={() => handleEditClick(livro)}>
                                                    Editar
                                                </button>
                                                <button className="button-excluir" onClick={() => handleDeleteLivro(livro.id)}>
                                                    Excluir
                                                </button>
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
                                </div>
                            )}

                            {adminView === 'addBook' && (
                                <form onSubmit={handleNewBookSubmit} className="form-section">
                                    <button onClick={() => setAdminView('none')} className="button-return">
                                        &larr; Voltar
                                    </button>
                                    <h4>Adicionar Novo Livro</h4>
                                    <div className="input-group">
                                        <label htmlFor="titulo">Título:</label>
                                        <input id="titulo" name="titulo" type="text" value={newBookData.titulo} onChange={handleNewBookChange} required />
                                    </div>
                                    <div className="input-group">
                                        <label htmlFor="autor">Autor:</label>
                                        <input id="autor" name="autor" type="text" value={newBookData.autor} onChange={handleNewBookChange} required />
                                    </div>
                                    <div className="input-group">
                                        <label htmlFor="isbn">ISBN:</label>
                                        <input id="isbn" name="isbn" type="text" value={newBookData.isbn} onChange={handleNewBookChange} required />
                                    </div>
                                    <div className="input-group">
                                        <label htmlFor="quantidadeDisponivel">Quantidade:</label>
                                        {/* --- CORREÇÃO 2 (B): min="1" --- */}
                                        <input id="quantidadeDisponivel" name="quantidadeDisponivel" type="number" min="1" value={newBookData.quantidadeDisponivel} onChange={handleNewBookChange} required />
                                    </div>
                                    <button type="submit" className="button-primary">Adicionar Livro</button>
                                </form>
                            )}

                            {adminView === 'addUser' && (
                                <form onSubmit={handleNewUserSubmit} className="form-section">
                                    <button onClick={() => setAdminView('none')} className="button-return">
                                        &larr; Voltar
                                    </button>
                                    <h4>Adicionar Novo Usuário</h4>
                                    <div className="input-group">
                                        <label htmlFor="username">Usuário (login):</label>
                                        <input id="username" name="username" type="text" value={newUserData.username} onChange={handleNewUserChange} required />
                                    </div>
                                    <div className="input-group">
                                        <label htmlFor="password">Senha:</label>
                                        <input id="password" name="password" type="password" value={newUserData.password} onChange={handleNewUserChange} required />
                                    </div>
                                    <div className="input-group">
                                        <label htmlFor="nomeCompleto">Nome Completo:</label>
                                        <input id="nomeCompleto" name="nomeCompleto" type="text" value={newUserData.nomeCompleto} onChange={handleNewUserChange} required />
                                    </div>
                                    <div className="input-group">
                                        <label>Papel:</label>
                                        <div className="role-radio-group">
                                            <label>
                                                <input type="radio" value="ROLE_COMUM" name="role" onChange={handleNewUserChange} checked={newUserData.role === 'ROLE_COMUM'} />
                                                Comum
                                            </label>
                                            <label>
                                                <input type="radio" value="ROLE_ADMIN" name="role" onChange={handleNewUserChange} checked={newUserData.role === 'ROLE_ADMIN'} />
                                                Admin
                                            </label>
                                        </div>
                                    </div>
                                    <button type="submit" className="button-primary">Adicionar Usuário</button>
                                </form>
                            )}
                        </div>
                    )}
                </div>
            </div>

            {/* --- MODAL DE EDIÇÃO --- */}
            {editingBook && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h3>Editando: {editingBook.titulo}</h3>
                            <button onClick={handleModalClose} className="modal-close-button">&times;</button>
                        </div>

                        <form className="modal-body" onSubmit={handleUpdateSubmit}>
                            <div className="input-group">
                                <label htmlFor="titulo">Título:</label>
                                <input id="titulo" name="titulo" type="text" value={adminFormData.titulo} onChange={handleAdminFormChange} />
                            </div>
                            <div className="input-group">
                                <label htmlFor="autor">Autor:</label>
                                <input id="autor" name="autor" type="text" value={adminFormData.autor} onChange={handleAdminFormChange} />
                            </div>
                            <div className="input-group">
                                <label htmlFor="isbn">ISBN:</label>
                                <input id="isbn" name="isbn" type="text" value={adminFormData.isbn} onChange={handleAdminFormChange} />
                            </div>
                            <div className="input-group">
                                <label htmlFor="quantidadeDisponivel">Quantidade:</label>
                                {/* --- CORREÇÃO 3: min="1" --- */}
                                <input id="quantidadeDisponivel" name="quantidadeDisponivel" type="number" min="1" value={adminFormData.quantidadeDisponivel} onChange={handleAdminFormChange} />
                            </div>
                            <button type="submit" className="button-primary">Salvar Alterações</button>
                        </form>
                    </div>
                </div>
            )}

        </div>
    );
}

export default App;