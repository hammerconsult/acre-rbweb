create or replace procedure proc_popular_documento_anexos is
    detentor_id number(19);
    ultimo_id number(19);
begin
    --corrige cotacao
    ultimo_id := null;
for registro in (select dacl.COTACAO_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from cotacao c
                              inner join detentorarqcomplicitacao dacl on dacl.COTACAO_ID = c.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where c.detentordocumentolicitacao_id is null
                     order by c.id)
        loop
            if (ultimo_id != registro.COTACAO_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update COTACAO
set detentordocumentolicitacao_id = detentor_id
where id = registro.COTACAO_ID;
end if;

            ultimo_id := registro.COTACAO_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;
    --corrige parecerlicitacao
    ultimo_id := null;
for registro in (select dacl.PARECERLICITACAO_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from PARECERLICITACAO pl
                              inner join detentorarqcomplicitacao dacl on dacl.PARECERLICITACAO_ID = pl.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where pl.detentordocumentolicitacao_id is null
                     order by pl.id)
        loop
            if (ultimo_id != registro.PARECERLICITACAO_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update PARECERLICITACAO
set detentordocumentolicitacao_id = detentor_id
where id = registro.PARECERLICITACAO_ID;
end if;

            ultimo_id := registro.PARECERLICITACAO_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;
    --corrige propostafornecedor
    ultimo_id := null;
for registro in (select dacl.PROPOSTAFORNECEDOR_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from PROPOSTAFORNECEDOR pf
                              inner join detentorarqcomplicitacao dacl on dacl.PROPOSTAFORNECEDOR_ID = pf.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where pf.detentordocumentolicitacao_id is null
                     order by pf.id)
        loop
            if (ultimo_id != registro.PROPOSTAFORNECEDOR_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update PROPOSTAFORNECEDOR
set detentordocumentolicitacao_id = detentor_id
where id = registro.PROPOSTAFORNECEDOR_ID;
end if;

            ultimo_id := registro.PROPOSTAFORNECEDOR_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;

    --corrige formulariocotacao
    ultimo_id := null;
for registro in (select dacl.LOTEFORMULARIOCOTACAO_ID,
                            lote.FORMULARIOCOTACAO_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from LOTEFORMULARIOCOTACAO lote
                              inner join detentorarqcomplicitacao dacl on lote.ID = dacl.LOTEFORMULARIOCOTACAO_ID
                              inner join FORMULARIOCOTACAO fc on lote.FORMULARIOCOTACAO_ID = fc.ID
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where fc.detentordocumentolicitacao_id is null
                     order by lote.id)
        loop
            if (ultimo_id != registro.LOTEFORMULARIOCOTACAO_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update FORMULARIOCOTACAO
set detentordocumentolicitacao_id = detentor_id
where id = registro.FORMULARIOCOTACAO_ID;
end if;

            ultimo_id := registro.LOTEFORMULARIOCOTACAO_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;
end ;
