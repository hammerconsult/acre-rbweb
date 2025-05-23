update programappa ap set datacadastro = to_date('31/12/2019', 'dd/mm/yyyy') where trunc(datacadastro) >= to_date('01/01/2020', 'dd/mm/yyyy')
