insert into historicosituacaopessoa (id, pessoa_id, situacaoCadastralPessoa, dataSituacao)
select hibernate_sequence.nextval, pes.id, pes.situacaocadastralpessoa, pes.dataregistro from pessoa pes where pes.dataregistro is not null
