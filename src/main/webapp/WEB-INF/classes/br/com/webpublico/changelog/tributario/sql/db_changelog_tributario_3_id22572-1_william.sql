insert into HistoricoImpressaoDAM (id, dam_id, usuariosistema_id, dataoperacao, tipoimpressao, parcela_id)
select hibernate_sequence.nextVal,	itemdam.DAM_ID ,null, sysdate, 'WEBSERVICE', pvd.id from parcelavalordivida pvd
inner join valordivida vd on pvd.VALORDIVIDA_ID = vd.id
inner join CALCULONFSE nfse on vd.CALCULO_ID = nfse.id
inner join itemdam on itemdam.PARCELA_ID = pvd.id
where not exists (select id from HistoricoImpressaoDAM historico where historico.parcela_id = pvd.id)
