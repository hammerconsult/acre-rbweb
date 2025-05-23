update PESSOAFISICA set ESTADOCIVIL = 'DIVORCIADO' where id = 185376342;
update CERTIDAOCASAMENTO certidao set DATAAVERBACAO = to_date('29/10/2019', 'dd/MM/yyyy') where certidao.ID in (select c.ID from DOCUMENTOPESSOAL doc inner join CERTIDAOCASAMENTO c on doc.ID = c.id where doc.PESSOAFISICA_ID = 185376342);
