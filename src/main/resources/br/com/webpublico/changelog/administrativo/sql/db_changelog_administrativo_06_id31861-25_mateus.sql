merge into publicacaoaditivo pub using (
select dataPublicacaoDOE, numeroDOE, paginaDOE, veiculoDePublicacao_id, id from AditivoContrato
where dataPublicacaoDOE is not null or numeroDOE is not null or paginaDOE is not null or veiculoDePublicacao_id is not null
) dados on (dados.id = pub.aditivocontrato_id)
when not matched then insert (id, DATAPUBLICACAO, NUMERO, PAGINA, VEICULODEPUBLICACAO_ID, ADITIVOCONTRATO_ID)
values (hibernate_sequence.nextval, dados.dataPublicacaoDOE, dados.numeroDOE, dados.paginaDOE, dados.veiculoDePublicacao_id, dados.id)
