update eventoredesim set cnpj = SUBSTR(descricao, length(trim(descricao))-14, 14) where descricao not like '%-%' or not descricao like '%/%';
