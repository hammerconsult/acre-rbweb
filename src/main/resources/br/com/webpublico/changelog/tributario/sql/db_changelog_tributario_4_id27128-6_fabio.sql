insert into historicosituacaopessoa (id, pessoa_id, situacaoCadastralPessoa, dataSituacao)
select hibernate_sequence.nextval, id, situacaocadastralpessoa, data from (
select pes.id, pes.situacaocadastralpessoa, max(rev.datahora) as data from pessoa pes
inner join pessoa_aud aud on aud.id = pes.id
inner join revisaoauditoria rev on rev.id = aud.rev
where pes.dataregistro is null
group by pes.id, pes.situacaocadastralpessoa)
