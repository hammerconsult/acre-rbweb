update eventoredesim set cnpj = SUBSTR(descricao, length(trim(descricao))-18, 18) where descricao like '%-%' and descricao like '%/%'
