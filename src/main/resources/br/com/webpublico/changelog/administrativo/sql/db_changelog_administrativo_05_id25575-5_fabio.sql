update PECAOBJETOFROTA peca set peca.descricao = (
select mat.descricao from PECAOBJETOFROTA pecaMat
inner join material mat on mat.id = pecaMat.material_id
where peca.id = pecaMat.id)
where peca.descricao is null;

update MANUTENCAOOBJLUBRIFICACAO lub set lub.lubrificante = (
select mat.descricao from MANUTENCAOOBJLUBRIFICACAO lubMat
inner join material mat on mat.id = lubMat.material_id
where lub.id = lubMat.id)
where lub.lubrificante is null;
