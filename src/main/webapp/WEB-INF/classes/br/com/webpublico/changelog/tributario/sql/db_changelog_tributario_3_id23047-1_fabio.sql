update documentooficial set CONTEUDO = replace(CONTEUDO, '<img src="img/assinaturasuperintendente.gif" width="110" height="110">', '<img src="/arquivos?id=675795497">')
where documentooficial.id in (
select doc.id from documentooficial doc
inner join TERMORBTRANS termo on termo.DOCUMENTOOFICIAL_ID = doc.id
where doc.CONTEUDO like '%<img src="img/assinaturasuperintendente.gif" width="110" height="110">%'
and termo.DATATERMO >= to_date('01/01/2017','dd/MM/yyyy'));
