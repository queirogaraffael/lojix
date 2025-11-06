export const mockPromocoes = [
  {
    id: 'p1',
    nome: 'Promo de Lançamento',
    taxaDeDesconto: 0.1, // 10%
  },
  {
    id: 'p2',
    nome: 'Queima de Estoque',
    taxaDeDesconto: 0.4, // 40%
  },
];

export const mockProdutos = [
  {
    id: 'prod1',
    nome: 'Refrigerante 2L',
    precoAtual: 8.0,
    tipo: 'Bebidas',
    descricao: 'Refrigerante sabor cola.',
    dataValidade: '2025-12-31',
    promocaoId: 'p1', // Associado à promo de 10%
  },
  {
    id: 'prod2',
    nome: 'Sabão em Pó 1Kg',
    precoAtual: 15.5,
    tipo: 'Limpeza',
    descricao: 'Sabão para roupas brancas.',
    dataValidade: '2026-10-20',
    promocaoId: null, // Sem promoção
  },
  {
    id: 'prod3',
    nome: 'Pão de Forma',
    precoAtual: 6.5,
    tipo: 'Padaria',
    descricao: 'Pão integral.',
    dataValidade: '2025-11-15',
    promocaoId: 'p2', // Associado à promo de 40%
  },
];

export const mockFuncionarios = [
  {
    id: 'f1',
    nome: 'Adminstrador Silva',
    email: 'admin@supergestor.com',
    cpf: '111.111.111-11',
    foto: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png',
  },
  {
    id: 'f2',
    nome: 'Funcionário Oliveira',
    email: 'func@supergestor.com',
    cpf: '222.222.222-22',
    foto: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png',
  },
];

export const mockClientes = [];